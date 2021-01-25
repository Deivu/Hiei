package hiei.struct;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class HieiShip {
    public final String nationality;
    public final String shipClass;
    public final String hullType;
    public final String rarity;
    public final String id;
    public final String name;
    public final JsonObject data;

    public HieiShip(JsonObject data) {
        this.nationality = data.get("nationality").getAsString();
        this.shipClass = data.get("class").getAsString();
        this.hullType = data.get("hullType").getAsString();
        this.rarity = data.get("rarity").getAsString();
        this.id = data.get("id").getAsString();
        this.name = data.get("names").getAsJsonObject().get("en").getAsString();
        this.data = data.deepCopy();
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this.data);
    }
}
