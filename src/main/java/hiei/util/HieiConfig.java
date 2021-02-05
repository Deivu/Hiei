package hiei.util;

import hiei.HieiServer;
import io.github.mightguy.spellcheck.symspell.api.DataHolder;
import io.github.mightguy.spellcheck.symspell.api.StringDistance;

import io.github.mightguy.spellcheck.symspell.common.QwertyDistance;
import io.github.mightguy.spellcheck.symspell.common.SpellCheckSettings;
import io.github.mightguy.spellcheck.symspell.common.WeightedDamerauLevenshteinDistance;
import io.github.mightguy.spellcheck.symspell.impl.InMemoryDataHolder;
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
    public final int checkUpdateInterval;
    public final int maxResults;
    public final double editDistance;
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
            this.editDistance = config.containsKey("editDistance") ? config.getInteger("editDistance") : 6;
            this.privateRest = !config.containsKey("privateRest") || config.getBoolean("privateRest");
            this.checkUpdateInterval = config.containsKey("checkUpdateInterval") ? config.getInteger("checkUpdateInterval") : 0;
        }
    }

    public StringDistance getDistanceComparator() {
        return new WeightedDamerauLevenshteinDistance(0.8f, 1.01f, 0.9f, 0.7f, new QwertyDistance());
    }
}
