package rawcomposition.bibletools.info.ui;

import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.WaspError;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rawcomposition.bibletools.info.BibleToolsApplication;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.api.BibleToolsApi;
import rawcomposition.bibletools.info.custom.ClearAutoCompleteTextView;
import rawcomposition.bibletools.info.custom.ScrollManager;
import rawcomposition.bibletools.info.custom.SearchTextWatcher;
import rawcomposition.bibletools.info.model.json.Reference;
import rawcomposition.bibletools.info.model.json.ReferencesResponse;
import rawcomposition.bibletools.info.ui.adapters.ReferenceListAdapter;
import rawcomposition.bibletools.info.ui.callbacks.OnNavigationListener;
import rawcomposition.bibletools.info.ui.callbacks.SearchQueryStripListener;
import rawcomposition.bibletools.info.ui.fragments.NavigationDrawerFragment;
import rawcomposition.bibletools.info.util.BibleQueryUtil;
import rawcomposition.bibletools.info.util.CacheUtil;
import rawcomposition.bibletools.info.util.DeviceUtil;
import rawcomposition.bibletools.info.util.GSonUtil;
import rawcomposition.bibletools.info.util.KeyBoardUtil;
import rawcomposition.bibletools.info.util.PreferenceUtil;

public class MainActivity extends BaseActivity implements
        OnNavigationListener, NavigationDrawerFragment.NavigationDrawerCallbacks{

    private static final int REQUEST_CODE = 1234;

    private static final String TAG = MainActivity.class.getName();

    private static final String VERSE_KEY = "verse";

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    private ClearAutoCompleteTextView mSearchView;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private ReferenceListAdapter mAdapter;

    private ProgressBar mProgress;

    private List<Reference> mReferences = new ArrayList<>();

    private RecyclerView mRecycler;

    private Realm mRealm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRealm = Realm.getInstance(this);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        initSearchView();

        initRecyclerStuff();

        if(!PreferenceUtil.getValue(this, getString(R.string.pref_tut_shown), false)){
            PreferenceUtil.updateValue(this, getString(R.string.pref_tut_shown), true);
            showHowItWorks();
        }
    }

    private void initSearchView(){

        mSearchView = (ClearAutoCompleteTextView) findViewById(R.id.searchView);
        mSearchView.showVoiceButton(mVoiceListener);
        mSearchView.setVoiceTouchListener(mVoiceListener);
        ImageView imageView = (ImageView) findViewById(R.id.drawerToggle);
        imageView.setOnClickListener(mDrawerToggleListener);

        mSearchView.addTextChangedListener(new SearchTextWatcher(mSearchView, imageView, mDrawerToggleListener));


        mSearchView.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                BibleQueryUtil.getAllQueries(this)));


        mSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    KeyBoardUtil.hideKeyboard(MainActivity.this, mSearchView);

                    if (!TextUtils.isEmpty(mSearchView.getText())) {
                        performQuery(mSearchView.getText().toString().trim());
                    }
                }
                return false;
            }
        });

    }

   private void initRecyclerStuff(){
       mHeaderView = findViewById(R.id.header);

       mRecycler = (RecyclerView) findViewById(R.id.recycler);

       if(DeviceUtil.isTablet(this)){
           StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
           manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
           mRecycler.setLayoutManager(manager);

       } else {
           mRecycler.setLayoutManager(new LinearLayoutManager(this));
       }

       mProgress = (ProgressBar) findViewById(R.id.progress);

       mAdapter = new ReferenceListAdapter(MainActivity.this, mReferences, this);
       mRecycler.setAdapter(mAdapter);

      // ScrollManager manager = new ScrollManager();
     //  manager.addView(mHeaderView, ScrollManager.Direction.UP);
      // manager.attach(mRecycler);


       Intent intent = getIntent();
       Uri data = intent.getData();

       if(data != null){
           String verse = data.getQueryParameter(VERSE_KEY);

           Log.d(TAG, "VERSE: " + verse);

           if(!TextUtils.isEmpty(verse)){
               performQuery(verse);
           } else {
               performQuery(CacheUtil.getRecentReference(this));
           }

       } else {
           performQuery(CacheUtil.getRecentReference(this));
       }

   }


    public void displayReferences(ReferencesResponse referencesResponse, boolean smoothScroll){
        mProgress.setVisibility(View.GONE);

        this.mReferences = referencesResponse.getResources();
        mAdapter.setReferences(mReferences);
        mAdapter.notifyDataSetChanged();

        if(smoothScroll){
            mRecycler.smoothScrollToPosition(0);
        }
    }



    private View.OnTouchListener mVoiceListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {


            if (mSearchView.getCompoundDrawables()[2] == null)
                return false;

            if (event.getAction() != MotionEvent.ACTION_UP)
                return false;

            if (event.getX() > mSearchView.getWidth() - mSearchView.getPaddingRight()	- mSearchView.mVoiceBtn.getIntrinsicWidth()) {
                startVoiceRecognitionActivity();
            }
            return false;
        }
    };


    private View.OnClickListener mDrawerToggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            KeyBoardUtil.hideKeyboard(MainActivity.this, mSearchView);
            mNavigationDrawerFragment.toggleDrawer();
        }
    };

    /**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening...");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            showToast("Speech not supported");
        }
    }

    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {

            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            if(!matches.isEmpty()){
                boolean matched = false;

                for(String item: matches){
                    for(String book: getResources().getStringArray(R.array.bible_books_full)){
                        if (book.toLowerCase().contains(item.toLowerCase())){
                            matched = true;

                            mSearchView.setText(book);
                            mSearchView.setSelection(mSearchView.getText().toString().length());

                            break;
                        }
                    }
                }

                if(!matched){
                    mSearchView.setText(matches.get(0));
                    mSearchView.setSelection(mSearchView.getText().toString().length());
                }

            }

        }

    }

    public void performQuery(String query){

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

    private void performRequest(final int book, final int chapter, final int verse){

        String jsonString = CacheUtil.find(this, CacheUtil.getFileName(this, book, chapter, verse));

        if(!TextUtils.isEmpty(jsonString)){

            ReferencesResponse referencesResponse = GSonUtil.getInstance().fromJson(jsonString, ReferencesResponse.class);

            if(referencesResponse != null){
                displayReferences(referencesResponse, true);

                return;
            }
        }

        if(!DeviceUtil.isConnected(this)){
            showToast(getString(R.string.error_no_connection));
            return;
        }

        BibleToolsApi api = ((BibleToolsApplication)getApplication())
                .getApi();

        api.deliverReferences(book, chapter, verse, new CallBack<ReferencesResponse>() {
            @Override
            public void onSuccess(ReferencesResponse response) {

                if(PreferenceUtil.getValue(MainActivity.this,
                        getString(R.string.pref_key_cache),
                        true)){

                    CacheUtil.save(MainActivity.this,
                            CacheUtil.getFileName(MainActivity.this, book, chapter, verse),
                            GSonUtil.getInstance().toJson(response));
                }

                 displayReferences(response, true);

            }

            @Override
            public void onError(WaspError waspError) {
                Log.d(TAG, waspError.getErrorMessage());
                mProgress.setVisibility(View.GONE);

                showToast(getString(R.string.api_default_error));
            }
        });
    }



    @Override
    public void onPrevious(String text) {

       int[] arr = BibleQueryUtil.stripRequest(text);

        if(arr.length == 3){
            KeyBoardUtil.hideKeyboard(this, mSearchView);
            performRequest(arr[0], arr[1], arr[2]);
        }
    }

    @Override
    public void onNext(String text) {
        int[] arr = BibleQueryUtil.stripRequest(text);

        if(arr.length == 3){
            KeyBoardUtil.hideKeyboard(this, mSearchView);
            performRequest(arr[0], arr[1], arr[2]);
        }
    }

    @Override
    public void onVerseClick(String verse){
        performQuery(verse);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Log.d(TAG, "Position: " + position);

        switch (position){
            case 1:
                //Favourites
                startActivity(new Intent(this, FavouritesActivity.class));
                break;
            case 2:
                //History
                showHistoryDialog(CacheUtil.getCachedReferences(this));
               break;
            case 4:
                //Settings
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case 5:
                //Help
                showHelp();
                break;
            case 6:
                //Feedback
                sendFeedBack();
                break;
        }
    }

    private void showHistoryDialog(List<String> history){

        int max = Integer.valueOf(PreferenceUtil.getValue(this,
                getString(R.string.pref_key_history_entries),
                "10"));

        if(history.size() > max){
            history = history.subList(0, max);
        }

        final List<String> list = history;

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(R.string.title_history)
                .titleColorRes(R.color.theme_primary)
                .iconRes(R.drawable.ic_history_color)
                .items(list.toArray(new String[list.size()]))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int position, CharSequence charSequence) {

                        performQuery(list.get(position));
                    }
                })
                .positiveText(R.string.action_cancel)
                .show();
    }



    private void showHowItWorks(){
        new MaterialDialog.Builder(this)
                .title(R.string.title_how_it_works)
                .content(R.string.msg_how_it_works)
                .positiveText(R.string.action_ok)
                .show();
    }

    @Override
    protected void onDestroy() {
        mRealm.close();
        super.onDestroy();
    }

    public Realm getRealm() {
        return mRealm;
    }
}
