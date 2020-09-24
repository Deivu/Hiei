package hiei.struct;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class HieiEndpointContext {
    public final RoutingContext routingContext;
    public final HttpServerRequest request;
    public final HttpServerResponse response;
    public final String queryString;

    public HieiEndpointContext(RoutingContext routingContext, HttpServerRequest request, HttpServerResponse response, String queryString) {
        this.routingContext = routingContext;
        this.request = request;
        this.response = response;
        this.queryString = queryString;
    }
}
