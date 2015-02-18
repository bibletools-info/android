package rawcomposition.bibletools.info.custom;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import rawcomposition.bibletools.info.R;

/**
 * Created by tinashe on 2015/02/17.
 */
public class SearchTextWatcher implements TextWatcher{

    private static final String TAG = SearchTextWatcher.class.getName();

    private static final int ARROW = 1;
    private static final int BURGER = 2;
    private int mState = BURGER;

    private ClearAutoCompleteTextView mTextView;
    private ImageView mDrawerToggle;

    private View.OnClickListener mDrawerToggleListener;


    public SearchTextWatcher(ClearAutoCompleteTextView textView, ImageView drawerToggle, View.OnClickListener drawerToggleListener) {
        this.mTextView = textView;
        this.mDrawerToggle = drawerToggle;
        this.mDrawerToggleListener = drawerToggleListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if(TextUtils.isEmpty(mTextView.getText())){
            if(mState != BURGER){
                mDrawerToggle.setImageResource(R.drawable.ic_menu_grey);
                mState = BURGER;

                mTextView.hideClearButton();

                mDrawerToggle.setOnClickListener(mDrawerToggleListener);
            }
        } else {
            if(mState != ARROW){
                mDrawerToggle.setImageResource(R.drawable.ic_arrow_back_grey);
                mState = ARROW;

                mTextView.showClearButton();

                mDrawerToggle.setOnClickListener(mClearListener);
            }
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    private View.OnClickListener mClearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mTextView.setText("");
        }
    };
}
