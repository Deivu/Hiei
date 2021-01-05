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
    private final HieiBarrageEndpoint hieiBarrageEndpoint;
    private final HieiEventEndpoint hieiEventEndpoint;
    private final HieiChapterEndpoint hieiChapterEndpoint;

    public HieiEndpointManager(HieiServer hiei) {
        this.hiei = hiei;
        this.hieiShipEndpoint = new HieiShipEndpoint(this.hiei);
        this.hieiEquipmentEndpoint = new HieiEquipmentEndpoint(this.hiei);
        this.hieiBarrageEndpoint = new HieiBarrageEndpoint(this.hiei);
        this.hieiEventEndpoint = new HieiEventEndpoint(this.hiei);
        this.hieiChapterEndpoint = new HieiChapterEndpoint(this.hiei);
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
        try {
            String auth = request.getHeader("authorization");
            if (!auth.equals(this.hiei.hieiConfig.pass)) {
                response.setStatusMessage("Unauthorized");
                context.fail(401);
                return;
            }
            this.hiei.hieiLogger.info("Manual data update check authorized at /update endpoint, checking...");
            if (this.hiei.shipNeedsUpdate()) {
                this.hiei.hieiLogger.info("Ship data update available, updating...");
                this.hiei.updateShips();
            }
            this.hiei.hieiLogger.info("Ship data is up to date!");
            if (this.hiei.equipNeedsUpdate()) {
                this.hiei.hieiLogger.info("Equip data update available, updating...");
                this.hiei.updateEquips();
            }
            this.hiei.hieiLogger.info("Equip data is up to date!");
            this.hiei.hieiLogger.info("Blindly updating Barrages, Events, & Chapters.");
            this.hiei.updateBarrages();
            this.hiei.updateEvents();
            this.hiei.updateChapters();
            this.hiei.hieiLogger.info("Barrages, Events, & Chapters data updated!");
            this.hiei.hieiLogger.info("Manual data update executed!");
            response.end();
        } catch (Throwable throwable) {
            context.fail(throwable);
        }
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
        response.putHeader("content-type", "application/json; charset=utf-8");
        HieiEndpointContext hieiEndpointContext = new HieiEndpointContext(context, request, response, query);
        if (query == null) {
            switch (endpoint) {
                case "/ship/random":
                    this.hieiShipEndpoint.random(hieiEndpointContext);
                    break;
                case "/equip/random":
                    this.hieiEquipmentEndpoint.random(hieiEndpointContext);
                    break;
                default:
                    this.hiei.hieiLogger.info("No matching no query string endpoint found for: " + endpoint);
                    response.end();
            }
            return;
        }
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
                break;
            case "/barrage/searchBarrageByName":
                this.hieiBarrageEndpoint.searchBarrageByName(hieiEndpointContext);
                break;
            case "/barrage/searchBarrageByShip":
                this.hieiBarrageEndpoint.searchBarrageByShipName(hieiEndpointContext);
                break;
            case "/event/search":
                this.hieiEventEndpoint.search(hieiEndpointContext);
                break;
            case "/chapter/code":
                this.hieiChapterEndpoint.code(hieiEndpointContext);
                break;
            case "/chapter/search":
                this.hieiChapterEndpoint.search(hieiEndpointContext);
                break;
            default:
                this.hiei.hieiLogger.info("No matching endpoint found for: " + endpoint);
                response.end();
        }
    }
}