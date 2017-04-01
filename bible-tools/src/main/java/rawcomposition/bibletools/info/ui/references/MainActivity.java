package rawcomposition.bibletools.info.ui.references;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.actions.SearchIntents;

import org.cryse.widget.persistentsearch.DefaultVoiceRecognizerDelegate;
import org.cryse.widget.persistentsearch.PersistentSearchView;
import org.cryse.widget.persistentsearch.VoiceRecognitionDelegate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.custom.CustomSearchListener;
import rawcomposition.bibletools.info.custom.VerseSuggestionBuilder;
import rawcomposition.bibletools.info.model.ReferenceMap;
import rawcomposition.bibletools.info.model.json.Reference;
import rawcomposition.bibletools.info.model.json.ReferencesRequest;
import rawcomposition.bibletools.info.ui.FavouritesActivity;
import rawcomposition.bibletools.info.ui.MapDetailActivity;
import rawcomposition.bibletools.info.ui.SettingsActivity;
import rawcomposition.bibletools.info.ui.VersePickerActivity;
import rawcomposition.bibletools.info.ui.adapters.ReferenceListAdapter;
import rawcomposition.bibletools.info.ui.base.BaseActivity;
import rawcomposition.bibletools.info.ui.callbacks.OnNavigationListener;
import rawcomposition.bibletools.info.util.CacheUtil;
import rawcomposition.bibletools.info.util.DeviceUtil;
import rawcomposition.bibletools.info.util.TextViewUtil;
import rawcomposition.bibletools.info.util.enums.BundledExtras;

