package hiei.endpoints.ships;

import hiei.HieiServer;
import hiei.struct.*;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SearchShip extends HieiEndpoint {
    public SearchShip(HieiServer hiei) { super(hiei); }

    @Override
    public void execute(HieiEndpointContext context) {
        context.response.end(this.search(context.queryString).encodePrettily());
    }

    private JsonArray search(String input) {
        List<HieiShip> data = this.hiei.hieiCache.ships.stream()
                .map(ship -> new HieiShipSearchResult(FuzzySearch.weightedRatio(input, ship.name), ship))
                .filter(result ->  result.score > 60)
                .sorted((a, b) -> b.score - a.score)
                .map(result -> result.shipData)
                .collect(Collectors.toList());
        if (data.size() > this.hiei.hieiConfig.maxResults) data = data.subList(0, this.hiei.hieiConfig.maxResults);
        return new JsonArray(data);
    }
}
