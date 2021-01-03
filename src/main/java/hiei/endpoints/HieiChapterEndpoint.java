package hiei.endpoints;

import com.google.gson.GsonBuilder;
import hiei.HieiServer;
import hiei.struct.*;
import com.google.gson.JsonObject;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.util.ArrayList;
import java.util.Comparator;

public class HieiChapterEndpoint {
    private HieiServer hiei;

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
             context.response.end(new GsonBuilder().setPrettyPrinting().create().toJson(subData.data));
             return;
        }
        HieiKeyChapter data = this.hiei.hieiCache.chapters.get(Integer.parseInt(query[0]) - 1);
        if (data == null) {
            context.response.end(new JsonObject().toString());
            return;
        }
        context.response.end(new GsonBuilder().setPrettyPrinting().create().toJson(data.data));
    }

    public void search(HieiEndpointContext context) {
        if (context.request.getParam("subChapters") != null && context.request.getParam("subChapters").equals("true")) {
            ArrayList<HieiSearchResult> searchResults = new ArrayList<>();
            for (HieiKeyChapter keyChapter : this.hiei.hieiCache.chapters) {
                for (HieiSubChapter subChapter : keyChapter.subChapters) {
                    HieiSearchResult result = new HieiSearchResult(FuzzySearch.weightedRatio(subChapter.name, context.queryString), subChapter);
                    if (result.score < this.hiei.hieiConfig.searchWeight) continue;
                    searchResults.add(result);
                }
            }
            if (searchResults.isEmpty()) {
                context.response.end(new JsonObject().toString());
                return;
            }
            HieiSubChapter subChapter = searchResults.stream()
                    .max(Comparator.comparing(HieiSearchResult::getScore))
                    .get()
                    .getSubChapter();
            context.response.end(new GsonBuilder().setPrettyPrinting().create().toJson(subChapter.data));
            return;
        }
         HieiSearchResult data = this.hiei.hieiCache.chapters.stream()
                .map(keyChapter -> new HieiSearchResult(FuzzySearch.weightedRatio(keyChapter.name, context.queryString), keyChapter))
                .filter(result ->  result.score > this.hiei.hieiConfig.searchWeight)
                .max(Comparator.comparing(HieiSearchResult::getScore))
                .orElse(null);
        if (data == null ) {
            context.response.end(new JsonObject().toString());
            return;
        }
        context.response.end(new GsonBuilder().setPrettyPrinting().create().toJson(data.getKeyChapter().data));
    }
}
