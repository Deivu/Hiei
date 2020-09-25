package hiei.endpoints;

import hiei.HieiServer;
import hiei.struct.HieiEndpointContext;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class HieiEndpointManager {
    private final HieiServer hiei;
    private final HieiShipEndpoint hieiShipEndpoint;
    private final HieiEquipmentEndpoint hieiEquipmentEndpoint;

    public HieiEndpointManager(HieiServer hiei) {
        this.hiei = hiei;
        this.hieiShipEndpoint = new HieiShipEndpoint(this.hiei);
        this.hieiEquipmentEndpoint = new HieiEquipmentEndpoint(this.hiei);
    }

    public void executeFail(RoutingContext context) {
        Throwable throwable = context.failure();
        HttpServerResponse response = context.response();
        int statusCode = context.statusCode();
        if (throwable != null) {
           this.hiei.hieiLogger.error(throwable);
        } else {
            this.hiei.hieiLogger.warn("Failed REST Request; Code: " + statusCode + " Reason: " + response.getStatusMessage());
        }
        response.setStatusCode(statusCode).end();
    }

    public void executeUpdate(RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();
        String auth = request.getHeader("authorization");
        if (!auth.equals(this.hiei.hieiConfig.pass)) {
            response.setStatusMessage("Unauthorized");
            context.fail(401);
            return;
        }
        // implement threading here kthx, just putting something
        hiei.hieiStore.updateShipData();
    }

    public void executeGet(String endpoint, RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();
        String auth = request.getHeader("authorization");
        if (this.hiei.hieiConfig.privateRest && !auth.equals(this.hiei.hieiConfig.pass)) {
            response.setStatusMessage("Unauthorized");
            context.fail(401);
            return;
        }
        String query = request.getParam("q");
        if (query == null) {
            response.setStatusMessage("Bad Request");
            context.fail(400);
            return;
        }
        response.putHeader("content-type", "application/json; charset=utf-8");
        HieiEndpointContext hieiEndpointContext = new HieiEndpointContext(context, request, response, query);
        switch (endpoint) {
            case "/ship/search":
                this.hieiShipEndpoint.search(hieiEndpointContext);
                break;
            case "/ship/id":
                this.hieiShipEndpoint.id(hieiEndpointContext);
                break;
            case "/ship/rarity":
                this.hieiShipEndpoint.rarity(hieiEndpointContext);
                break;
            case "/ship/hullType":
                this.hieiShipEndpoint.hullType(hieiEndpointContext);
                break;
            case "/ship/shipClass":
                this.hieiShipEndpoint.shipClass(hieiEndpointContext);
                break;
            case "/ship/nationality":
                this.hieiShipEndpoint.nationality(hieiEndpointContext);
                break;
            case "/equip/search":
                this.hieiEquipmentEndpoint.search(hieiEndpointContext);
                break;
            case "/equip/nationality":
                this.hieiEquipmentEndpoint.nationality(hieiEndpointContext);
                break;
            case "/equip/category":
                this.hieiEquipmentEndpoint.category(hieiEndpointContext);
        }
    }
}
