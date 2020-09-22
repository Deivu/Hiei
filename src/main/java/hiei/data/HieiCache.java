package hiei.data;

import hiei.HieiServer;
import hiei.struct.HieiEquip;
import hiei.struct.HieiShip;
import io.vertx.core.json.JsonArray;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
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
        if (data.isEmpty()) return;
        if (!this.ships.isEmpty()) this.ships.clear();
        for (int i = 0; i < data.size(); i++) this.ships.add(new HieiShip(data.getJsonObject(i)));
    }

    public void updateEquipCache(JsonArray data) {
        if (data.isEmpty()) return;
        if (!this.equips.isEmpty()) this.equips.clear();
        for (int i = 0; i < data.size(); i++) this.equips.add(new HieiEquip(data.getJsonObject(i)));
    }
}
