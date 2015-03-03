package rawcomposition.bibletools.info.ui.callbacks;

/**
 * Created by tinashe on 2015/02/18.
 */
public interface OnNavigationListener {

    public abstract void onPrevious(String text);

    public abstract void onNext(String text);

    public abstract void onVerseClick(String verse);
}
