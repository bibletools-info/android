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


    public SearchTextWatcher(ClearAutoCompleteTextView textView, ImageView drawerToggle) {
        this.mTextView = textView;
        this.mDrawerToggle = drawerToggle;

        mDrawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTextView.setText("");
            }
        });
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
            }
        } else {
            if(mState != ARROW){
                mDrawerToggle.setImageResource(R.drawable.ic_arrow_back_grey);
                mState = ARROW;

                mTextView.showClearButton();
            }
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
