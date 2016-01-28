package rawcomposition.bibletools.info.util;

import android.os.Build;

/**
 * Created by tinashe on 2015/11/23.
 */
public class VersionUtils {

    public static boolean isAtLeastL() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isAtleastM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
