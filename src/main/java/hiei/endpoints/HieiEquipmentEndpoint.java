package hiei.endpoints;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import hiei.HieiServer;
import hiei.struct.HieiEndpointContext;
import hiei.struct.HieiEquip;
import hiei.struct.HieiSearchResult;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class HieiEquipmentEndpoint {
    private final HieiServer hiei;

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

    public void random(HieiEndpointContext context) {
        int random = new Random().nextInt(this.hiei.hieiCache.equips.size() - 1);
        HieiEquip equip = this.hiei.hieiCache.equips.get(random);
        context.response.end(equip.toString());
    }

    public void search(HieiEndpointContext context) {
        List<HieiEquip> data = this.hiei.hieiCache.equips.stream()
                .map(equip -> new HieiSearchResult(this.hiei, equip).analyzeScore(context.queryString))
                .filter(result -> result.score <= this.hiei.hieiConfig.editDistance)
                .sorted(Comparator.comparingDouble(a -> a.score))
                .limit(this.hiei.hieiConfig.maxResults)
                .map(HieiSearchResult::getEquip)
                .collect(Collectors.toList());
        JsonArray json = new JsonArray();
        for (HieiEquip obj : data) json.add(obj.data);
        context.response.end(new GsonBuilder().setPrettyPrinting().create().toJson(json));
    }
}
