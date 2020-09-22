package hiei.struct;

import hiei.HieiServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

abstract public class HieiEndpoint {
    protected final HieiServer hiei;

    public HieiEndpoint(HieiServer hiei) { this.hiei = hiei; }

    public abstract void execute(HieiEndpointContext context);
}
