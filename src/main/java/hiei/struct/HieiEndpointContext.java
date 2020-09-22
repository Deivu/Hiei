package hiei.struct;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class HieiEndpointContext {
    public RoutingContext routingContext;
    public HttpServerRequest request;
    public HttpServerResponse response;
    public String queryString;

    public HieiEndpointContext(RoutingContext routingContext, HttpServerRequest request, HttpServerResponse response, String queryString) {
        this.routingContext = routingContext;
        this.request = request;
        this.response = response;
        this.queryString = queryString;
    }
}
