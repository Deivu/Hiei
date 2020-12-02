package hiei;

import com.google.gson.JsonObject;
import hiei.data.HieiCache;
import hiei.data.HieiStore;
import hiei.data.HieiUpdater;
import hiei.endpoints.HieiEndpointManager;
import hiei.util.HieiConfig;
import hiei.util.HieiLogger;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.*;

public class HieiServer {
    public final String version;
    public final HieiLogger hieiLogger;
    public final HieiConfig hieiConfig;
    public final Vertx vertx;
    public final HieiStore hieiStore;
    public final HieiUpdater hieiUpdater;
    public final HieiCache hieiCache;
    public final HieiEndpointManager hieiEndpointManager;
    public final ScheduledExecutorService singleThreadScheduler;

    private final HttpServer server;
    private final Router mainRouter;
    private final Router apiRoutes;
    private final String[] getEndpoints;

    public HieiServer() throws IOException, URISyntaxException {
        this.version = getClass().getPackage().getImplementationVersion() != null ? getClass().getPackage().getImplementationVersion() : "dev";
        this.hieiLogger = new HieiLogger();
        this.hieiConfig = new HieiConfig();
        this.vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(this.hieiConfig.threads));
        this.hieiStore = new HieiStore(this);
        this.hieiUpdater = new HieiUpdater(this);
        this.hieiCache = new HieiCache(this);
        this.hieiEndpointManager = new HieiEndpointManager(this);
        this.singleThreadScheduler = Executors.newSingleThreadScheduledExecutor();
        this.server = this.vertx.createHttpServer();
        this.mainRouter = Router.router(vertx);
        this.apiRoutes = Router.router(vertx);
        this.getEndpoints = new String[]{
                "/ship/search",
                "/ship/id",
                "/ship/rarity",
                "/ship/hullType",
                "/ship/shipClass",
                "/ship/nationality",
                "/equip/search",
                "/equip/nationality",
                "/equip/category"
        };
    }

    public HieiServer buildRest() {
        for (String endpoint : getEndpoints)
            apiRoutes.route(HttpMethod.GET, endpoint)
                    .blockingHandler(context -> this.hieiEndpointManager.executeGet(endpoint, context), false)
                    .failureHandler(this.hieiEndpointManager::executeFail)
                    .enable();
        apiRoutes.route(HttpMethod.POST, "/update")
                .blockingHandler(this.hieiEndpointManager::executeUpdate, false)
                .failureHandler(this.hieiEndpointManager::executeFail)
                .enable();
        apiRoutes.route("/*")
                .handler(StaticHandler.create().setIndexPage("hiei.html"))
                .failureHandler(this.hieiEndpointManager::executeFail)
                .enable();
        mainRouter.mountSubRouter(this.hieiConfig.routePrefix, apiRoutes);
        return this;
    }

    public HieiServer startServer() throws ExecutionException, InterruptedException {
        this.hieiLogger.info("Pre-start server checks initializing....");
        if (!this.shipDataUpToDate()) {
            this.hieiLogger.info("Ship data update available, updating...");
            this.updateShips();
        } else {
            this.hieiCache.updateShipCache(this.hieiStore.getLocalShipsData());
        }
        this.hieiLogger.info("Ship data is up to date!");
        if (!this.equipDataUpToDate()) {
            this.hieiLogger.info("Equip data update available, updating...");
            this.updateEquips();
        } else {
            this.hieiCache.updateEquipCache(this.hieiStore.getLocalEquipmentsData());
        }
        this.hieiLogger.info("Equip data is up to date!");
        server.requestHandler(this.mainRouter).listen(this.hieiConfig.port);
        this.hieiLogger.info("Kongō class second ship, Hiei is ready! Awaiting orders at port: " + this.hieiConfig.port);
        return this;
    }

    public void scheduleTasks() {
        if (this.hieiConfig.checkUpdateInterval == 0) {
            this.hieiLogger.info("Automatic update check is disabled, you need to update the data periodically yourself");
            return;
        }
        this.singleThreadScheduler.scheduleAtFixedRate(this::executeTask, this.hieiConfig.checkUpdateInterval, this.hieiConfig.checkUpdateInterval, TimeUnit.HOURS);
        this.hieiLogger.info("Automatic update check is now set. Set to run every " + this.hieiConfig.checkUpdateInterval + " hour(s)");
    }

    public boolean shipDataUpToDate() throws ExecutionException, InterruptedException {
        JsonObject remote = this.hieiUpdater.fetchShipVersionData().get();
        JsonObject local = this.hieiStore.getLocalShipVersion();
        return remote.get("version-number").getAsString().equals(local.get("version-number").getAsString());
    }

    public boolean equipDataUpToDate() throws ExecutionException, InterruptedException {
        JsonObject remote = this.hieiUpdater.fetchEquipmentVersionData().get();
        JsonObject local = this.hieiStore.getLocalEquipVersion();
        return remote.get("version-number").getAsString().equals(local.get("version-number").getAsString());
    }

    public void updateShips() throws ExecutionException, InterruptedException {
        this.hieiStore.updateShipData();
        this.hieiCache.updateShipCache(this.hieiStore.getLocalShipsData());
    }

    public void updateEquips() throws ExecutionException, InterruptedException {
        this.hieiStore.updateEquipmentData();
        this.hieiCache.updateEquipCache(this.hieiStore.getLocalEquipmentsData());
    }

    private void executeTask() {
        try {
            this.hieiLogger.info("Automatic update check starting...");
            if (!this.shipDataUpToDate()) {
                this.hieiLogger.info("Ship data update available, updating...");
                this.updateShips();
            }
            this.hieiLogger.info("Ship data is up to date!");
            if (!this.equipDataUpToDate()) {
                this.hieiLogger.info("Equip data update available, updating...");
                this.updateEquips();
            }
            this.hieiLogger.info("Equip data is up to date!");
            this.hieiLogger.info("Automatic update check executed!");
        } catch (Throwable throwable) {
            this.hieiLogger.error(throwable);
        }
    }
}
