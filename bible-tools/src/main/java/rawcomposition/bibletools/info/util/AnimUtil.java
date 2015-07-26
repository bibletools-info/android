package rawcomposition.bibletools.info.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

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

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        view.startAnimation(animation);
    }

    public static void SlideUp(View view) {
        ViewPropertyAnimator.animate(view)
                .translationY(0)
                .alpha(1)
                .setDuration(HEADER_HIDE_ANIM_DURATION)
                .setInterpolator(new DecelerateInterpolator());
    }

    public static void slideDown(View view) {
        ViewPropertyAnimator.animate(view)
                .translationY(-view.getBottom())
                .alpha(0)
                .setDuration(HEADER_HIDE_ANIM_DURATION)
                .setInterpolator(new DecelerateInterpolator());
    }
}
