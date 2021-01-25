package hiei.struct;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class HieiEvent {
    public final String name;
    public final JsonObject data;

    public HieiEvent(JsonObject data) {
        this.name = data.get("name").getAsString();
        this.data = data.deepCopy();
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this.data);
    }
}
