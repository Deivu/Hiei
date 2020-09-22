package hiei.endpoints.equipments;

import hiei.HieiServer;
import hiei.struct.*;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.util.List;
import java.util.stream.Collectors;

public class SearchEquipment extends HieiEndpoint {
    public SearchEquipment(HieiServer hiei) { super(hiei); }

    @Override
    public void execute(HieiEndpointContext context) {
        context.response.end(this.search(context.queryString).toString());
    }

    private JsonArray search(String input) {
        List<HieiEquip> data = this.hiei.hieiCache.equips.stream()
                .map(equip -> new HieiEquipSearchResult(FuzzySearch.weightedRatio(input, equip.name), equip))
                .filter(result ->  result.score > 60)
                .sorted((a, b) -> b.score - a.score)
                .map(result -> result.equipData)
                .collect(Collectors.toList());
        if (data.size() > this.hiei.hieiConfig.maxResults) data = data.subList(0, this.hiei.hieiConfig.maxResults);
        return new JsonArray(data);
    }
}
