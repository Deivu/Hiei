package hiei.endpoints.ships;

import com.google.gson.JsonArray;
import hiei.HieiServer;
import hiei.struct.HieiEndpoint;
import hiei.struct.HieiEndpointContext;
import hiei.struct.HieiShip;
import hiei.struct.HieiShipSearchResult;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.util.List;
import java.util.stream.Collectors;

public class SearchShip extends HieiEndpoint {
    public SearchShip(HieiServer hiei) { super(hiei); }

    @Override
    public void execute(HieiEndpointContext context) {
        List<HieiShip> ships = this.search(context.queryString);
        JsonArray data = new JsonArray();
        for (HieiShip ship : ships) data.add(ship.data);
        context.response.end(data.toString());
    }

    private List<HieiShip> search(String input) {
        List<HieiShip> data = this.hiei.hieiCache.ships.stream()
                .map(ship -> new HieiShipSearchResult(FuzzySearch.weightedRatio(input, ship.name), ship))
                .filter(result ->  result.score > 60)
                .sorted((a, b) -> b.score - a.score)
                .map(result -> result.shipData)
                .collect(Collectors.toList());
        if (data.size() > this.hiei.hieiConfig.maxResults) data = data.subList(0, this.hiei.hieiConfig.maxResults);
        return data;
    }
}
