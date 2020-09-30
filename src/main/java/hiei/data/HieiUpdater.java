package hiei.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hiei.HieiServer;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class HieiUpdater {
    private final HieiServer hiei;
    private final String versionData;
    private final String shipData;
    private final String equipmentData;
    private final WebClient client;

    public HieiUpdater(HieiServer hiei) {
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
                }, this.hiei.singleThreadExecutor)
                .thenApplyAsync(data -> {
                    Buffer buffer = this.hiei.hieiStore.getFileSystem().readFileBlocking(this.hiei.hieiStore.getDataDirectory() + this.hiei.hieiStore.getShipVersionFileName());
                    Gson gson = new Gson();
                    JsonObject current = gson.fromJson(buffer.toString(StandardCharsets.UTF_8.name()), JsonObject.class);
                    if (current == null) return true;
                    current = current.getAsJsonObject();
                    return current.get("version-number") == null|| !data.get("version-number").getAsBigInteger().equals(current.get("version-number").getAsBigInteger());
                }, this.hiei.singleThreadExecutor)
                .exceptionally(throwable -> {
                    this.hiei.hieiLogger.error(throwable);
                    return true;
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
                }, this.hiei.singleThreadExecutor)
                .thenApplyAsync(data -> {
                    Buffer buffer = this.hiei.hieiStore.getFileSystem().readFileBlocking(this.hiei.hieiStore.getDataDirectory() + this.hiei.hieiStore.getEquipmentVersionFileName());
                    Gson gson = new Gson();
                    JsonObject current = gson.fromJson(buffer.toString(StandardCharsets.UTF_8.name()), JsonObject.class);
                    if (current == null) return true;
                    current = current.getAsJsonObject();
                    return current.get("version-number") == null|| !data.get("version-number").getAsBigInteger().equals(current.get("version-number").getAsBigInteger());
                }, this.hiei.singleThreadExecutor)
                .exceptionally(throwable -> {
                    this.hiei.hieiLogger.error(throwable);
                    return true;
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
                        Gson gson = new Gson();
                        JsonObject data = gson.fromJson(response.result().bodyAsString(StandardCharsets.UTF_8.name()), JsonObject.class);
                        result.complete(data.get("ships").getAsJsonObject());
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
                        Gson gson = new Gson();
                        JsonObject data = gson.fromJson(response.result().bodyAsString(StandardCharsets.UTF_8.name()), JsonObject.class);
                        result.complete(data.get("equipments").getAsJsonObject());
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
                        Gson gson = new Gson();
                        result.complete(gson.fromJson(response.result().bodyAsString(StandardCharsets.UTF_8.name()), JsonArray.class));
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
                        Gson gson = new Gson();
                        JsonObject unparsedResponse = gson.fromJson(response.result().bodyAsString(StandardCharsets.UTF_8.name()), JsonObject.class);
                        JsonArray parsedResponse = new JsonArray();
                        for (String key : unparsedResponse.keySet()) parsedResponse.add(unparsedResponse.getAsJsonObject(key));
                        result.complete(parsedResponse);
                    } catch (Throwable throwable) {
                        result.completeExceptionally(throwable);
                    }
                });
        return result;
    }
}
