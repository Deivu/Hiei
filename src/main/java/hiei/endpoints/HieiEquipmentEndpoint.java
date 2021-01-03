package hiei.endpoints;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import hiei.HieiServer;
import hiei.struct.HieiEndpointContext;
import hiei.struct.HieiEquip;
import hiei.struct.HieiSearchResult;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.util.List;
import java.util.stream.Collectors;

public class HieiEquipmentEndpoint {
    private HieiServer hiei;

    public HieiEquipmentEndpoint(HieiServer hiei) { this.hiei = hiei; }

    public void category(HieiEndpointContext context) {
        List<HieiEquip> data = this.hiei.hieiCache.equips.stream()
                .filter(equip -> equip.category.equals(context.queryString))
                .limit(this.hiei.hieiConfig.maxResults)
                .collect(Collectors.toList());
        JsonArray json = new JsonArray();
        for (HieiEquip obj : data) json.add(obj.data);
        context.response.end(new GsonBuilder().setPrettyPrinting().create().toJson(json));
    }

    public void nationality(HieiEndpointContext context) {
        List<HieiEquip> data = this.hiei.hieiCache.equips.stream()
                .filter(equip -> equip.nationality.equals(context.queryString))
                .limit(this.hiei.hieiConfig.maxResults)
                .collect(Collectors.toList());
        JsonArray json = new JsonArray();
        for (HieiEquip obj : data) json.add(obj.data);
        context.response.end(new GsonBuilder().setPrettyPrinting().create().toJson(json));
    }

    public void search(HieiEndpointContext context) {
        List<HieiEquip> data = this.hiei.hieiCache.equips.stream()
                .map(equip -> new HieiSearchResult(FuzzySearch.weightedRatio(context.queryString, equip.name), equip))
                .filter(result ->  result.score > this.hiei.hieiConfig.searchWeight)
                .sorted((a, b) -> b.score - a.score)
                .limit(this.hiei.hieiConfig.maxResults)
                .map(HieiSearchResult::getEquip)
                .collect(Collectors.toList());
        JsonArray json = new JsonArray();
        for (HieiEquip obj : data) json.add(obj.data);
        context.response.end(new GsonBuilder().setPrettyPrinting().create().toJson(json));
    }
}
