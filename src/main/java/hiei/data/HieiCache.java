package hiei.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hiei.HieiServer;
import hiei.struct.HieiEquip;
import hiei.struct.HieiShip;

import java.util.concurrent.CopyOnWriteArrayList;

public class HieiCache {
    private final HieiServer hiei;
    public final CopyOnWriteArrayList<HieiShip> ships;
    public final CopyOnWriteArrayList<HieiEquip> equips;

    public HieiCache(HieiServer hiei) {
        this.hiei = hiei;
        this.ships = new CopyOnWriteArrayList<>();
        this.equips = new CopyOnWriteArrayList<>();
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
}
