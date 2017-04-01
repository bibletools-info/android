package rawcomposition.bibletools.info.ui.references;

import android.support.annotation.StringRes;

import java.util.List;

import rawcomposition.bibletools.info.model.json.Reference;
import rawcomposition.bibletools.info.ui.base.BasePresenter;
import rawcomposition.bibletools.info.ui.base.BaseView;

/**
 * Created by tinashe on 2017/04/01.
 */

public interface ReferencesContract {

    interface View extends BaseView {

        void initDrawer();

        void initSearchView();

        void setUpRecyclerView();

        void getLaunchIntent();

        void showHowItWorks();

        void showProgress(boolean show);

        void showReferences(List<Reference> resources, boolean refresh);

        void showError(@StringRes int errorRes);

        void showError(String error);

    }

    interface Presenter extends BasePresenter {

        void performQuery(String query);

        void previousClicked(String query);

        void nextClicked(String query);

        void performRequest(int book, int chapter, int verse);

    }
}
