package rawcomposition.bibletools.info.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by tinashe on 2015/03/07.
 */
public class PreferenceUtil {

    private static SharedPreferences mPref;

    private static synchronized SharedPreferences getPrefInstance(Context context){
        if(mPref == null){
            mPref = PreferenceManager.getDefaultSharedPreferences(context);
        }

        return mPref;
    }

    /**
     * Returns a boolean from Preferences
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getValue(Context context, String key, boolean defValue){
        return getPrefInstance(context)
                .getBoolean(key, defValue);
    }

    /**
     * Returns an int from Preferences
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static int getValue(Context context, String key, int defValue){
        return getPrefInstance(context)
                .getInt(key, defValue);
    }

    /**
     * Returns a string from Preferences
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String getValue(Context context, String key, String defValue){
        return getPrefInstance(context)
                .getString(key, defValue);
    }


    /**
     * Updates a boolean value in Preferences
     * @param context
     * @param key
     * @param value
     */
    public static void updateValue(Context context, String key, boolean value){
        getPrefInstance(context)
                .edit()
                .putBoolean(key, value)
                .apply();
    }

    /**
     * Updates an integer value in Preferences
     * @param context
     * @param key
     * @param value
     */
    public static void updateValue(Context context, String key, int value){
        getPrefInstance(context)
                .edit()
                .putInt(key, value)
                .apply();
    }

    /**
     * Updates a string value in Preferences
     * @param context
     * @param key
     * @param value
     */
    public static void updateValue(Context context, String key, String value){
        getPrefInstance(context)
                .edit()
                .putString(key, value)
                .apply();
    }
}
