package hiei.data;

import hiei.HieiServer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class HieiCache {
    private final HieiServer hiei;

    private List<JSONObject> ships;
    private List<JSONObject> equips;

    public HieiCache(HieiServer hiei) throws FileNotFoundException {
        this.hiei = hiei;
        this.ships = new ArrayList<>();
        this.equips = new ArrayList<>();
        JSONArray localShips = this.hiei.hieiStore.getLocalShipsData();
        if (!localShips.isEmpty()) {
            for (int i = 0; i < localShips.length(); i++) this.ships.add(localShips.getJSONObject(i));
        }
        JSONArray localEquipments = this.hiei.hieiStore.getLocalEquipmentsData();
        if (!localEquipments.isEmpty()) {
            for (int i = 0; i < localEquipments.length(); i++) this.equips.add(localEquipments.getJSONObject(i));
        }
    }
}
