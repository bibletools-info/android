package rawcomposition.bibletools.info.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

import java.util.Calendar;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.util.enums.FontWeight;

/**
 * Created by tinashe on 2015/04/08.
 */
public class ThemeUtil {

    private static final String TAG = ThemeUtil.class.getName();

    public static void setAppTheme(Activity activity, boolean settings) {

        if (isDarkTheme(activity)) {
            activity.setTheme(settings ? R.style.Theme_Dark_Settings : R.style.Theme_Dark);
            activity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }

    }

    public static boolean isDarkTheme(Context context) {
        String val = PreferenceUtil.getValue(context,
                context.getString(R.string.pref_theme_type), "zero");

        if (val.equals("zero")) {
            return false;
        } else if (val.equals("one")) {
            return true;
        } else {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            return hour > 19;
        }
    }


    public static FontWeight getFontWeight(Context context) {
        String val = PreferenceUtil.getValue(context,
                context.getString(R.string.pref_font_weight),
                "lig");

        if (val.equals("reg")) {
            return FontWeight.REGULAR;
        } else if (val.equals("med")) {
            return FontWeight.MEDIUM;
        } else if (val.equals("lig")) {
            return FontWeight.LIGHT;
        } else {
            return FontWeight.HEAVY;
        }

    }

    public static void tintDrawable(Drawable drawable, int color) {
        if (drawable == null) {
            return;
        }

        drawable = DrawableCompat.wrap(drawable);

        DrawableCompat.setTint(drawable, color);

        DrawableCompat.unwrap(drawable);
    }
}
