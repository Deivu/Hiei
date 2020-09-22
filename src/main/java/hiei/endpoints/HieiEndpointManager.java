package hiei.endpoints;

import hiei.HieiServer;
import hiei.endpoints.ships.SearchShip;
import hiei.struct.HieiEndpointContext;
import hiei.struct.HieiEndpointRouter;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class HieiEndpointManager {
    private final HieiServer hiei;
    private final HieiEndpointRouter hieiEndpointRouter;

    public HieiEndpointManager(HieiServer hiei) {
        this.hiei = hiei;
        this.hieiEndpointRouter = new HieiEndpointRouter(this.hiei);
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
        if (this.hiei.hieiConfig.includeAuthOnGetEndpoints && !auth.equals(this.hiei.hieiConfig.pass)) {
            response.setStatusMessage("Unauthorized");
            context.fail(401);
            return;
        }
        String query = request.getParam("query");
        if (query == null) {
            response.setStatusMessage("Bad Request");
            context.fail(400);
            return;
        }
        HieiEndpointContext hieiEndpointContext = new HieiEndpointContext(context, request, response, query);
        switch (endpoint) {
            case "searchShip":
                this.hieiEndpointRouter.searchShip.execute(hieiEndpointContext);
                break;
            case "searchEquipment":
                this.hieiEndpointRouter.searchEquipment.execute(hieiEndpointContext);
        }
    }
}
