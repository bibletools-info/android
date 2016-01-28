package rawcomposition.bibletools.info.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.actions.SearchIntents;

import org.cryse.widget.persistentsearch.DefaultVoiceRecognizerDelegate;
import org.cryse.widget.persistentsearch.PersistentSearchView;
import org.cryse.widget.persistentsearch.VoiceRecognitionDelegate;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import io.realm.Realm;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.api.BibleToolsService;
import rawcomposition.bibletools.info.custom.CustomSearchListener;
import rawcomposition.bibletools.info.custom.VerseSuggestionBuilder;
import rawcomposition.bibletools.info.model.json.Reference;
import rawcomposition.bibletools.info.model.json.ReferencesResponse;
import rawcomposition.bibletools.info.ui.adapters.ReferenceListAdapter;
import rawcomposition.bibletools.info.ui.callbacks.OnNavigationListener;
import rawcomposition.bibletools.info.ui.callbacks.SearchQueryStripListener;
import rawcomposition.bibletools.info.util.BibleQueryUtil;
import rawcomposition.bibletools.info.util.CacheUtil;
import rawcomposition.bibletools.info.util.DeviceUtil;
import rawcomposition.bibletools.info.util.GSonUtil;
import rawcomposition.bibletools.info.util.PreferenceUtil;
import rawcomposition.bibletools.info.util.TextViewUtil;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends BaseActivity implements
        OnNavigationListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    private static final String TAG = MainActivity.class.getName();

    private static final String VERSE_KEY = "verse";

    private VerseSuggestionBuilder mSearchAdapter;
    private ReferenceListAdapter mAdapter;

    private List<Reference> mReferences = new ArrayList<>();

    private Realm mRealm;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @Bind(R.id.recycler)
    RecyclerView mRecycler;

    @Bind(R.id.progress)
    ProgressBar mProgress;

    @Bind(R.id.swipe_to_refresh)
    SwipeRefreshLayout mSwipeToRefreshLayout;

    @Bind(R.id.searchview)
    PersistentSearchView mSearchView;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRealm = Realm.getDefaultInstance();

        initDrawer();

        initSearchView();

        initRecyclerStuff();

        getSearchIntentData();

        if (!PreferenceUtil.getValue(this, getString(R.string.pref_tut_shown), false)) {
            PreferenceUtil.updateValue(this, getString(R.string.pref_tut_shown), true);
            showHowItWorks();
        }
    }

    private void initDrawer() {

        navigationView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                navigationView.removeOnLayoutChangeListener(this);

                TextView textView = (TextView) findViewById(R.id.app_title);
                TextViewUtil.setCustomFontTitle(MainActivity.this, textView);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.nav_fav:
                        startActivity(new Intent(MainActivity.this, FavouritesActivity.class));
                        break;
                    case R.id.action_settings:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    case R.id.action_feedback:
                        sendFeedBack();
                        break;
                    case R.id.action_help:
                        showHelp();
                        break;
                    case R.id.action_donate:
                        onDonateButtonClicked();
                        break;
                }
                return false;
            }
        });

        //Only Home is checked
        navigationView.getMenu().findItem(R.id.nam_home)
                .setChecked(true);


    }

    private void initSearchView() {
        VoiceRecognitionDelegate delegate = new DefaultVoiceRecognizerDelegate(this, VOICE_RECOGNITION_REQUEST_CODE);
        if (delegate.isVoiceRecognitionAvailable()) {
            mSearchView.setVoiceRecognitionDelegate(delegate);
        }

        mSearchView.setHomeButtonListener(new PersistentSearchView.HomeButtonListener() {
            @Override
            public void onHomeButtonClick() {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mSearchView.setSearchListener(new CustomSearchListener() {
            @Override
            public void onSearch(String query) {
                if (!TextUtils.isEmpty(query)) {
                    if (query.contains(":")) {
                        performQuery(query);
                        mSearchAdapter.refreshSearchHistory();
                    } else {
                        mSearchView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mSearchView.openSearch();
                            }
                        }, 200);
                    }

                }

            }

            @Override
            public void onSearchEditClosed() {
                mSearchAdapter.refreshSearchHistory();
            }
        });
        mSearchAdapter = new VerseSuggestionBuilder(this);
        mSearchView.setSuggestionBuilder(mSearchAdapter);

    }

    private void initRecyclerStuff() {

        if (DeviceUtil.isTablet(this)) {
            StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            mRecycler.setLayoutManager(manager);
        } else {
            mRecycler.setLayoutManager(new LinearLayoutManager(this));
        }
        mAdapter = new ReferenceListAdapter(MainActivity.this, mReferences, this);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setItemAnimator(new DefaultItemAnimator());

        mSwipeToRefreshLayout.setColorSchemeResources(
                R.color.theme_primary_dark,
                R.color.theme_primary_dark,
                R.color.theme_primary_dark,
                R.color.theme_primary_dark
        );
        mSwipeToRefreshLayout.setOnRefreshListener(this);

    }

    private void getSearchIntentData() {
        if (getIntent().getAction() != null) {
            if (getIntent().getAction().equals(SearchIntents.ACTION_SEARCH)) {
                String query = getIntent().getStringExtra(SearchManager.QUERY);
                if (!TextUtils.isEmpty(query)) {
                    performQuery(query);
                    return;
                }
            }
        }

        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            String verse = data.getQueryParameter(VERSE_KEY);

            Log.d(TAG, "VERSE: " + verse);

            if (!TextUtils.isEmpty(verse)) {
                performQuery(verse);
            } else {
                performQuery(CacheUtil.getRecentReference(this));
            }

        } else {
            performQuery(CacheUtil.getRecentReference(this));
        }
    }

    public void displayReferences(ReferencesResponse referencesResponse, boolean smoothScroll) {
        mProgress.setVisibility(View.GONE);

        this.mReferences = referencesResponse.getResources();
        mAdapter.setReferences(mReferences);
        mAdapter.notifyDataSetChanged();

        if (smoothScroll) {

            RecyclerView.LayoutManager manager = mRecycler.getLayoutManager();
            manager.smoothScrollToPosition(mRecycler, null, 0);
        }
    }


    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {

            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            if (!matches.isEmpty()) {
                ArrayList<String> verseMatches = new ArrayList<>();

                for (String item : matches) {
                    for (String book : getResources().getStringArray(R.array.bible_books_full)) {
                        if (book.toLowerCase().contains(item.toLowerCase())) {

                            verseMatches.add(book);
                        }
                    }
                }

                if (verseMatches.isEmpty()) {
                    mSearchView.populateEditText(matches);
                } else {
                    mSearchView.populateEditText(verseMatches);
                }


            }

        }

    }

    public void performQuery(String query) {

        BibleQueryUtil.stripQuery(this, query, new SearchQueryStripListener() {
            @Override
            public void onSuccess(int book, int chapter, int verse) {
                performRequest(book, chapter, verse);
            }

            @Override
            public void onError() {
                showToast(getString(R.string.api_default_error));
            }
        });

    }

    private void performRequest(final int book, final int chapter, final int verse) {

        String jsonString = CacheUtil.find(this, CacheUtil.getFileName(this, book, chapter, verse));

        if (!TextUtils.isEmpty(jsonString)) {

            ReferencesResponse referencesResponse = GSonUtil.getInstance().fromJson(jsonString, ReferencesResponse.class);

            if (referencesResponse != null) {
                displayReferences(referencesResponse, true);

                return;
            }
        }

        if (!DeviceUtil.isConnected(this)) {
            mProgress.setVisibility(View.GONE);
            mSwipeToRefreshLayout.setRefreshing(false);
            showToast(getString(R.string.error_no_connection));
            return;
        }

        mSwipeToRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeToRefreshLayout.setRefreshing(true);
            }
        }, 100);

        Call<ReferencesResponse> call = BibleToolsService.getApi()
                .deliverReferences(book, chapter, verse);
        call.enqueue(new Callback<ReferencesResponse>() {
            @Override
            public void onResponse(Response<ReferencesResponse> response, Retrofit retrofit) {

                mSwipeToRefreshLayout.setRefreshing(false);

                if (!response.isSuccess() || response.body().getResources().isEmpty()
                        || TextUtils.isEmpty(response.body().getResources().get(0).getText())) {
                    showToast(getString(R.string.api_default_error));
                    return;
                }

                if (PreferenceUtil.getValue(MainActivity.this,
                        getString(R.string.pref_key_cache),
                        true)) {

                    CacheUtil.save(MainActivity.this,
                            CacheUtil.getFileName(MainActivity.this, book, chapter, verse),
                            GSonUtil.getInstance().toJson(response.body()));
                }

                displayReferences(response.body(), true);
            }

            @Override
            public void onFailure(Throwable t) {
                mProgress.setVisibility(View.GONE);
                mSwipeToRefreshLayout.setRefreshing(false);

                showToast(getString(R.string.api_default_error));
            }
        });

    }


    @Override
    public void onPrevious(String text) {

        int[] arr = BibleQueryUtil.stripRequest(text);

        if (arr.length == 3) {
            performRequest(arr[0], arr[1], arr[2]);
        }
    }

    @Override
    public void onNext(String text) {
        int[] arr = BibleQueryUtil.stripRequest(text);

        if (arr.length == 3) {
            performRequest(arr[0], arr[1], arr[2]);
        }
    }

    @Override
    public void onVerseClick(String verse) {
        performQuery(verse);
    }

    @Override
    public void onScrollRequired(int position) {

        RecyclerView.LayoutManager manager = mRecycler.getLayoutManager();
        manager.smoothScrollToPosition(mRecycler, null, position);
    }


    private void showHowItWorks() {
        new MaterialDialog.Builder(this)
                .title(R.string.title_how_it_works)
                .content(R.string.msg_how_it_works)
                .positiveText(R.string.action_ok)
                .show();
    }

    @Override
    protected void onDestroy() {
        if (mRealm != null) {
            mRealm.close();
        }
        super.onDestroy();
    }

    public Realm getRealm() {
        return mRealm;
    }

    @Override
    public void onRefresh() {
        mSwipeToRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeToRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }
}
