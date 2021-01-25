package hiei.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hiei.HieiServer;
import hiei.struct.*;

import java.util.concurrent.CopyOnWriteArrayList;

public class HieiCache {
    private final HieiServer hiei;
    public final CopyOnWriteArrayList<HieiShip> ships;
    public final CopyOnWriteArrayList<HieiEquip> equips;
    public final CopyOnWriteArrayList<HieiBarrage> barrage;
    public final CopyOnWriteArrayList<HieiEvent> events;
    public final CopyOnWriteArrayList<HieiKeyChapter> chapters;
    public final CopyOnWriteArrayList<HieiVoice> voices;

    public HieiCache(HieiServer hiei) {
        this.hiei = hiei;
        this.ships = new CopyOnWriteArrayList<>();
        this.equips = new CopyOnWriteArrayList<>();
        this.barrage = new CopyOnWriteArrayList<>();
        this.events = new CopyOnWriteArrayList<>();
        this.chapters = new CopyOnWriteArrayList<>();
        this.voices = new CopyOnWriteArrayList<>();
    }

    public void updateShipCache(JsonArray data) {
        if (data.size() == 0) return;
        if (!this.ships.isEmpty()) this.ships.clear();
        for (int i = 0; i < data.size(); i++) {
            JsonObject ship = data.get(i).getAsJsonObject();
            String id = ship.get("id").getAsString();
            String name;
            if (ship.get("names") == null || ship.get("names").getAsJsonObject().get("en") == null) {
                name = ship.get("wikiUrl").getAsString();
            } else {
                name = ship.get("names").getAsJsonObject().get("en").getAsString();
            }
            this.hiei.hieiLogger.debug("Loading Ship; ID => " + id + " | Name => " + name);
            try {
                this.ships.add(new HieiShip(ship));
            } catch (Exception error) {
                this.hiei.hieiLogger.error("Error Loading Ship; ID => " + id + " | Name => " + name, error);
            }
        }
    }

    public void updateEquipCache(JsonArray data) {
        if (data.size() == 0) return;
        if (!this.equips.isEmpty()) this.ships.clear();
        for (int i = 0; i < data.size(); i++) {
            JsonObject equip = data.get(i).getAsJsonObject();
            String name;
            if (equip.get("names") == null || equip.get("names").getAsJsonObject().get("en") == null) {
                name = equip.get("wikiUrl").getAsString();
            } else {
                name = equip.get("names").getAsJsonObject().get("en").getAsString();
            }
            this.hiei.hieiLogger.debug("Loading Equip; Name => " + name);
            try {
                this.equips.add(new HieiEquip(equip));
            } catch (Exception error) {
                this.hiei.hieiLogger.error("Error Loading Equip; Name => " + name, error);
            }
        }
    }

    public void updateBarrageCache(JsonArray data) {
        if (data.size() == 0) return;
        if (!this.barrage.isEmpty()) this.barrage.clear();
        for (int i = 0; i < data.size(); i++) {
            JsonObject barrage = data.get(i).getAsJsonObject();
            String name;
            if (barrage.get("name") == null) {
                name = barrage.toString();
            } else {
                name = barrage.get("name").toString();
            }
            this.hiei.hieiLogger.debug("Loading Barrage; Name => " + name);
            try {
                this.barrage.add(new HieiBarrage(barrage));
            } catch (Exception error) {
                this.hiei.hieiLogger.error("Error Loading Barrage; Name => " + name, error);
            }
        }
    }

    public void updateEventCache(JsonArray data) {
        if (data.size() == 0) return;
        if (!this.events.isEmpty()) this.events.clear();
        for (int i = 0; i < data.size(); i++) {
            JsonObject event = data.get(i).getAsJsonObject();
            String name;
            if (event.get("name") == null) {
                name = event.toString();
            } else {
                name = event.get("name").toString();
            }
            this.hiei.hieiLogger.debug("Loading Event; Name => " + name);
            try {
                this.events.add(new HieiEvent(event));
            } catch (Exception error) {
                this.hiei.hieiLogger.error("Error Loading Event; Name => " + name, error);
            }
        }
    }

    public void updateChapterCache(JsonArray data) {
        if (data.size() == 0) return;
        if (!this.chapters.isEmpty()) this.chapters.clear();
        for (int i = 0; i < data.size(); i++) {
            JsonObject chapter = data.get(i).getAsJsonObject();
            this.hiei.hieiLogger.debug("Loading Key Chapter; Number => " + (i + 1));
            try {
                this.chapters.add(new HieiKeyChapter(Integer.toString(i + 1), chapter));
            } catch (Exception error) {
                this.hiei.hieiLogger.error("Error Loading Key Chapter; Number => " + (i + 1), error);
            }
        }
    }

    public void updateVoiceCache(JsonArray data) {
        if (data.size() == 0) return;
        if (!this.voices.isEmpty()) this.voices.clear();
        for (int i = 0; i < data.size(); i++) {
            JsonObject voice = data.get(i).getAsJsonObject();
            String id = voice.keySet().stream().findFirst().orElse("Unknown");
            this.hiei.hieiLogger.debug("Loading Ship Voice; ID => " + id);
            try {
                this.voices.add(new HieiVoice(id, voice));
            } catch (Exception error) {
                this.hiei.hieiLogger.error("Error Loading Ship Voice; ID => " + id, error);
            }
        }
    }
}
