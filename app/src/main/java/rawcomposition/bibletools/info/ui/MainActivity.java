package rawcomposition.bibletools.info.ui;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.WaspError;

import rawcomposition.bibletools.info.BibleToolsApplication;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.api.BibleToolsApi;
import rawcomposition.bibletools.info.model.References;
import rawcomposition.bibletools.info.ui.fragments.ReferencesFragment;


public class MainActivity extends ActionBarActivity implements SearchView.OnQueryTextListener{

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ReferencesFragment(), ReferencesFragment.class.getName())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(this);

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

    @Override
    public boolean onQueryTextSubmit(String query) {
        performQuery(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }


    /*
        test method
     */
    private void performQuery(String query){
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


}
