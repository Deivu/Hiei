package hiei.struct;

import io.vertx.core.json.JsonObject;

public class HieiEquip {
    public final String category;
    public final String nationality;
    public final String name;
    public final JsonObject data;

    public HieiEquip(JsonObject data) {
        this.category = data.getString("category");
        this.nationality = data.getString("nationality");
        this.name = data.getJsonObject("names").getString("en");
        this.data = data;
    }
}
