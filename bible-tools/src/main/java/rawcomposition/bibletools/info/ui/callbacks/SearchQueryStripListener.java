package rawcomposition.bibletools.info.ui.callbacks;

/**
 * Created by tinashe on 2015/02/17.
 */
public interface SearchQueryStripListener {

    void onSuccess(int book, int chapter, int verse);

    void onError();
}
