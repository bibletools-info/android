package rawcomposition.bibletools.info.custom;

import android.content.Context;

import org.cryse.widget.persistentsearch.SearchItem;
import org.cryse.widget.persistentsearch.SearchSuggestionsBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.util.BibleQueryUtil;
import rawcomposition.bibletools.info.util.CacheUtil;
import rawcomposition.bibletools.info.util.PreferenceUtil;

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
        int max = Integer.valueOf(
                PreferenceUtil.getValue(mContext, mContext.getString(R.string.pref_key_history_entries), "5")
        );

        List<String> history = CacheUtil.getCachedReferences(mContext);
        emptySuggestions = new ArrayList<>();
        for (String item : history) {
            if (emptySuggestions.size() == max) {
                break;
            }
            SearchItem searItem = new SearchItem(item, item, SearchItem.TYPE_SEARCH_ITEM_HISTORY);
            emptySuggestions.add(searItem);
        }
    }

}
