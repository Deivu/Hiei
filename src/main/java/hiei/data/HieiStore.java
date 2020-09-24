package hiei.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hiei.HieiServer;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

import java.nio.charset.StandardCharsets;

public class HieiStore {
    private final HieiServer hiei;
    private final String[] files;
    private final String dataDirectory;

    private boolean isShipsUpdating;
    private boolean isEquipsUpdating;

    public HieiStore(HieiServer hiei) {
        this.hiei = hiei;
        this.files = new String[]{"ship-version.json", "equipment-version.json", "ships.json", "equipments.json"};
        this.dataDirectory = this.hiei.hieiConfig.directory + "data/";
        this.isEquipsUpdating = false;
        this.isEquipsUpdating = false;
        if (!this.getFileSystem().existsBlocking(this.dataDirectory)) {
            this.getFileSystem().mkdirBlocking(this.dataDirectory);
            this.hiei.hieiLogger.debug("Created '.data/' directory because it doesn't exist");
        }
        for (String fileName : this.files) {
            if (!this.getFileSystem().existsBlocking(this.dataDirectory + fileName)) {
                this.getFileSystem().createFileBlocking(this.dataDirectory + fileName);
                this.hiei.hieiLogger.debug("Created '.data/" + fileName + "' file because it doesn't exist");
            }
        }
    }

    public FileSystem getFileSystem() { return this.hiei.vertx.fileSystem(); }

    public String getDataDirectory() { return this.dataDirectory; }

    public String getShipVersionFileName() { return this.files[0]; }

    public String getEquipmentVersionFileName() { return this.files[1]; }

    public String getShipDataFileName() { return this.files[2]; }

    public String getEquipmentDataFileName() { return this.files[3]; }

    public JsonArray getLocalShipsData() {
        Buffer buffer = this.getFileSystem().readFileBlocking(this.dataDirectory + this.getShipDataFileName());
        return new Gson().fromJson(buffer.toString(StandardCharsets.UTF_8.name()), JsonArray.class);
    }

    public JsonArray getLocalEquipmentsData() {
        Buffer buffer = this.getFileSystem().readFileBlocking(this.dataDirectory + this.getEquipmentDataFileName());
        return new Gson().fromJson(buffer.toString(StandardCharsets.UTF_8.name()), JsonArray.class);
    }

    public void updateShipData() {
        try {
            if (this.isShipsUpdating) {
                this.hiei.hieiLogger.debug("Tried to update ship data, but a thread is already updating it");
                return;
            }
            this.isShipsUpdating = true;
            JsonObject remoteVersion = this.hiei.hieiUpdater.fetchShipVersionData().get();
            JsonArray remoteShips = this.hiei.hieiUpdater.fetchShipData().get();
            this.getFileSystem().writeFileBlocking(this.getDataDirectory() + this.getShipVersionFileName(), Buffer.buffer(remoteVersion.toString()));
            this.getFileSystem().writeFileBlocking(this.getDataDirectory() + this.getShipDataFileName(), Buffer.buffer(remoteShips.toString()));
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
            JsonObject remoteVersion = this.hiei.hieiUpdater.fetchEquipmentVersionData().get();
            JsonArray remoteEquips = this.hiei.hieiUpdater.fetchEquipmentData().get();
            this.getFileSystem().writeFileBlocking(this.getDataDirectory() + this.getEquipmentVersionFileName(), Buffer.buffer(remoteVersion.toString()));
            this.getFileSystem().writeFileBlocking(this.getDataDirectory() + this.getEquipmentDataFileName(), Buffer.buffer(remoteEquips.toString()));
        } catch (Throwable throwable) {
            this.hiei.hieiLogger.error(throwable);
        } finally {
            this.isEquipsUpdating = false;
        }
    }
}
