package hiei.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hiei.HieiServer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HieiUpdater {
    private final String versionData;
    private final String shipData;
    private final String equipmentData;
    private final String barrageData;
    private final String chaptersData;
    private final String eventData;
    private final String voiceData;
    private final WebClient client;

    public HieiUpdater(HieiServer hiei) {
        this.versionData = "https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/version-info.json";
        this.shipData = "https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/ships.json";
        this.equipmentData = "https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/equipments.json";
        this.barrageData = "https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/barrage.json";
        this.chaptersData = "https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/chapters.json";
        this.eventData = "https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/events.json";
        this.voiceData = "https://raw.githubusercontent.com/AzurAPI/azurapi-js-setup/master/voice_lines.json";
        this.client = WebClient.create(hiei.vertx, new WebClientOptions().setUserAgent("Hiei/" + hiei.version));
    }

    public CompletableFuture<JsonObject> fetchShipVersionData() {
        CompletableFuture<JsonObject> result = new CompletableFuture<>();
        client.requestAbs(HttpMethod.GET, this.versionData)
                .send(response -> {
                    if (response.failed()) {
                        Throwable throwable = response.cause();
                        if (throwable == null) throwable = new Throwable("Can't fetch remote ship version data");
                        result.completeExceptionally(throwable);
                        return;
                    }
                    Gson gson = new Gson();
                    JsonObject data = gson.fromJson(response.result().bodyAsString(StandardCharsets.UTF_8.name()), JsonObject.class);
                    data = data.get("ships").getAsJsonObject();
                    if (data == null) data = new JsonObject();
                    result.complete(data);
                });
        return result;
    }

    public CompletableFuture<JsonObject> fetchEquipmentVersionData() {
        CompletableFuture<JsonObject> result = new CompletableFuture<>();
        client.requestAbs(HttpMethod.GET, this.versionData)
                .send(response -> {
                    if (response.failed()) {
                        Throwable throwable = response.cause();
                        if (throwable == null) throwable = new Throwable("Can't fetch remote equip version data");
                        result.completeExceptionally(throwable);
                        return;
                    }
                    Gson gson = new Gson();
                    JsonObject data = gson.fromJson(response.result().bodyAsString(StandardCharsets.UTF_8.name()), JsonObject.class);
                    data = data.get("equipments").getAsJsonObject();
                    if (data == null) data = new JsonObject();
                    result.complete(data);
                });
        return result;
    }

    public CompletableFuture<JsonArray> fetchShipData() {
        CompletableFuture<JsonArray> result = new CompletableFuture<>();
        client.requestAbs(HttpMethod.GET, this.shipData)
                .send(response -> {
                    if (response.failed()) {
                        Throwable throwable = response.cause();
                        if (throwable == null) throwable = new Throwable("Can't fetch remote ship data");
                        result.completeExceptionally(throwable);
                        return;
                    }
                    result.complete(new Gson().fromJson(response.result().bodyAsString(StandardCharsets.UTF_8.name()), JsonArray.class));
                });
        return result;
    }

    public CompletableFuture<JsonArray> fetchEquipmentData() {
        CompletableFuture<JsonArray> result = new CompletableFuture<>();
        client.requestAbs(HttpMethod.GET, this.equipmentData)
                .send(response -> {
                    if (response.failed()) {
                        Throwable throwable = response.cause();
                        if (throwable == null) throwable = new Throwable("Can't fetch remote equip data");
                        result.completeExceptionally(throwable);
                        return;
                    }
                    JsonObject unparsedResponse = new Gson().fromJson(response.result().bodyAsString(StandardCharsets.UTF_8.name()), JsonObject.class);
                    JsonArray parsedResponse = new JsonArray();
                    for (String key : unparsedResponse.keySet()) parsedResponse.add(unparsedResponse.getAsJsonObject(key));
                    result.complete(parsedResponse);
                });
        return result;
    }

    public CompletableFuture<JsonArray> fetchBarrageData() {
        CompletableFuture<JsonArray> result = new CompletableFuture<>();
        client.requestAbs(HttpMethod.GET, this.barrageData)
                .send(response -> {
                    if (response.failed()) {
                        Throwable throwable = response.cause();
                        if (throwable == null) throwable = new Throwable("Can't fetch remote barrage data");
                        result.completeExceptionally(throwable);
                        return;
                    }
                    result.complete(new Gson().fromJson(response.result().bodyAsString(StandardCharsets.UTF_8.name()), JsonArray.class));
                });
        return result;
    }

    public CompletableFuture<JsonArray> fetchChaptersData() {
        CompletableFuture<JsonArray> result = new CompletableFuture<>();
        client.requestAbs(HttpMethod.GET, this.chaptersData)
                .send(response -> {
                    if (response.failed()) {
                        Throwable throwable = response.cause();
                        if (throwable == null) throwable = new Throwable("Can't fetch remote chapters data");
                        result.completeExceptionally(throwable);
                        return;
                    }
                    JsonObject unparsedResponse = new Gson().fromJson(response.result().bodyAsString(StandardCharsets.UTF_8.name()), JsonObject.class);
                    JsonArray parsedResponse = new JsonArray();
                    for (String key : unparsedResponse.keySet()) parsedResponse.add(unparsedResponse.getAsJsonObject(key));
                    result.complete(parsedResponse);
                });
        return result;
    }

    public CompletableFuture<JsonArray> fetchEventData() {
        CompletableFuture<JsonArray> result = new CompletableFuture<>();
        client.requestAbs(HttpMethod.GET, this.eventData)
                .send(response -> {
                    if (response.failed()) {
                        Throwable throwable = response.cause();
                        if (throwable == null) throwable = new Throwable("Can't fetch remote events data");
                        result.completeExceptionally(throwable);
                        return;
                    }
                    result.complete(new Gson().fromJson(response.result().bodyAsString(StandardCharsets.UTF_8.name()), JsonArray.class));
                });
        return result;
    }
    public CompletableFuture<JsonArray> fetchVoiceData() {
        CompletableFuture<JsonArray> result = new CompletableFuture<>();
        client.requestAbs(HttpMethod.GET, this.voiceData)
                .send(response -> {
                    if (response.failed()) {
                        Throwable throwable = response.cause();
                        if (throwable == null) throwable = new Throwable("Can't fetch remote voice data");
                        result.completeExceptionally(throwable);
                        return;
                    }
                    JsonObject unparsedResponse = new Gson().fromJson(response.result().bodyAsString(StandardCharsets.UTF_8.name()), JsonObject.class);
                    JsonArray parsedResponse = new JsonArray();
                    for (Map.Entry<String, JsonElement> entry : unparsedResponse.entrySet()) {
                        JsonObject newObject = new JsonObject();
                        newObject.add(entry.getKey(), entry.getValue());
                        parsedResponse.add(newObject);
                    }
                    result.complete(parsedResponse);
                });
        return result;
    }
}
