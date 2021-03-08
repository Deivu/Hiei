package hiei.struct;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class HieiKeyChapter {
    public final String chapter;
    public final String name;
    public final CopyOnWriteArrayList<HieiSubChapter> subChapters;
    public final JsonObject data;

    public HieiKeyChapter(String count, JsonObject data) {
        this.chapter = count;
        this.name = data.get("names").getAsJsonObject().get("en").getAsString();
        this.subChapters = new CopyOnWriteArrayList<>();
        List<String> subChapterKeys =  data.keySet().stream().filter(value -> value.chars().allMatch(Character::isDigit)).collect(Collectors.toList());
        for (String key : subChapterKeys) this.subChapters.add(new HieiSubChapter(key, data.get(key).getAsJsonObject()));
        this.data = data.deepCopy();
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this.data);
    }
}
