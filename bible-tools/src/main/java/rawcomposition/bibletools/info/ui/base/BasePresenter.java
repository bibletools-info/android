package rawcomposition.bibletools.info.ui.base;

/**
 * Created by tinashe on 2016/12/21.
 */

public interface BasePresenter {

    void startPresenting();

    void clearSubscriptions();

    void resubscribeIfNeeded();
}
