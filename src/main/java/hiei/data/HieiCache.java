package hiei.data;

import com.google.gson.JsonArray;
import hiei.HieiServer;
import hiei.struct.HieiEquip;
import hiei.struct.HieiShip;

import java.util.concurrent.CopyOnWriteArrayList;

public class HieiCache {

    public final CopyOnWriteArrayList<HieiShip> ships;
    public final CopyOnWriteArrayList<HieiEquip> equips;

    public HieiCache() {
        this.ships = new CopyOnWriteArrayList<>();
        this.equips = new CopyOnWriteArrayList<>();
    }

    public void updateShipCache(JsonArray data) {
        if (data.size() == 0) return;
        if (!this.ships.isEmpty()) this.ships.clear();
        for (int i = 0; i < data.size(); i++)
            this.ships.add(new HieiShip(data.get(i).getAsJsonObject()));
    }

    public void updateEquipCache(JsonArray data) {
        if (data.size() == 0) return;
        if (!this.equips.isEmpty()) this.ships.clear();
        for (int i = 0; i < data.size(); i++)
            this.equips.add(new HieiEquip(data.get(i).getAsJsonObject()));
    }
}
