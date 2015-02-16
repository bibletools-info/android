package rawcomposition.bibletools.info.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import rawcomposition.bibletools.info.R;

/**
 * Created by tinashe
 */
public abstract class BaseActivity extends ActionBarActivity {

    protected abstract int getLayoutResource();

    protected Toolbar mToolbar;

    protected View mHeaderView;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        setContentView(getLayoutResource());

        mToolbar = (Toolbar) findViewById(R.id.app_action_bar);
        mHeaderView = findViewById(R.id.header_view);
        if (mToolbar != null) {
            try{
                ViewCompat.setTranslationZ(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
                ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));

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
}
