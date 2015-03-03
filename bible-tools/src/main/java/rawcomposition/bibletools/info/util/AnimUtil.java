package rawcomposition.bibletools.info.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Created by tinashe on 2015/02/15.
 */
public class AnimUtil {

    /**
     * Adds a slide in animation to the view on enter
     *
     * @param context
     * @param view
     */
    public static void slideInEnterAnimation(Context context, View view){
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        int screenHeight =  display.heightPixels;

        ViewHelper.setTranslationY(view, screenHeight);
        ViewPropertyAnimator
                .animate(view)
                .translationY(0)
                .setDuration(700)
                .setInterpolator(new DecelerateInterpolator(3f))
                .start();
    }
}
