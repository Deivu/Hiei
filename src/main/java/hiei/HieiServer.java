package hiei;

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
    public final ExecutorService singleThreadExecutor;

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
        this.hieiCache = new HieiCache();
        this.hieiEndpointManager = new HieiEndpointManager(this);
        this.singleThreadScheduler = Executors.newSingleThreadScheduledExecutor();
        this.singleThreadExecutor = Executors.newSingleThreadExecutor();
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
        if (this.hieiUpdater.shipDataNeedsUpdate().get()) {
            this.hieiLogger.info("Ship data update available, updating...");
            this.hieiStore.updateShipData();
            this.hieiLogger.info("Local ship data up to date!");
        } else {
            this.hieiLogger.info("Ship data is up to date!");
        }
        if (this.hieiUpdater.equipmentDataNeedsUpdate().get()) {
            this.hieiLogger.info("Equip data update available, updating...");
            this.hieiStore.updateEquipmentData();
            this.hieiLogger.info("Local equip data up to date!");
        } else {
            this.hieiLogger.info("Equip data is up to date!");
        }
        this.hieiCache.updateShipCache(this.hieiStore.getLocalShipsData());
        this.hieiLogger.info("Ship rest cache loaded!");
        this.hieiCache.updateEquipCache(this.hieiStore.getLocalEquipmentsData());
        this.hieiLogger.info("Equip rest cache loaded!");
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

    private void executeTask() {
        try {
            this.hieiLogger.info("Automatic update check starting...");
            if (this.hieiUpdater.shipDataNeedsUpdate().get()) {
                this.hieiLogger.info("Ship data update available, updating...");
                this.hieiStore.updateShipData();
                this.hieiLogger.info("Local ship data up to date!");
                this.hieiCache.updateShipCache(this.hieiStore.getLocalShipsData());
                this.hieiLogger.info("Ship rest cache re-loaded!");
            } else {
                this.hieiLogger.info("Ship data is up to date!");
            }
            if (this.hieiUpdater.equipmentDataNeedsUpdate().get()) {
                this.hieiLogger.info("Equip data update available, updating...");
                this.hieiStore.updateEquipmentData();
                this.hieiLogger.info("Local equip data up to date!");
                this.hieiCache.updateEquipCache(this.hieiStore.getLocalEquipmentsData());
                this.hieiLogger.info("Equip rest cache re-loaded!");
            } else {
                this.hieiLogger.info("Equip data is up to date!");
            }
            this.hieiLogger.info("Automatic update check executed!");
        } catch (Throwable throwable) {
            this.hieiLogger.error(throwable);
        }
    }
}
