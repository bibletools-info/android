package rawcomposition.bibletools.info.ui;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.WaspError;

import java.util.ArrayList;

import rawcomposition.bibletools.info.BibleToolsApplication;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.api.BibleToolsApi;
import rawcomposition.bibletools.info.custom.ClearAutoCompleteTextView;
import rawcomposition.bibletools.info.custom.SearchTextWatcher;
import rawcomposition.bibletools.info.model.json.References;
import rawcomposition.bibletools.info.ui.callbacks.SearchQueryStripListener;
import rawcomposition.bibletools.info.ui.fragments.ReferencesFragment;
import rawcomposition.bibletools.info.util.BibleQueryUtil;
import rawcomposition.bibletools.info.util.CacheUtil;
import rawcomposition.bibletools.info.util.GSonUtil;
import rawcomposition.bibletools.info.util.KeyBoardUtil;

public class SearchActivity extends BaseActivity {

    private static final int REQUEST_CODE = 1234;

    private static final String TAG = SearchActivity.class.getName();

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search;
    }

    private ClearAutoCompleteTextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ReferencesFragment(), ReferencesFragment.class.getName())
                    .commit();
        }
    }

    private void init(){

        textView = (ClearAutoCompleteTextView) findViewById(R.id.searchView);
        textView.showVoiceButton(mVoiceListener);
        KeyBoardUtil.hideKeyboard(this, textView);
        textView.setVoiceTouchListener(mVoiceListener);
        ImageView imageView = (ImageView) findViewById(R.id.drawerToggle);

        textView.addTextChangedListener(new SearchTextWatcher(textView, imageView));


        textView.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                BibleQueryUtil.getAllQueries(this)));


        textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    KeyBoardUtil.hideKeyboard(SearchActivity.this, textView);

                    if(!TextUtils.isEmpty(textView.getText())){
                        performQuery(textView.getText().toString());
                    }
                }
                return false;
            }
        });

    }

    public View getHideAbleView(){
        return findViewById(R.id.top);
    }



    private View.OnTouchListener mVoiceListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {


            if (textView.getCompoundDrawables()[2] == null)
                return false;

            if (event.getAction() != MotionEvent.ACTION_UP)
                return false;

            if (event.getX() > textView.getWidth() - textView.getPaddingRight()	- textView.mVoiceBtn.getIntrinsicWidth()) {
                startVoiceRecognitionActivity();
            }
            return false;
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
        startActivityForResult(intent, REQUEST_CODE);
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

                            textView.setText(book);
                            textView.setSelection(textView.getText().toString().length());

                            break;
                        }
                    }
                }

                if(!matched){
                    textView.setText(matches.get(0));
                    textView.setSelection(textView.getText().toString().length());
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

    private void performRequest(int book, int chapter, int verse){

        BibleToolsApi api = ((BibleToolsApplication)getApplication())
                .getApi();

        api.deliverReferences(book, chapter, verse, new CallBack<References>() {
            @Override
            public void onSuccess(References response) {

                CacheUtil.save(SearchActivity.this,
                        References.class.getName(),
                        GSonUtil.getInstance().toJson(response));

                ReferencesFragment fragment = (ReferencesFragment)
                        getSupportFragmentManager().findFragmentByTag(ReferencesFragment.class.getName());

                if(fragment != null){
                    fragment.displayReferences(response, true);
                }
            }

            @Override
            public void onError(WaspError waspError) {
                Log.d(TAG, waspError.getErrorMessage());

                showToast(getString(R.string.api_default_error));
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG)
                .show();
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_search, container, false);
            return rootView;
        }
    }
}
