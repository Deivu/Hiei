package hiei.endpoints;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import hiei.HieiServer;
import hiei.struct.HieiBarrage;
import hiei.struct.HieiEndpointContext;
import hiei.struct.HieiSearchResult;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HieiBarrageEndpoint {
    private HieiServer hiei;

    public HieiBarrageEndpoint(HieiServer hiei) { this.hiei = hiei; }

    public  void searchBarrageByName(HieiEndpointContext context) {
        List<HieiBarrage> data = this.hiei.hieiCache.barrage.stream()
                .map(barrage -> new HieiSearchResult(FuzzySearch.weightedRatio(context.queryString, barrage.name), barrage))
                .filter(result ->  result.score > this.hiei.hieiConfig.searchWeight)
                .sorted((a, b) -> b.score - a.score)
                .limit(this.hiei.hieiConfig.maxResults)
                .map(HieiSearchResult::getBarrage)
                .collect(Collectors.toList());
        JsonArray json = new JsonArray();
        for (HieiBarrage obj : data) json.add(obj.data);
        context.response.end(new GsonBuilder().setPrettyPrinting().create().toJson(json));
    }

    public void searchBarrageByShipName(HieiEndpointContext context) {
        ArrayList<HieiSearchResult> results = new ArrayList<>();
        for (HieiBarrage barrage : this.hiei.hieiCache.barrage) {
            for (String ship : barrage.ships) {
                HieiSearchResult result = new HieiSearchResult(FuzzySearch.weightedRatio(context.queryString, ship), barrage);
                if (result.score < this.hiei.hieiConfig.searchWeight) continue;
                results.add(result);
            }
        }
        List<HieiBarrage> data = results.stream()
                .map(HieiSearchResult::getBarrage)
                .limit(this.hiei.hieiConfig.maxResults)
                .collect(Collectors.toList());
        JsonArray json = new JsonArray();
        for (HieiBarrage obj : data) json.add(obj.data);
        context.response.end(new GsonBuilder().setPrettyPrinting().create().toJson(json));
    }
}
