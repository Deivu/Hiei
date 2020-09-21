package hiei;

import hiei.data.HieiCache;
import hiei.data.HieiStore;
import hiei.data.HieiUpdater;
import hiei.util.HieiConfig;
import hiei.util.HieiLogger;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HieiServer {
    public final HieiLogger hieiLogger;
    public final HieiConfig hieiConfig;
    public final Vertx vertx;
    public final HieiStore hieiStore;
    public final HieiUpdater hieiUpdater;
    public final HieiCache hieiCache;
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
        this.hieiCache = new HieiCache(this);
        this.singleThreadScheduler = Executors.newSingleThreadScheduledExecutor();
        this.cachedThreadPool= Executors.newCachedThreadPool();
        this.server = this.vertx.createHttpServer();
        this.mainRouter = Router.router(vertx);
        this.apiRoutes = Router.router(vertx);
    }

    public HieiServer buildRoute() {
        /*
        apiRoutes.route().handler(BodyHandler.create());
        apiRoutes.route(HttpMethod.POST, "/newVote")
                .blockingHandler(context -> this.routeHandler.trigger("newVote", context), true)
                .failureHandler(this.routeHandler::triggerFail)
                .enable( );
        apiRoutes.route(HttpMethod.GET, "/voteInfo")
                .produces("application/json")
                .blockingHandler(context -> this.routeHandler.trigger("voteInfo", context), false)
                .failureHandler(this.routeHandler::triggerFail)
                .enable( );
        apiRoutes.route(HttpMethod.GET, "/stats")
                .produces("application/json")
                .blockingHandler(context -> this.routeHandler.trigger("stats", context), false)
                .failureHandler(this.routeHandler::triggerFail)
                .enable();
        apiRoutes.route("/*")
                .handler(StaticHandler.create().setIndexPage("haruna.html"))
                .failureHandler(this.routeHandler::triggerFail)
                .enable();
        mainRouter.mountSubRouter(this.hieiConfig.routePrefix, apiRoutes);
         */
        return this;
    }

    public void startServer() {
        server.requestHandler(this.mainRouter).listen(this.hieiConfig.port);
    }
}
