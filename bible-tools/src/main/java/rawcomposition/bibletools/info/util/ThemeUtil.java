package rawcomposition.bibletools.info.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;


import java.util.Calendar;

import rawcomposition.bibletools.info.R;

/**
 * Created by tinashe on 2015/04/08.
 */
public class ThemeUtil {

    private static final String TAG = ThemeUtil.class.getName();

    public static void setAppTheme(Activity activity, boolean settings){

        if(isDarkTheme(activity)){
           // activity.setTheme(settings ? R.style.Theme_Dark_Settings : R.style.Theme_Dark);
            //activity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        } else {
            activity.setTheme(settings ? R.style.Theme_Light_Settings : R.style.Theme_Light);

          //  TypedValue typedValue = new TypedValue();
           // Resources.Theme theme = activity.getTheme();
          //  theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true);
           // int color = typedValue.data;
           // activity.getWindow().setBackgroundDrawable(new ColorDrawable(R.color.screen_background_holo_light));
        }

    }

    public static boolean isDarkTheme(Context context){
        String val = PreferenceUtil.getValue(context,
                context.getString(R.string.pref_theme_type), "zero");

        if(val.equals("zero")){
            return false;
        } else if(val.equals("one")){
            return true;
        } else {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            return hour > 19;
        }
    }
}
