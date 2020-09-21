package hiei.data;

import hiei.HieiServer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class HieiUpdater {
    private final HieiServer hiei;
    private final String versionData;
    private final String shipData;
    private final String equipmentData;
    private final WebClient client;

    public HieiUpdater(HieiServer hiei) throws IOException {
        this.hiei = hiei;
        this.versionData = "https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/version-info.json";
        this.shipData = "https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/ships.json";
        this.equipmentData = "https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/equipments.json";
        this.client = WebClient.create(this.hiei.vertx, new WebClientOptions().setUserAgent("Hiei/dev"));
    }

    public CompletableFuture<Boolean> shipDataNeedsUpdate() {
        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return this.fetchShipVersionData().get();
                    } catch (Throwable throwable) {
                        throw new CompletionException(throwable);
                    }
                }, this.hiei.cachedThreadPool)
                .thenApplyAsync(data -> {
                    try {
                        InputStream is = new FileInputStream(this.hiei.hieiStore.getDataDirectory() + this.hiei.hieiStore.getShipVersionFileName());
                        JSONObject current = new JSONObject(new JSONTokener(is));
                        return data.getInteger("version-number").equals(current.getInt("version-number"));
                    } catch (Throwable throwable) {
                        throw new CompletionException(throwable);
                    }
                }, this.hiei.cachedThreadPool)
                .exceptionally(throwable -> {
                    this.hiei.hieiLogger.error(throwable);
                    return null;
                });
    }

    public CompletableFuture<Boolean> equipmentDataNeedsUpdate() {
        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return this.fetchEquipmentVersionData().get();
                    } catch (Throwable throwable) {
                        throw new CompletionException(throwable);
                    }
                }, this.hiei.cachedThreadPool)
                .thenApplyAsync(data -> {
                    try {
                        InputStream is = new FileInputStream(this.hiei.hieiStore.getDataDirectory() + this.hiei.hieiStore.getEquipmentVersionFileName());
                        JSONObject current = new JSONObject(new JSONTokener(is));
                        return data.getInteger("version-number").equals(current.getInt("version-number"));
                    } catch (Throwable throwable) {
                        throw new CompletionException(throwable);
                    }
                }, this.hiei.cachedThreadPool)
                .exceptionally(throwable -> {
                    this.hiei.hieiLogger.error(throwable);
                    return null;
                });
    }

    public CompletableFuture<JsonObject> fetchShipVersionData() {
        CompletableFuture<JsonObject> result = new CompletableFuture<>();
        client.requestAbs(HttpMethod.GET, this.versionData)
                .send(response -> {
                    try {
                        if (response.failed()) {
                            Throwable throwable = response.cause();
                            if (throwable == null) {
                                result.complete(null);
                                return;
                            }
                            throw throwable;
                        }
                        result.complete(response.result().bodyAsJsonObject().getJsonObject("ships"));
                    } catch (Throwable throwable) {
                        result.completeExceptionally(throwable);
                    }
                });
        return result;
    }

    public CompletableFuture<JsonObject> fetchEquipmentVersionData() {
        CompletableFuture<JsonObject> result = new CompletableFuture<>();
        client.requestAbs(HttpMethod.GET, this.versionData)
                .send(response -> {
                    try {
                        if (response.failed()) {
                            Throwable throwable = response.cause();
                            if (throwable == null) {
                                result.complete(null);
                                return;
                            }
                            throw throwable;
                        }
                        result.complete(response.result().bodyAsJsonObject().getJsonObject("equipments"));
                    } catch (Throwable throwable) {
                        result.completeExceptionally(throwable);
                    }
                });
        return result;
    }

    public CompletableFuture<JsonArray> fetchShipData() {
        CompletableFuture<JsonArray> result = new CompletableFuture<>();
        client.requestAbs(HttpMethod.GET, this.shipData)
                .send(response -> {
                    try {
                        if (response.failed()) {
                            Throwable throwable = response.cause();
                            if (throwable == null) {
                                result.complete(null);
                                return;
                            }
                            throw throwable;
                        }
                        result.complete(response.result().bodyAsJsonArray());
                    } catch (Throwable throwable) {
                        result.completeExceptionally(throwable);
                    }
                });
        return result;
    }

    public CompletableFuture<JsonArray> fetchEquipmentData() {
        CompletableFuture<JsonArray> result = new CompletableFuture<>();
        client.requestAbs(HttpMethod.GET, this.equipmentData)
                .send(response -> {
                    try {
                        if (response.failed()) {
                            Throwable throwable = response.cause();
                            if (throwable == null) {
                                result.complete(null);
                                return;
                            }
                            throw throwable;
                        }
                        result.complete(response.result().bodyAsJsonArray());
                    } catch (Throwable throwable) {
                        result.completeExceptionally(throwable);
                    }
                });
        return result;
    }
}
