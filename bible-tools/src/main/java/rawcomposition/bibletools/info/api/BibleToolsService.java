package rawcomposition.bibletools.info.api;

import rawcomposition.bibletools.info.util.GSonUtil;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by tinashe on 2015/06/02.
 */
public class BibleToolsService {

    private static final String END_POINT = "http://bibletools.info";

    private static BibleToolsApi mApi;
    private static BibleToolsService mService;

    private BibleToolsService() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(END_POINT)
                .setConverter(new GsonConverter(GSonUtil.getInstance()))
                .build();

        mApi = restAdapter.create(BibleToolsApi.class);
    }

    public static synchronized BibleToolsApi getApi(){
        if(mService == null){
            mService = new BibleToolsService();
        }

        return mApi;
    }
}
