package hiei.struct;

import com.google.gson.JsonObject;

import java.util.concurrent.CopyOnWriteArrayList;

public class HieiKeyChapter {
    public final String chapter;
    public final String name;
    public final CopyOnWriteArrayList<HieiSubChapter> subChapters;
    public final JsonObject data;

    public HieiKeyChapter(String count, JsonObject data) {
        this.chapter = count;
        this.name = data.get("names").getAsJsonObject().get("en").getAsString();
        this.subChapters = new CopyOnWriteArrayList<>();
        for (String key : data.keySet()) {
            if (key.equals("names")) continue;
            this.subChapters.add(new HieiSubChapter(key, data.get(key).getAsJsonObject()));
        }
        this.data = data.deepCopy();
    }
}
