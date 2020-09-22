package hiei.data;

import hiei.HieiServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class HieiStore {
    private final HieiServer hiei;
    private final String[] files;
    private final String dataDirectory;

    private boolean isShipsUpdating;
    private boolean isEquipsUpdating;

    public HieiStore(HieiServer hiei) throws IOException {
        this.hiei = hiei;
        this.files = new String[]{"ship-version.json", "equipment-version.json", "ships.json", "equipments.json"};
        this.dataDirectory = this.hiei.hieiConfig.directory + "data/";
        this.isEquipsUpdating = false;
        this.isEquipsUpdating = false;

        if (new File(this.dataDirectory).mkdir())
            this.hiei.hieiLogger.debug("Created '.data/' directory because it doesn't exist");
        for (String fileName : this.files) {
            if (new File(this.dataDirectory + fileName).createNewFile()) {
                try (FileWriter def = new FileWriter(this.dataDirectory + fileName, false)) {
                    if (fileName.equals(this.getShipDataFileName()) || fileName.equals(this.getEquipmentDataFileName())) {
                        def.write("[]");
                    } else {
                        def.write("{}");
                    }
                    def.flush();
                }
                this.hiei.hieiLogger.debug("Created '.data/" + fileName + "' file because it doesn't exist");
            }
        }
    }

    public String getDataDirectory() { return this.dataDirectory; }

    public String getShipVersionFileName() { return this.files[0]; }

    public String getEquipmentVersionFileName() { return this.files[1]; }

    public String getShipDataFileName() { return this.files[2]; }

    public String getEquipmentDataFileName() { return this.files[3]; }

    public JsonArray getLocalShipsData() throws IOException {
        try (InputStream is = new FileInputStream(this.dataDirectory + this.getShipDataFileName())) {
            return new JsonArray(IOUtils.toString(is));
        }
    }

    public JsonArray getLocalEquipmentsData() throws IOException {
        try (InputStream is = new FileInputStream(this.dataDirectory + this.getEquipmentDataFileName())) {
            return new JsonArray(IOUtils.toString(is));
        }
    }

    public void updateShipData() {
        try {
            if (this.isShipsUpdating) {
                this.hiei.hieiLogger.debug("Tried to update ship data, but a thread is already updating it");
                return;
            }
            this.isShipsUpdating = true;
            File file = new File(this.dataDirectory + this.getShipDataFileName());
            if (file.createNewFile()) this.hiei.hieiLogger.debug("Created '.data/" + this.getShipDataFileName() + "' file because it doesn't exist");
            JsonArray remoteShips = this.hiei.hieiUpdater.fetchShipData().get();
            try (FileWriter ships = new FileWriter(file, false)) {
                ships.write(remoteShips.encodePrettily());
                this.updateShipVersion();
                ships.flush();
            }
        } catch (Throwable throwable) {
            this.hiei.hieiLogger.error(throwable);
        } finally {
            this.isShipsUpdating = false;
        }
    }

    public void updateEquipmentData() {
        try {
            if (this.isEquipsUpdating) {
                this.hiei.hieiLogger.debug("Tried to update equipment data, but a thread is already updating it");
                return;
            }
            this.isEquipsUpdating = true;
            File file = new File(this.dataDirectory + this.getEquipmentDataFileName());
            if (file.createNewFile()) this.hiei.hieiLogger.debug("Created '.data/" + this.getEquipmentDataFileName() + "' file because it doesn't exist");
            JsonArray remoteEquips = this.hiei.hieiUpdater.fetchEquipmentData().get();
            try (FileWriter equips = new FileWriter(file, false)) {
                equips.write(remoteEquips.encodePrettily());
                this.updateEquipmentVersion();
                equips.flush();
            }
        } catch (Throwable throwable) {
            this.hiei.hieiLogger.error(throwable);
        } finally {
            this.isEquipsUpdating = false;
        }
    }

    private void updateShipVersion() throws IOException, ExecutionException, InterruptedException {
        JsonObject remoteVersion = this.hiei.hieiUpdater.fetchShipVersionData().get();
        try (FileWriter version = new FileWriter( new File(this.dataDirectory + this.getShipVersionFileName()),false)) {
            version.write(remoteVersion.encodePrettily());
            version.flush();
        }
    }

    private void updateEquipmentVersion() throws IOException, ExecutionException, InterruptedException {
        JsonObject remoteVersion = this.hiei.hieiUpdater.fetchEquipmentVersionData().get();
        try (FileWriter version = new FileWriter( new File(this.dataDirectory + this.getEquipmentVersionFileName()),false)) {
            version.write(remoteVersion.encodePrettily());
            version.flush();
        }
    }
}
