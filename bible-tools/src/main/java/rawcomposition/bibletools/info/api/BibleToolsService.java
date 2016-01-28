package rawcomposition.bibletools.info.api;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import rawcomposition.bibletools.info.util.GSonUtil;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by tinashe on 2015/06/02.
 */
public class BibleToolsService {

    private static final String BASE_URL = "http://bibletools.info";

    private static BibleToolsApi mApi;
    private static BibleToolsService mService;

    private BibleToolsService() {

        OkHttpClient client = new OkHttpClient();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GSonUtil.getInstance()))
                .client(client)
                .build();

        mApi = retrofit.create(BibleToolsApi.class);

    }

    public static synchronized BibleToolsApi getApi(){
        if(mService == null){
            mService = new BibleToolsService();
        }

        return mApi;
    }
}
