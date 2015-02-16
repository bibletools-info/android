package rawcomposition.bibletools.info.util;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by tinashe on 2014/12/05.
 */
public class DeviceUtil {

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
