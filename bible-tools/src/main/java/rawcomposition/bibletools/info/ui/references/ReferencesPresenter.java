package rawcomposition.bibletools.info.ui.references;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import rawcomposition.bibletools.info.BibleToolsApp;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.api.BibleToolsApi;
import rawcomposition.bibletools.info.api.BibleToolsService;
import rawcomposition.bibletools.info.model.json.Reference;
import rawcomposition.bibletools.info.model.json.ReferencesResponse;
import rawcomposition.bibletools.info.rx.SchedulerProvider;
import rawcomposition.bibletools.info.ui.callbacks.SearchQueryStripListener;
import rawcomposition.bibletools.info.util.BibleQueryUtil;
import rawcomposition.bibletools.info.util.CacheUtil;
import rawcomposition.bibletools.info.util.GSonUtil;
import rawcomposition.bibletools.info.util.PreferenceUtil;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by tinashe on 2017/04/01.
 */

class ReferencesPresenter implements ReferencesContract.Presenter {

    private static final String TAG = ReferencesPresenter.class.getName();

    private final BibleToolsApi bibleToolsApi = BibleToolsService.createService(BibleToolsApi.class);

    private final ReferencesContract.View view;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    ReferencesPresenter(ReferencesContract.View view) {
        this.view = view;
    }

    private Context getContext() {
        return BibleToolsApp.getAppContext();
    }

    @Override
    public void startPresenting() {

        view.initDrawer();

        view.initSearchView();

        view.setUpRecyclerView();

        view.getLaunchIntent();

        if (!PreferenceUtil.getValue(getContext(), getContext().getString(R.string.pref_tut_shown), false)) {
            PreferenceUtil.updateValue(getContext(), getContext().getString(R.string.pref_tut_shown), true);
            view.showHowItWorks();
        }

    }

    @Override
    public void clearSubscriptions() {
        if (compositeSubscription.hasSubscriptions()) {
            compositeSubscription.clear();
        }
    }

    @Override
    public void resubscribeIfNeeded() {

    }

    @Override
    public void performQuery(String query) {
        BibleQueryUtil.stripQuery(BibleToolsApp.getAppContext(), query, new SearchQueryStripListener() {
            @Override
            public void onSuccess(int book, int chapter, int verse) {
                performRequest(book, chapter, verse);
            }

            @Override
            public void onError() {
                view.showError(R.string.api_default_error);
                performRequest(1, 1, 1);
            }
        });
    }

    @Override
    public void previousClicked(String query) {
        int[] arr = BibleQueryUtil.stripRequest(query);

        if (arr.length == 3) {
            performRequest(arr[0], arr[1], arr[2]);
        }
    }

    @Override
    public void nextClicked(String query) {
        int[] arr = BibleQueryUtil.stripRequest(query);

        if (arr.length == 3) {
            performRequest(arr[0], arr[1], arr[2]);
        }
    }

    @Override
    public void performRequest(int book, int chapter, int verse) {
        String jsonString = CacheUtil.find(getContext(),
                CacheUtil.getFileName(getContext(), book, chapter, verse));

        if (!TextUtils.isEmpty(jsonString)) {

            ReferencesResponse referencesResponse = GSonUtil.getInstance()
                    .fromJson(jsonString, ReferencesResponse.class);

            if (referencesResponse != null) {
                view.showReferences(referencesResponse.getResources(), true);

                return;
            }
        }

        Subscription subscription = bibleToolsApi
                .deliverReferences(book, chapter, verse)
                .compose(SchedulerProvider.DEFAULT.applySchedulers())
                .doOnSubscribe(() -> view.showProgress(true))
                .doOnTerminate(() -> view.showProgress(false))
                .subscribe(referencesResponse -> {

                    List<Reference> references = referencesResponse.getResources();

                    if (references == null || references.isEmpty()
                            || TextUtils.isEmpty(references.get(0).getText())) {

                        view.showError(R.string.api_default_error);
                        return;

                    }

                    if (PreferenceUtil.getValue(getContext(),
                            getContext().getString(R.string.pref_key_cache), true)) {

                        CacheUtil.save(getContext(),
                                CacheUtil.getFileName(getContext(), book, chapter, verse),
                                GSonUtil.getInstance().toJson(referencesResponse));
                    }

                    view.showReferences(references, true);


                }, throwable -> {
                    Log.e(TAG, throwable.getMessage(), throwable);
                });

        compositeSubscription.add(subscription);
    }
}
