package rawcomposition.bibletools.info.util;

import com.google.gson.Gson;

/**
 * Created by tinashe on 2015/02/16.
 */
public class GSonUtil {

    private static Gson mGSon;

    public static Gson getInstance(){
        if(mGSon == null){
            mGSon = new Gson();
        }

        return mGSon;
    }
}
