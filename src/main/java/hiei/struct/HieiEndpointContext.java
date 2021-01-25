package hiei.struct;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public class HieiEndpointContext {
    public final HttpServerRequest request;
    public final HttpServerResponse response;
    public final String queryString;

    public HieiEndpointContext(HttpServerRequest request, HttpServerResponse response, String queryString) {
        this.request = request;
        this.response = response;
        this.queryString = queryString;
    }
}
