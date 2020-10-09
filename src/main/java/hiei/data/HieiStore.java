package hiei.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hiei.HieiServer;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class HieiStore {
    private final HieiServer hiei;
    private final String[] files;
    private final String dataDirectory;

    public HieiStore(HieiServer hiei) {
        this.hiei = hiei;
        this.files = new String[]{"ship-version.json", "equipment-version.json", "ships.json", "equipments.json"};
        this.dataDirectory = this.hiei.hieiConfig.directory + "data/";
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

    public String getShipVersionFileName() { return this.files[0]; }

    public String getEquipmentVersionFileName() { return this.files[1]; }

    public String getShipDataFileName() { return this.files[2]; }

    public String getEquipmentDataFileName() { return this.files[3]; }

    public JsonArray getLocalShipsData() {
        Buffer buffer = this.getFileSystem()
                .readFileBlocking(this.dataDirectory + this.getShipDataFileName());
        return new Gson()
                .fromJson(buffer.toString(StandardCharsets.UTF_8.name()), JsonArray.class);
    }

    public JsonArray getLocalEquipmentsData() {
        Buffer buffer = this.getFileSystem()
                .readFileBlocking(this.dataDirectory + this.getEquipmentDataFileName());
        return new Gson()
                .fromJson(buffer.toString(StandardCharsets.UTF_8.name()), JsonArray.class);
    }

    public JsonObject getLocalShipVersion() {
        Buffer buffer = this.getFileSystem()
                .readFileBlocking(this.dataDirectory + this.getShipVersionFileName());
        JsonObject data = new Gson()
                .fromJson(buffer.toString(StandardCharsets.UTF_8.name()), JsonObject.class);
        if (data == null || data.get("version-number") == null) {
           JsonObject json = new JsonObject();
           json.addProperty("version-number", "0");
           return json;
        }
        return data;
    }

    public JsonObject getLocalEquipVersion() {
        Buffer buffer = this.getFileSystem()
                .readFileBlocking(this.dataDirectory + this.getEquipmentVersionFileName());
        JsonObject data = new Gson()
                .fromJson(buffer.toString(StandardCharsets.UTF_8.name()), JsonObject.class);
        if (data == null || data.get("version-number") == null) {
            JsonObject json = new JsonObject();
            json.addProperty("version-number", "0");
            return json;
        }
        return data;
    }

    public void updateShipData() throws ExecutionException, InterruptedException {
        Buffer remoteVersion = Buffer.buffer(this.hiei.hieiUpdater.fetchShipVersionData().get().toString());
        Buffer remoteShips = Buffer.buffer(this.hiei.hieiUpdater.fetchShipData().get().toString());
        this.getFileSystem()
                .writeFileBlocking(this.dataDirectory + this.getShipVersionFileName(), remoteVersion);
        this.getFileSystem()
                .writeFileBlocking(this.dataDirectory + this.getShipDataFileName(), remoteShips);
    }

    public void updateEquipmentData() throws ExecutionException, InterruptedException {
        Buffer remoteVersion = Buffer.buffer(this.hiei.hieiUpdater.fetchEquipmentVersionData().get().toString());
        Buffer remoteEquips = Buffer.buffer(this.hiei.hieiUpdater.fetchEquipmentData().get().toString());
        this.getFileSystem()
                .writeFileBlocking(this.dataDirectory + this.getEquipmentVersionFileName(), remoteVersion);
        this.getFileSystem()
                .writeFileBlocking(this.dataDirectory + this.getEquipmentDataFileName(), remoteEquips);
    }
}
