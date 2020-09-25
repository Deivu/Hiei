package hiei.endpoints;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hiei.HieiServer;
import hiei.struct.*;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.util.List;
import java.util.stream.Collectors;

public class HieiShipEndpoint {
    private HieiServer hiei;

    public HieiShipEndpoint(HieiServer hiei) { this.hiei = hiei; }

    public void nationality(HieiEndpointContext context) {
        List<HieiShip> data = this.hiei.hieiCache.ships.stream()
                .filter(ship -> ship.nationality.equals(context.queryString))
                .limit(this.hiei.hieiConfig.maxResults)
                .collect(Collectors.toList());
        JsonArray json = new JsonArray();
        for (HieiShip obj : data) json.add(obj.data);
        context.response.end(json.toString());
    }

    public void shipClass(HieiEndpointContext context) {
        List<HieiShip> data = this.hiei.hieiCache.ships.stream()
                .filter(ship -> ship.shipClass.equals(context.queryString))
                .limit(this.hiei.hieiConfig.maxResults)
                .collect(Collectors.toList());
        JsonArray json = new JsonArray();
        for (HieiShip obj : data) json.add(obj.data);
        context.response.end(json.toString());
    }

    public void hullType(HieiEndpointContext context) {
        List<HieiShip> data = this.hiei.hieiCache.ships.stream()
                .filter(ship -> ship.hullType.equals(context.queryString))
                .limit(this.hiei.hieiConfig.maxResults)
                .collect(Collectors.toList());
        JsonArray json = new JsonArray();
        for (HieiShip obj : data) json.add(obj.data);
        context.response.end(json.toString());
    }

    public void rarity(HieiEndpointContext context) {
        List<HieiShip> data = this.hiei.hieiCache.ships.stream()
                .filter(ship -> ship.rarity.equals(context.queryString))
                .limit(this.hiei.hieiConfig.maxResults)
                .collect(Collectors.toList());
        JsonArray json = new JsonArray();
        for (HieiShip obj : data) json.add(obj.data);
        context.response.end(json.toString());
    }

    public void id(HieiEndpointContext context) {
        List<HieiShip> data = this.hiei.hieiCache.ships.stream()
                .filter(ship -> ship.id.equals(context.queryString))
                .collect(Collectors.toList());
        JsonObject json = new JsonObject();
        if (!data.isEmpty()) json = data.get(0).data;
        context.response.end(json.toString());
    }

    public void search(HieiEndpointContext context) {
        List<HieiShip> data = this.hiei.hieiCache.ships.stream()
                .map(ship -> new HieiSearchResult(FuzzySearch.weightedRatio(context.queryString, ship.name), ship))
                .filter(result ->  result.score > this.hiei.hieiConfig.searchWeight)
                .sorted((a, b) -> b.score - a.score)
                .limit(this.hiei.hieiConfig.maxResults)
                .map(HieiSearchResult::getShip)
                .collect(Collectors.toList());
        JsonArray json = new JsonArray();
        for (HieiShip obj : data) json.add(obj.data);
        context.response.end(data.toString());
    }
}
