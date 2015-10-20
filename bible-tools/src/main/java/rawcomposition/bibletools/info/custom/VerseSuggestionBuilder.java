package rawcomposition.bibletools.info.custom;

import android.content.Context;

import org.cryse.widget.persistentsearch.SearchItem;
import org.cryse.widget.persistentsearch.SearchSuggestionsBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import rawcomposition.bibletools.info.util.BibleQueryUtil;
import rawcomposition.bibletools.info.util.CacheUtil;

/**
 * Created by tinashe on 2015/10/20.
 */
public class VerseSuggestionBuilder implements SearchSuggestionsBuilder {

    private Context mContext;
    List<SearchItem> emptySuggestions;

    List<String> queries;

    public VerseSuggestionBuilder(Context context) {
        this.mContext = context;
        refreshSearchHistory();
        queries = BibleQueryUtil.getAllQueries(mContext);
    }

    @Override
    public Collection<SearchItem> buildEmptySearchSuggestion(int maxCount) {

        return emptySuggestions;
    }

    @Override
    public Collection<SearchItem> buildSearchSuggestion(int maxCount, String query) {

        List<String> options = new ArrayList<>();
        ArrayList<SearchItem> suggestions = new ArrayList<>();
        for (String q : queries) {
            if (q.toLowerCase().contains(query.toLowerCase())) {
                options.add(q);
            }
        }

        if (!options.isEmpty()) {
            Collections.sort(options);
            for (String item : options) {
                SearchItem searItem = new SearchItem(item, item, SearchItem.TYPE_SEARCH_ITEM_SUGGESTION);
                suggestions.add(searItem);
            }
        }

        return suggestions;
    }

    public void refreshSearchHistory() {
        List<String> history = CacheUtil.getCachedReferences(mContext);
        emptySuggestions = new ArrayList<>();
        for (String item : history) {
            SearchItem searItem = new SearchItem(item, item, SearchItem.TYPE_SEARCH_ITEM_HISTORY);
            emptySuggestions.add(searItem);
        }
    }

}
