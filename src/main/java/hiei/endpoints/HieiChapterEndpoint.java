package hiei.endpoints;

import hiei.HieiServer;
import hiei.struct.*;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Comparator;

public class HieiChapterEndpoint {
    private final HieiServer hiei;

    public HieiChapterEndpoint(HieiServer hiei) { this.hiei = hiei; }

    public void code(HieiEndpointContext context) {
        String[] query = context.queryString.split("-");
        if (query.length > 1) {
             HieiKeyChapter data = this.hiei.hieiCache.chapters.get(Integer.parseInt(query[0]) - 1);
             if (data == null) {
                 context.response.end(new JsonObject().toString());
                 return;
             }
             HieiSubChapter subData = data.subChapters.get(Integer.parseInt(query[1]) - 1);
             if (subData == null) {
                 context.response.end(new JsonObject().toString());
                 return;
             }
             context.response.end(subData.toString());
             return;
        }
        HieiKeyChapter data = this.hiei.hieiCache.chapters.get(Integer.parseInt(query[0]) - 1);
        if (data == null) {
            context.response.end(new JsonObject().toString());
            return;
        }
        context.response.end(data.toString());
    }

    public void search(HieiEndpointContext context) {
        if (context.request.getParam("subChapters") != null && context.request.getParam("subChapters").equals("true")) {
            ArrayList<HieiSearchResult> searchResults = new ArrayList<>();
            for (HieiKeyChapter keyChapter : this.hiei.hieiCache.chapters) {
                for (HieiSubChapter subChapter : keyChapter.subChapters) {
                    HieiSearchResult result = new HieiSearchResult(this.hiei, subChapter).analyzeScore(context.queryString);
                    if (result.score > this.hiei.hieiConfig.editDistance) continue;
                    searchResults.add(result);
                }
            }
            if (searchResults.isEmpty()) {
                context.response.end(new JsonObject().toString());
                return;
            }
            HieiSubChapter data = searchResults.stream()
                    .min((a, b) -> (int) (b.score - a.score))
                    .get()
                    .getSubChapter();
            context.response.end(data.toString());
            return;
        }
         HieiSearchResult data = this.hiei.hieiCache.chapters.stream()
                 .map(keyChapter -> new HieiSearchResult(this.hiei, keyChapter).analyzeScore(context.queryString))
                 .filter(result -> result.score <= this.hiei.hieiConfig.editDistance)
                 .min((a, b) -> (int) (b.score - a.score))
                 .orElse(null);
        if (data == null ) {
            context.response.end(new JsonObject().toString());
            return;
        }
        context.response.end(data.getKeyChapter().toString());
    }
}
