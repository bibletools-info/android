package rawcomposition.bibletools.info.ui.base;

/**
 * Created by tinashe on 2016/11/21.
 */

public interface BaseView extends ProgressView {

    /**
     * Concrete class should initialise respective Presenters in this method
     */
    void hookUpPresenter();

    void showAlert(String message);

    void showAlert(String message, boolean finishActivity);

    void hideAlert();
}
