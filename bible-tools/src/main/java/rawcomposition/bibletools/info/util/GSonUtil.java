package rawcomposition.bibletools.info.util;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tinashe on 2015/02/16.
 */
public class GSonUtil {

    private static final String TAG = GSonUtil.class.getName();

    private static Gson mGSon;

    public static Gson getInstance() {
        if (mGSon == null) {
            mGSon = new Gson();
        }

        return mGSon;
    }

    public static String readJsonFile(Context context, String fileName) {
        String jsonString = null;

        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            jsonString = new String(buffer, "UTF8");


        } catch (IOException ex) {
            Log.d(TAG, "" + ex.getMessage());
        }


        return jsonString;
    }
}
