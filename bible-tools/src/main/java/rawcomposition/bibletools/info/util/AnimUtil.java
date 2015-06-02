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

    private static final int HEADER_HIDE_ANIM_DURATION = 300;


    /**
     * Adds a slide in animation to the view on enter
     *
     * @param context
     * @param view
     */
    public static void slideInEnterAnimation(Context context, View view) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        int screenHeight = display.heightPixels;

        ViewHelper.setTranslationY(view, screenHeight);
        ViewPropertyAnimator
                .animate(view)
                .translationY(0)
                .setDuration(700)
                .setInterpolator(new DecelerateInterpolator(3f))
                .start();
    }

    public static void SlideUp(View view){
        ViewPropertyAnimator.animate(view)
                .translationY(0)
                .alpha(1)
                .setDuration(HEADER_HIDE_ANIM_DURATION)
                .setInterpolator(new DecelerateInterpolator());
    }

    public static void slideDown(View view){
        ViewPropertyAnimator.animate(view)
                .translationY(-view.getBottom())
                .alpha(0)
                .setDuration(HEADER_HIDE_ANIM_DURATION)
                .setInterpolator(new DecelerateInterpolator());
    }
}
