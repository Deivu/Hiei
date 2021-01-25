package hiei.endpoints;

import com.google.gson.JsonObject;
import hiei.HieiServer;
import hiei.struct.HieiEndpointContext;
import hiei.struct.HieiVoice;

public class HieiVoiceEndpoint {
    private final HieiServer hiei;

    public HieiVoiceEndpoint(HieiServer hiei) { this.hiei = hiei; }

    public void id(HieiEndpointContext context) {
        HieiVoice data = this.hiei.hieiCache.voices.stream()
                .filter(voice -> voice.id.equals(context.queryString))
                .findFirst()
                .orElse(null);
        if (data == null) {
            context.response.end(new JsonObject().toString());
            return;
        }
        context.response.end(data.toString());
    }
}
