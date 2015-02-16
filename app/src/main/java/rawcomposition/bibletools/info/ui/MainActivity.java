package rawcomposition.bibletools.info.ui;

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.WaspError;

import java.util.List;

import rawcomposition.bibletools.info.BibleToolsApplication;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.api.BibleToolsApi;
import rawcomposition.bibletools.info.model.QueryObject;
import rawcomposition.bibletools.info.model.json.References;
import rawcomposition.bibletools.info.ui.adapters.SearchCursorAdapter;
import rawcomposition.bibletools.info.ui.fragments.NavigationDrawerFragment;
import rawcomposition.bibletools.info.ui.fragments.ReferencesFragment;
import rawcomposition.bibletools.info.util.BibleQueryUtil;


public class MainActivity extends BaseActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    private static final String TAG = MainActivity.class.getName();

    private SearchView mSearchView;

    private MenuItem mSearchItem;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);


        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mSearchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        mSearchView.setQueryHint(getString(R.string.action_search));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query)){
                    performQuery(query);
                    MenuItemCompat.collapseActionView(mSearchItem);

                    return true;
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if(!TextUtils.isEmpty(query)){
                    List<QueryObject> queryObjects = BibleQueryUtil.getSuggestions(MainActivity.this, query);

                    if(queryObjects.isEmpty()){
                        return false;
                    }

                    Cursor cursor = BibleQueryUtil.getQueryCursor(queryObjects);

                    SearchCursorAdapter adapter = new SearchCursorAdapter(MainActivity.this, cursor, 0, queryObjects);
                    mSearchView.setSuggestionsAdapter(adapter);

                    return true;
                }

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /*
        test method
     */
    public void performQuery(String query){
        String[] arr = query.split(" ");

        if(arr.length != 3){
            showToast("Input 3 numbers (1 1 1 = Genesis 1:1)");

            return;
        }

        int book;
        int chapter;
        int verse;

        try{
            book = Integer.parseInt(arr[0]);
            chapter = Integer.parseInt(arr[1]);
            verse = Integer.parseInt(arr[2]);

        }catch (NumberFormatException ex){
            showToast("Input numbers");

            return;
        }

        if(mSearchItem != null){
            MenuItemCompat.collapseActionView(mSearchItem);
        }

        BibleToolsApi api = ((BibleToolsApplication)getApplication())
                .getApi();

        api.deliverReferences(book, chapter, verse, new CallBack<References>() {
            @Override
            public void onSuccess(References response) {

                ReferencesFragment fragment = (ReferencesFragment)
                        getSupportFragmentManager().findFragmentByTag(ReferencesFragment.class.getName());

                if(fragment != null){
                    fragment.displayReferences(response);
                }
            }

            @Override
            public void onError(WaspError waspError) {
                Log.d(TAG, waspError.getErrorMessage());
            }
        });

    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG)
                .show();
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {

        Fragment fragment = (position == 0) ? new ReferencesFragment() : PlaceholderFragment.newInstance(position + 1);

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, fragment.getClass().getName())
                .commit();

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.app_name);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_nav, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }


}
