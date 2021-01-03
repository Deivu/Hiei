package hiei.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        this.files = new String[]{"ship-version.json", "equipment-version.json", "ships.json", "equipments.json", "barrage.json", "chapters.json", "event.json"};
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

    public String getBarrageDataFileName() { return this.files[4]; }

    public String getChaptersDataFileName() { return this.files[5]; }

    public String getEventDataFileName() { return this.files[6]; }

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

    public JsonArray getLocalBarragesData() {
        Buffer buffer = this.getFileSystem()
                .readFileBlocking(this.dataDirectory + this.getBarrageDataFileName());
        return new Gson()
                .fromJson(buffer.toString(StandardCharsets.UTF_8.name()), JsonArray.class);
    }

    public JsonArray getLocalEventsData() {
        Buffer buffer = this.getFileSystem()
                .readFileBlocking(this.dataDirectory + this.getEventDataFileName());
        return new Gson()
                .fromJson(buffer.toString(StandardCharsets.UTF_8.name()), JsonArray.class);
    }
    public JsonArray getLocalChaptersData() {
        Buffer buffer = this.getFileSystem()
                .readFileBlocking(this.dataDirectory + this.getChaptersDataFileName());
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

    public JsonArray updateShipData() throws ExecutionException, InterruptedException {
        JsonObject version = this.hiei.hieiUpdater.fetchShipVersionData().get();
        JsonArray ships = this.hiei.hieiUpdater.fetchShipData().get();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        this.getFileSystem()
                .writeFileBlocking(this.dataDirectory + this.getShipVersionFileName(), Buffer.buffer(gson.toJson(version)));
        this.getFileSystem()
                .writeFileBlocking(this.dataDirectory + this.getShipDataFileName(), Buffer.buffer(gson.toJson(ships)));
        return ships;
    }

    public JsonArray updateEquipmentData() throws ExecutionException, InterruptedException {
        JsonObject version = this.hiei.hieiUpdater.fetchEquipmentVersionData().get();
        JsonArray equips = this.hiei.hieiUpdater.fetchEquipmentData().get();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        this.getFileSystem()
                .writeFileBlocking(this.dataDirectory + this.getEquipmentVersionFileName(), Buffer.buffer(gson.toJson(version)));
        this.getFileSystem()
                .writeFileBlocking(this.dataDirectory + this.getEquipmentDataFileName(), Buffer.buffer(gson.toJson(equips)));
        return equips;
    }

    public JsonArray updateBarrageData() throws ExecutionException, InterruptedException  {
        JsonArray barrages = this.hiei.hieiUpdater.fetchBarrageData().get();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        this.getFileSystem()
                .writeFileBlocking(this.dataDirectory + this.getBarrageDataFileName(), Buffer.buffer(gson.toJson(barrages)));
        return barrages;
    }

    public JsonArray updateChapterData() throws ExecutionException, InterruptedException  {
        JsonArray chapters = this.hiei.hieiUpdater.fetchChaptersData().get();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        this.getFileSystem()
                .writeFileBlocking(this.dataDirectory + this.getChaptersDataFileName(), Buffer.buffer(gson.toJson(chapters)));
        return chapters;
    }

    public JsonArray updateEventData() throws ExecutionException, InterruptedException  {
        JsonArray events = this.hiei.hieiUpdater.fetchEventData().get();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        this.getFileSystem()
                .writeFileBlocking(this.dataDirectory + this.getEventDataFileName(), Buffer.buffer(gson.toJson(events)));
        return events;
    }
}
