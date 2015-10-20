package rawcomposition.bibletools.info.custom;

import org.cryse.widget.persistentsearch.PersistentSearchView;

/**
 * Created by tinashe on 2015/10/20.
 */
public abstract class CustomSearchListener implements PersistentSearchView.SearchListener {
    @Override
    public void onSearchCleared() {

    }

    @Override
    public void onSearchTermChanged(String term) {

    }

    @Override
    public void onSearchEditOpened(){

    }

    @Override
    public boolean onSearchEditBackPressed() {
        return false;
    }

    @Override
    public void onSearchExit() {

    }
}
