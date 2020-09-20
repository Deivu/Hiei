package hiel.data;

import hiel.HielServer;
import io.vertx.core.json.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class HielCache {
    private final HielServer hiel;

    private List<JSONObject> ships;
    private List<JSONObject> equips;

    public HielCache(HielServer hiel) throws FileNotFoundException {
        this.hiel = hiel;
        this.ships = new ArrayList<>();
        this.equips = new ArrayList<>();
        JSONArray localShips = this.hiel.hielStore.getLocalShipsData();
        if (!localShips.isEmpty()) {
            for (int i = 0; i < localShips.length(); i++) this.ships.add(localShips.getJSONObject(i));
        }
        JSONArray localEquipments = this.hiel.hielStore.getLocalEquipmentsData();
        if (!localEquipments.isEmpty()) {
            for (int i = 0; i < localEquipments.length(); i++) this.equips.add(localEquipments.getJSONObject(i));
        }
    }
}
