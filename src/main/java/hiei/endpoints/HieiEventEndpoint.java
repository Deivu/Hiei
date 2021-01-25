package hiei.endpoints;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import hiei.HieiServer;
import hiei.struct.HieiEndpointContext;
import hiei.struct.HieiEvent;
import hiei.struct.HieiSearchResult;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.util.List;
import java.util.stream.Collectors;

public class HieiEventEndpoint {
    private final HieiServer hiei;

    public  HieiEventEndpoint(HieiServer hiei) { this.hiei = hiei; }

    public void search(HieiEndpointContext context) {
        List<HieiEvent> data = this.hiei.hieiCache.events.stream()
                .map(event -> new HieiSearchResult(FuzzySearch.weightedRatio(context.queryString, event.name), event))
                .filter(result ->  result.score > this.hiei.hieiConfig.searchWeight)
                .sorted((a, b) -> b.score - a.score)
                .limit(this.hiei.hieiConfig.maxResults)
                .map(HieiSearchResult::getEvent)
                .collect(Collectors.toList());
        JsonArray json = new JsonArray();
        for (HieiEvent obj : data) json.add(obj.data);
        context.response.end(new GsonBuilder().setPrettyPrinting().create().toJson(json));
    }
}
