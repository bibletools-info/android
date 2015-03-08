package rawcomposition.bibletools.info.ui;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import io.realm.Realm;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.ui.fragments.FavouritesFragment;
import rawcomposition.bibletools.info.util.enums.ViewType;

public class FavouritesActivity extends BaseActivity {

    private static final String TAG = FavouritesActivity.class.getName();

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_favourites;
    }

    private Realm mRealm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRealm = Realm.getInstance(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FavouritesFragment(), FavouritesFragment.class.getName())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favourites, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setQueryHint(getString(R.string.action_search));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                FavouritesFragment fragment = (FavouritesFragment)
                        getSupportFragmentManager().findFragmentByTag(FavouritesFragment.class.getName());

                if(fragment != null){
                    fragment.performSearch(s);
                }

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FavouritesFragment fragment = (FavouritesFragment)
                getSupportFragmentManager().findFragmentByTag(FavouritesFragment.class.getName());

        if(fragment != null){

            switch (item.getItemId()){
                case R.id.action_show_all:
                    fragment.setViewType(ViewType.ALL);

                    return true;
                case R.id.action_ot:
                    fragment.setViewType(ViewType.OLD_TESTAMENT);

                    return true;
                case R.id.action_nt:
                    fragment.setViewType(ViewType.NEW_TESTAMENT);

                    return true;

                case R.id.action_filter:
                    Menu menu = item.getSubMenu();
                    switch (fragment.getViewType()){
                        case ALL:
                            menu.findItem(R.id.action_show_all)
                                    .setChecked(true);
                            return true;
                        case NEW_TESTAMENT:

                            menu.findItem(R.id.action_nt)
                                    .setChecked(true);
                            break;
                        case OLD_TESTAMENT:
                            menu.findItem(R.id.action_ot)
                                    .setChecked(true);
                            break;
                    }
                    return true;
            }
        }


        return super.onOptionsItemSelected(item);
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
