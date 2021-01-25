package hiei.struct;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class HieiVoice {
    public final String id;
    public final JsonObject data;

    public HieiVoice(String id, JsonObject data) {
        this.id = id;
        this.data = data.deepCopy();
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this.data);
    }
}
