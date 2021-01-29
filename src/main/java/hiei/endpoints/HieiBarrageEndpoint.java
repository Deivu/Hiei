package hiei.endpoints;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import hiei.HieiServer;
import hiei.struct.HieiBarrage;
import hiei.struct.HieiEndpointContext;
import hiei.struct.HieiSearchResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HieiBarrageEndpoint {
    private final HieiServer hiei;

    public HieiBarrageEndpoint(HieiServer hiei) { this.hiei = hiei; }

    public  void searchBarrageByName(HieiEndpointContext context) {
        List<HieiBarrage> data = this.hiei.hieiCache.barrage.stream()
                .map(barrage -> new HieiSearchResult(this.hiei, barrage).analyzeScore(context.queryString))
                .filter(result -> result.score <= this.hiei.hieiConfig.editDistance)
                .sorted(Comparator.comparingDouble(a -> a.score))
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
                HieiSearchResult result = new HieiSearchResult(this.hiei, barrage, ship).analyzeScore(context.queryString);
                if (result.score > this.hiei.hieiConfig.editDistance) continue;
                results.add(result);
            }
        }
        List<HieiBarrage> data = results.stream()
                .sorted(Comparator.comparingDouble(a -> a.score))
                .map(HieiSearchResult::getBarrage)
                .limit(this.hiei.hieiConfig.maxResults)
                .collect(Collectors.toList());
        JsonArray json = new JsonArray();
        for (HieiBarrage obj : data) json.add(obj.data);
        context.response.end(new GsonBuilder().setPrettyPrinting().create().toJson(json));
    }
}
