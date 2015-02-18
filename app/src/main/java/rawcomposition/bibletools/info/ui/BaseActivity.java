package rawcomposition.bibletools.info.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

import rawcomposition.bibletools.info.R;

/**
 * Created by tinashe
 */
public abstract class BaseActivity extends ActionBarActivity {

    // Durations for certain animations we use:
    private static final int HEADER_HIDE_ANIM_DURATION = 300;

    protected abstract int getLayoutResource();

    protected Toolbar mToolbar;

    protected View mHeaderView;

    // When set, these components will be shown/hidden in sync with the action bar
    // to implement the "quick recall" effect (the Action Bar and the header views disappear
    // when you scroll down a list, and reappear quickly when you scroll up).
    private ArrayList<View> mHideableHeaderViews = new ArrayList<View>();

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        setContentView(getLayoutResource());

        mToolbar = (Toolbar) findViewById(R.id.app_action_bar);
        mHeaderView = findViewById(R.id.header_view);
        if (mToolbar != null) {
            try{

                if(mHeaderView != null){
                    ViewCompat.setTranslationZ(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
                    ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
                }


                setSupportActionBar(mToolbar);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }catch (Throwable ex){

            }

        }
    }


    public Toolbar getToolbar(){
        return mToolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void hideActionBar(){
        onActionBarAutoShowOrHide(false);
    }

    public void showActionBar(){
        onActionBarAutoShowOrHide(true);
    }

    protected void registerHideableHeaderView(View hideableHeaderView) {
        if (!mHideableHeaderViews.contains(hideableHeaderView)) {
            mHideableHeaderViews.add(hideableHeaderView);
        }
    }

    protected void deregisterHideableHeaderView(View hideableHeaderView) {
        if (mHideableHeaderViews.contains(hideableHeaderView)) {
            mHideableHeaderViews.remove(hideableHeaderView);
        }
    }

    protected void onActionBarAutoShowOrHide(boolean shown) {

        for (View view : mHideableHeaderViews) {
            if (shown) {
                ViewPropertyAnimator.animate(view)
                        .translationY(0)
                        .alpha(1)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            } else {
                ViewPropertyAnimator.animate(view)
                        .translationY(-view.getBottom())
                        .alpha(0)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            }
        }
    }
}
