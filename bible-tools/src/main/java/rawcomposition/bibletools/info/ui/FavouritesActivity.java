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

import com.afollestad.materialdialogs.MaterialDialog;

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

            switch (item.getItemId()){
                case R.id.action_filter:
                    showFilterOptions();

                    return true;
            }


        return super.onOptionsItemSelected(item);
    }

    private void showFilterOptions(){

        final FavouritesFragment fragment = (FavouritesFragment)
                getSupportFragmentManager().findFragmentByTag(FavouritesFragment.class.getName());

        if(fragment == null){
            return;
        }

        int pstn;

        switch (fragment.getViewType()){
            case OLD_TESTAMENT:
                pstn = 1;
                break;
            case NEW_TESTAMENT:
                pstn = 2;
                break;
            default:
                pstn = 0;
                break;
        }

        String[] arr = {getString(R.string.title_all), getString(R.string.title_ot), getString(R.string.title_nt)};

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder
                .items(arr)
                .itemsCallbackSingleChoice(pstn, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, View view, int position, CharSequence charSequence) {
                        Log.d(TAG, "onSelection() " + position);

                        switch (position){
                            case 1:
                                fragment.setViewType(ViewType.OLD_TESTAMENT);
                                break;
                            case 2:
                                fragment.setViewType(ViewType.NEW_TESTAMENT);
                                break;
                            default:
                                fragment.setViewType(ViewType.ALL);
                                break;
                        }
                        return true;
                    }
                })
                .positiveText(R.string.action_filter)
                .negativeText(R.string.action_cancel)
                .build().show();
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
