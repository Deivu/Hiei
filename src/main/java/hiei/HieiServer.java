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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HieiServer {
    public final Vertx vertx;
    public final HieiLogger hieiLogger;
    public final HieiConfig hieiConfig;
    public final HieiStore hieiStore;
    public final HieiUpdater hieiUpdater;
    public final HieiCache hieiCache;
    public final HieiEndpointManager hieiEndpointManager;
    public final ExecutorService singleThreadScheduler;
    public final ExecutorService cachedThreadPool;

    private final HttpServer server;
    private final Router mainRouter;
    private final Router apiRoutes;

    public HieiServer() throws IOException, URISyntaxException {

        this.hieiLogger = new HieiLogger();
        this.hieiConfig = new HieiConfig();
        this.vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(this.hieiConfig.threads));
        this.hieiStore = new HieiStore(this);
        this.hieiUpdater = new HieiUpdater(this);
        this.hieiCache = new HieiCache();
        this.hieiEndpointManager = new HieiEndpointManager(this);
        this.singleThreadScheduler = Executors.newSingleThreadScheduledExecutor();
        this.cachedThreadPool = Executors.newCachedThreadPool();
        this.server = this.vertx.createHttpServer();
        this.mainRouter = Router.router(vertx);
        this.apiRoutes = Router.router(vertx);
    }

    public HieiServer buildRoute() {
        apiRoutes.route(HttpMethod.GET, "/searchShip")
                .produces("application/json")
                .blockingHandler(context -> this.hieiEndpointManager.executeGet("searchShip", context), false)
                .failureHandler(this.hieiEndpointManager::executeFail)
                .enable();
        apiRoutes.route(HttpMethod.GET, "/searchEquipment")
                .produces("application/json")
                .blockingHandler(context -> this.hieiEndpointManager.executeGet("searchEquipment", context), false)
                .failureHandler(this.hieiEndpointManager::executeFail)
                .enable();
        apiRoutes.route("/*")
                .handler(StaticHandler.create().setIndexPage("haruna.html"))
                .failureHandler(this.hieiEndpointManager::executeFail)
                .enable();
        mainRouter.mountSubRouter(this.hieiConfig.routePrefix, apiRoutes);
        return this;
    }

    public void startServer() throws ExecutionException, InterruptedException {
        this.hieiLogger.info("Pre-start server checks initializing....");
        if (this.hieiUpdater.shipDataNeedsUpdate().get()) {
            this.hieiLogger.info("Downloading new ships from remote...");
            this.hieiStore.updateShipData();
        }
        if (this.hieiUpdater.equipmentDataNeedsUpdate().get()) {
            this.hieiLogger.info("Downloading new equipments from remote...");
            this.hieiStore.updateEquipmentData();
        }
        this.hieiCache.updateShipCache(this.hieiStore.getLocalShipsData());
        this.hieiLogger.info("Ship cache initialized!");
        this.hieiCache.updateEquipCache(this.hieiStore.getLocalEquipmentsData());
        this.hieiLogger.info("Equip cache initialized");
        server.requestHandler(this.mainRouter).listen(this.hieiConfig.port);
        this.hieiLogger.info("Kongō class second ship, Hiei is ready! Awaiting orders at port: " + this.hieiConfig.port);
    }
}
