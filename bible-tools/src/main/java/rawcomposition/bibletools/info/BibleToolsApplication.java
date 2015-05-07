package rawcomposition.bibletools.info;

import android.app.Application;

import com.orhanobut.wasp.OkHttpStack;
import com.orhanobut.wasp.Wasp;

import rawcomposition.bibletools.info.api.BibleToolsApi;

/**
 * Created by tinashe on 2015/02/15.
 */
public class BibleToolsApplication extends Application {

    private BibleToolsApi mApi;

    @Override
    public void onCreate() {
        super.onCreate();

        mApi = new Wasp.Builder(this)
                .setEndpoint(getString(R.string.base_api_url))
                .setWaspHttpStack(new OkHttpStack())
                .build()
                .create(BibleToolsApi.class);
    }

    public BibleToolsApi getApi() {
        return mApi;
    }

}
