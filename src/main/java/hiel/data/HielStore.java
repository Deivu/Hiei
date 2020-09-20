package hiel.data;

import hiel.HielServer;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class HielStore {
    private final HielServer hiel;
    private final String[] files;
    private final String dataDirectory;

    private boolean isShipsUpdating;
    private boolean isEquipsUpdating;

    public HielStore(HielServer hiel) throws IOException {
        this.hiel = hiel;
        this.files = new String[]{"ship-version.json", "equipment-version.json", "ships.json", "equipment.json"};
        this.dataDirectory = this.hiel.hielConfig.directory + "data/";
        this.isEquipsUpdating = false;
        this.isEquipsUpdating = false;

        if (!new File(this.dataDirectory).mkdir())
            this.hiel.hielLogger.debug("Created '.data/' directory because it doesn't exist");
        for (String fileName : this.files)
            if (!new File(this.dataDirectory + fileName).createNewFile())
                this.hiel.hielLogger.debug("Created '.data/" + fileName + "' file because it doesn't exist");
    }

    public String getDataDirectory() { return dataDirectory; }
    public String getShipVersionFileName() { return files[0]; }
    public String getEquipmentVersionFileName() { return files[1]; }
    public String getShipDataFileName() { return files[2]; }
    public String getEquipmentDataFileName() { return files[3]; }

    public JSONArray getLocalShipsData() throws FileNotFoundException {
        InputStream is = new FileInputStream(this.dataDirectory + this.getShipDataFileName());
        return new JSONArray(new JSONTokener(is));
    }

    public JSONArray getLocalEquipmentsData() throws FileNotFoundException {
        InputStream is = new FileInputStream(this.dataDirectory + this.getEquipmentDataFileName());
        return new JSONArray(new JSONTokener(is));
    }

    public void updateShipData() {
        if (this.isShipsUpdating) {
            this.hiel.hielLogger.debug("Tried to update ship data, but a thread is already updating it");
            return;
        }
        this.hiel.cachedThreadPool.execute(() -> {
            try {
                this.isShipsUpdating = true;
                if (this.hiel.hielUpdater.shipDataNeedsUpdate().get()) {
                    File file = new File(this.dataDirectory + this.getShipDataFileName());
                    if (!file.createNewFile()) this.hiel.hielLogger.debug("Created '.data/" + this.getShipDataFileName() + "' file because it doesn't exist");
                    JsonArray remoteShips = this.hiel.hielUpdater.fetchShipData().get();
                    try (FileWriter ships = new FileWriter(file, false)) {
                        ships.write(remoteShips.toString());
                        this.updateShipVersion();
                        ships.flush();
                    }
                }
            } catch (Throwable throwable) {
                this.hiel.hielLogger.error(throwable);
            } finally {
                this.isShipsUpdating = false;
            }
        });
    }

    public void updateEquipmentData() {
        if (this.isEquipsUpdating) {
            this.hiel.hielLogger.debug("Tried to update equipment data, but a thread is already updating it");
            return;
        }
        this.hiel.cachedThreadPool.execute(() -> {
            try {
                this.isEquipsUpdating = true;
                if (this.hiel.hielUpdater.equipmentDataNeedsUpdate().get()) {
                    File file = new File(this.dataDirectory + this.getEquipmentDataFileName());
                    if (!file.createNewFile()) this.hiel.hielLogger.debug("Created '.data/" + this.getEquipmentDataFileName() + "' file because it doesn't exist");
                    JsonArray remoteEquips = this.hiel.hielUpdater.fetchEquipmentData().get();
                    try (FileWriter equips = new FileWriter(file, false)) {
                        equips.write(remoteEquips.toString());
                        this.updateEquipmentVersion();
                        equips.flush();
                    }
                }
            } catch (Throwable throwable) {
                this.hiel.hielLogger.error(throwable);
            } finally {
                this.isEquipsUpdating = false;
            }
        });
    }

    private void updateShipVersion() throws IOException, ExecutionException, InterruptedException {
        JsonObject remoteVersion = this.hiel.hielUpdater.fetchShipVersionData().get();
        try (FileWriter version = new FileWriter( new File(this.dataDirectory + this.getShipVersionFileName()),false)) {
            version.write(remoteVersion.toString());
            version.flush();
        }
    }

    private void updateEquipmentVersion() throws IOException, ExecutionException, InterruptedException {
        JsonObject remoteVersion = this.hiel.hielUpdater.fetchEquipmentVersionData().get();
        try (FileWriter version = new FileWriter( new File(this.dataDirectory + this.getEquipmentVersionFileName()),false)) {
            version.write(remoteVersion.toString());
            version.flush();
        }
    }
}
