package hiei.endpoints.equipments;

import com.google.gson.JsonArray;
import hiei.HieiServer;
import hiei.struct.HieiEndpoint;
import hiei.struct.HieiEndpointContext;
import hiei.struct.HieiEquip;
import hiei.struct.HieiEquipSearchResult;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.util.List;
import java.util.stream.Collectors;

public class SearchEquipment extends HieiEndpoint {
    public SearchEquipment(HieiServer hiei) { super(hiei); }

    @Override
    public void execute(HieiEndpointContext context) {
        List<HieiEquip> equips = this.search(context.queryString);
        JsonArray data = new JsonArray();
        for (HieiEquip equip : equips) data.add(equip.data);
        context.response.end(data.toString());
    }

    private List<HieiEquip> search(String input) {
        List<HieiEquip> data = this.hiei.hieiCache.equips.stream()
                .map(equip -> new HieiEquipSearchResult(FuzzySearch.weightedRatio(input, equip.name), equip))
                .filter(result ->  result.score > 60)
                .sorted((a, b) -> b.score - a.score)
                .map(result -> result.equipData)
                .collect(Collectors.toList());
        if (data.size() > this.hiei.hieiConfig.maxResults) data = data.subList(0, this.hiei.hieiConfig.maxResults);
        return data;
    }
}
