package hiel.util;

import hiel.HielServer;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class HielConfig {
    public final int port;
    public final int threads;
    public final String pass;
    public final String routePrefix;
    public final String directory;

    public HielConfig() throws FileNotFoundException, URISyntaxException {
        File file = new File(HielServer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        this.directory = file.getPath().replace(file.getName(), "");
        InputStream is = new FileInputStream(this.directory + "config.json");
        JSONObject config = new JSONObject(new JSONTokener(is));
        port = config.has("port") ? config.getInt("port") : 1024;
        threads = config.has("threads") ? config.getInt("threads") : Runtime.getRuntime().availableProcessors();
        pass = config.has("pass") ? config.getString("pass") : null;
        routePrefix = config.has("routePrefix") ? "/" + config.getString("routePrefix") : "/";
    }
}
