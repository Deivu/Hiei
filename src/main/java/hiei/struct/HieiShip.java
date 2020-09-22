package hiei.struct;

import io.vertx.core.json.JsonObject;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HieiShip {
    public final String nationality;
    public final String shipClass;
    public final String hullType;
    public final String rarity;
    public final String id;
    public final String name;
    public final JsonObject data;

    public HieiShip(JsonObject data) {
        this.nationality = data.getString("nationality");
        this.shipClass = data.getString("class");
        this.hullType = data.getString("hullType");
        this.rarity = data.getString("rarity");
        this.id = data.getString("id");
        this.name = data.getJsonObject("names").getString("en");
        this.data = data.copy();
    }

    @Override
    public String toString() {
        return this.data.toString();
    }
}
