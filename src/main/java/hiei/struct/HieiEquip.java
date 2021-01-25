package hiei.struct;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class HieiEquip {
    public final String category;
    public final String nationality;
    public final String name;
    public final JsonObject data;

    public HieiEquip(JsonObject data) {
        this.category = data.get("category").getAsString();
        this.nationality = data.get("nationality").getAsString();
        this.name = data.get("names").getAsJsonObject().get("en").getAsString();
        this.data = data.deepCopy();
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this.data);
    }
}
