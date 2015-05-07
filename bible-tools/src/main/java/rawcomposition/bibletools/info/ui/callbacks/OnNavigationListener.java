package rawcomposition.bibletools.info.ui.callbacks;

/**
 * Created by tinashe on 2015/02/18.
 */
public interface OnNavigationListener {

    void onPrevious(String text);

    void onNext(String text);

    void onVerseClick(String verse);

    void onScrollRequired(int position);
}
