package hiei.util;

import hiei.HieiServer;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URISyntaxException;

public class HieiConfig {
    public final String pass;
    public final int port;
    public final int threads;
    public final String routePrefix;
    public final String directory;
    public final int maxResults;
    public final int searchWeight;
    public final boolean privateRest;

    public HieiConfig() throws IOException, URISyntaxException {
        File file = new File(HieiServer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        this.directory = file.getPath().replace(file.getName(), "");
        try (InputStream is = new FileInputStream(this.directory + "config.json")) {
            JsonObject config = new JsonObject(IOUtils.toString(is));
            this.pass = config.getString("pass");
            this.port = config.containsKey("port") ? config.getInteger("port") : 1024;
            this.threads = config.containsKey("threads") ? config.getInteger("threads") : Runtime.getRuntime().availableProcessors();
            this.routePrefix = config.containsKey("routePrefix") ? "/" + config.getString("routePrefix") : "/";
            this.maxResults = config.containsKey("maxResults") ? config.getInteger("maxResults") : 5;
            this.searchWeight = config.containsKey("searchWeight") ? config.getInteger("searchWeight") : 75;
            this.privateRest = !config.containsKey("privateRest") || config.getBoolean("privateRest");
        }
    }
}
