package rawcomposition.bibletools.info.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.util.ThemeUtil;

/**
 * Created by tinashe on 2015/02/17.
 */
public class SearchTextWatcher implements TextWatcher {

    @SuppressWarnings("unused")
    private static final String TAG = SearchTextWatcher.class.getName();

    private static final int ARROW = 1;
    private static final int BURGER = 2;
    private int mState = BURGER;

    private ClearAutoCompleteTextView mTextView;
    private ImageView mDrawerToggle;

    private Drawable mBurgerDrawable;
    private Drawable mArrowDrawable;

    private View.OnClickListener mDrawerToggleListener;
    private View.OnClickListener mClearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mTextView.setText("");
        }
    };

    public SearchTextWatcher(ClearAutoCompleteTextView textView, ImageView drawerToggle, View.OnClickListener drawerToggleListener) {
        this.mTextView = textView;
        this.mDrawerToggle = drawerToggle;
        this.mDrawerToggleListener = drawerToggleListener;

        initDrawables();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (TextUtils.isEmpty(mTextView.getText())) {
            if (mState != BURGER) {
                mDrawerToggle.setImageDrawable(mBurgerDrawable);
                mState = BURGER;

                mTextView.hideClearButton();

                mDrawerToggle.setOnClickListener(mDrawerToggleListener);
            }
        } else {
            if (mState != ARROW) {
                mDrawerToggle.setImageDrawable(mArrowDrawable);
                mState = ARROW;

                mTextView.showClearButton();

                mDrawerToggle.setOnClickListener(mClearListener);
            }
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void initDrawables() {
        Context context = mTextView.getContext();

        mBurgerDrawable = ResourcesCompat.getDrawable(
                context.getResources(),
                R.drawable.ic_menu_grey,
                context.getTheme());
        mArrowDrawable = ResourcesCompat.getDrawable(
                context.getResources(),
                R.drawable.ic_arrow_back_grey,
                context.getTheme());


        if (ThemeUtil.isDarkTheme(mTextView.getContext())) {

            ThemeUtil.tintDrawable(mBurgerDrawable, Color.WHITE);
            ThemeUtil.tintDrawable(mArrowDrawable, Color.WHITE);
        }

        mTextView.initDrawables();
    }
}
