package hiel;

import hiel.data.HielCache;
import hiel.data.HielStore;
import hiel.data.HielUpdater;
import hiel.util.HielConfig;
import hiel.util.HielLogger;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HielServer {
    public final HielLogger hielLogger;
    public final HielConfig hielConfig;
    public final Vertx vertx;
    public final HielStore hielStore;
    public final HielUpdater hielUpdater;
    public final HielCache hielCache;
    public final ExecutorService singleThreadScheduler;
    public final ExecutorService cachedThreadPool;

    private final HttpServer server;
    private final Router mainRouter;
    private final Router apiRoutes;

    public HielServer() throws IOException, URISyntaxException {
        this.hielLogger = new HielLogger();
        this.hielConfig = new HielConfig();
        this.vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(this.hielConfig.threads));
        this.hielStore = new HielStore(this);
        this.hielUpdater = new HielUpdater(this);
        this.hielCache = new HielCache(this);
        this.singleThreadScheduler = Executors.newSingleThreadScheduledExecutor();
        this.cachedThreadPool= Executors.newCachedThreadPool();
        this.server = this.vertx.createHttpServer();
        this.mainRouter = Router.router(vertx);
        this.apiRoutes = Router.router(vertx);
    }

    public HielServer buildRoute() {
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
        mainRouter.mountSubRouter(this.hielConfig.routePrefix, apiRoutes);
         */
        return this;
    }

    public void startServer() {
        server.requestHandler(this.mainRouter).listen(this.hielConfig.port);
    }
}
