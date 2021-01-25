package hiei.struct;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.concurrent.CopyOnWriteArrayList;

public class HieiBarrage {
    public final String name;
    public final CopyOnWriteArrayList<String> ships;
    public final JsonObject data;

    public HieiBarrage(JsonObject data) {
        this.name = data.get("name").getAsString();
        this.ships = new CopyOnWriteArrayList<>();
        JsonArray ships = data.get("ships").getAsJsonArray();
        for (int i = 0; i < ships.size(); i++) this.ships.add(ships.get(i).getAsString());
        this.data = data.deepCopy();
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this.data);
    }
}
