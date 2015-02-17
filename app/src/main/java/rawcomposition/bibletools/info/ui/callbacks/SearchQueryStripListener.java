package rawcomposition.bibletools.info.ui.callbacks;

/**
 * Created by tinashe on 2015/02/17.
 */
public interface SearchQueryStripListener {

    public abstract void onSuccess(int book, int chapter, int verse);

    public abstract void onError();
}