public class MainActivity extends BaseActivity implements ReferencesContract.View,
        OnNavigationListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private static final int VERSE_PICKER_REQUEST_CODE = 1235;

    private static final String TAG = MainActivity.class.getName();

    public static final String VERSE_KEY = "verse";

    private VerseSuggestionBuilder mSearchAdapter;
    private ReferenceListAdapter mAdapter;

    private Realm mRealm;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    @BindView(R.id.progress)
    ProgressBar mProgress;

    @BindView(R.id.swipe_to_refresh)
    SwipeRefreshLayout mSwipeToRefreshLayout;

    @BindView(R.id.searchview)
    PersistentSearchView mSearchView;

    @BindView(R.id.hint_label)
    TextView hintLabel;

    @BindView(R.id.dialog_picker)
    ImageButton dialogPicker;

    private ReferencesPresenter presenter;

    @Override
    protected int layoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean showHomeAsUp() {
        return false;
    }

    @Override
    public void hookUpPresenter() {
        mRealm = Realm.getDefaultInstance();

        presenter = new ReferencesPresenter(this);
        presenter.startPresenting();
    }

    @Override
    public void initDrawer() {

        View view = navigationView.getHeaderView(0);
        TextView textView = ButterKnife.findById(view, R.id.app_title);
        TextViewUtil.setCustomFontTitle(MainActivity.this, textView);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
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
                    showToast("Select browser...");
                    showWebUrl(getString(R.string.app_help));
                    break;
                case R.id.action_donate:
                    onDonateButtonClicked();
                    break;
            }
            return false;
        });

        //Only Home is checked
        navigationView.getMenu().findItem(R.id.nam_home)
                .setChecked(true);

    }

    @Override
    public void initSearchView() {
        VoiceRecognitionDelegate delegate = new DefaultVoiceRecognizerDelegate(this, VOICE_RECOGNITION_REQUEST_CODE);
        if (delegate.isVoiceRecognitionAvailable()) {
            mSearchView.setVoiceRecognitionDelegate(delegate);
        }

        mSearchView.setHomeButtonListener(() -> mDrawerLayout.openDrawer(GravityCompat.START));

        mSearchView.setSearchListener(new CustomSearchListener() {

            @Override
            public void onSearch(String query) {
                if (!TextUtils.isEmpty(query)) {
                    if (query.contains(":")) {
                        presenter.performQuery(query);
                        mSearchAdapter.refreshSearchHistory();
                    } else {
                        mSearchView.postDelayed(() -> mSearchView.openSearch(), 200);
                    }
                }
            }

            @Override
            public void onSearchEditOpened() {
                dialogPicker.setVisibility(View.GONE);
                hintLabel.setVisibility(View.GONE);
            }

            @Override
            public void onSearchEditClosed() {

                if (TextUtils.isEmpty(mSearchView.getSearchText())) {
                    hintLabel.setVisibility(View.VISIBLE);
                    dialogPicker.setVisibility(View.VISIBLE);
                }
                mSearchAdapter.refreshSearchHistory();
            }
        });

        mSearchAdapter = new VerseSuggestionBuilder(this);
        mSearchView.setSuggestionBuilder(mSearchAdapter);

    }

    @Override
    public void setUpRecyclerView() {
        if (DeviceUtil.isTablet(this)) {
            StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL);
            manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            mRecycler.setLayoutManager(manager);
        } else {
            mRecycler.setLayoutManager(new LinearLayoutManager(this));
        }
        mAdapter = new ReferenceListAdapter(this);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setItemAnimator(new DefaultItemAnimator());

        mSwipeToRefreshLayout.setColorSchemeResources(R.color.theme_primary_dark);
        mSwipeToRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void getLaunchIntent() {
        if (getIntent().getAction() != null) {
            if (getIntent().getAction().equals(SearchIntents.ACTION_SEARCH)) {
                String query = getIntent().getStringExtra(SearchManager.QUERY);
                if (!TextUtils.isEmpty(query)) {
                    presenter.performQuery(query);
                    return;
                }
            }
        }

        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            Log.d(TAG, "PATH: " + data.getEncodedPath());
            String verse = data.getEncodedPath().replace("/", "").replace("_", " ");

            if (TextUtils.isEmpty(verse)) {
                presenter.performQuery(CacheUtil.getRecentReference(this));
                return;
            }

            String bkNum = verse.substring(0, 1);
            if (TextUtils.isDigitsOnly(bkNum)) {
                verse = verse.replaceFirst(bkNum, bkNum + " ");
            }

            presenter.performQuery(verse);

        } else if (intent.hasExtra(VERSE_KEY)) {
            String verse = intent.getStringExtra(VERSE_KEY);
            if (!TextUtils.isEmpty(verse)) {
                presenter.performQuery(verse);
            } else {
                presenter.performQuery(CacheUtil.getRecentReference(this));
            }
        } else {
            presenter.performQuery(CacheUtil.getRecentReference(this));
        }

    }

    @OnClick(R.id.dialog_picker)
    public void dialogPickerClicked() {
        startActivityForResult(new Intent(this, VersePickerActivity.class),
                VERSE_PICKER_REQUEST_CODE);
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

        } else if (requestCode == VERSE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            ReferencesRequest request = (ReferencesRequest)
                    data.getSerializableExtra(BundledExtras.DATA_OBJECT.name());

            if (request != null) {
                presenter.performRequest(request.getBook(), request.getChapter(), request.getVerse());
            }
        }
    }


    @Override
    public void onPrevious(String text) {

        presenter.previousClicked(text);
    }

    @Override
    public void onNext(String text) {
        presenter.nextClicked(text);
    }

    @Override
    public void onVerseClick(String verse) {
        presenter.performQuery(verse);
    }

    @Override
    public void onScrollRequired(int position) {

        RecyclerView.LayoutManager manager = mRecycler.getLayoutManager();
        manager.smoothScrollToPosition(mRecycler, null, position);
    }

    @Override
    public void onMapSelected(View view, ReferenceMap map) {
        MapDetailActivity.launch(this, view, map);
    }


    @Override
    public void showHowItWorks() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_how_it_works)
                .setMessage(R.string.msg_how_it_works)
                .setPositiveButton(R.string.action_ok, null)
                .show();
    }

    @Override
    public void showProgress(boolean show) {
        mSwipeToRefreshLayout.setRefreshing(show);
    }

    @Override
    public void showReferences(List<Reference> resources, boolean smoothScroll) {
        mProgress.setVisibility(View.GONE);

        mAdapter.setReferences(resources);

        if (smoothScroll) {

            RecyclerView.LayoutManager manager = mRecycler.getLayoutManager();
            manager.smoothScrollToPosition(mRecycler, null, 0);
        }
    }

    @Override
    public void showError(@StringRes int errorRes) {
        showToast(getString(errorRes));
    }

    @Override
    public void showError(String error) {
        showToast(error);

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
        mSwipeToRefreshLayout.postDelayed(() -> mSwipeToRefreshLayout.setRefreshing(false), 1000);
    }
}
