package hiei.struct;

import com.google.gson.JsonObject;

public class HieiSubChapter {
    public final String subChapter;
    public final String name;
    public final JsonObject data;

    public HieiSubChapter(String count, JsonObject data) {
        this.subChapter = count;
        this.name = data.get("names").getAsJsonObject().get("en").getAsString();
        this.data = data.deepCopy();
    }
}
