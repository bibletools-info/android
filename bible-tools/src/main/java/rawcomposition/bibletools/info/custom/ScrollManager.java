/*
 * Copyright (C) ${YEAR} Antonio Leiva
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rawcomposition.bibletools.info.custom;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.HashMap;

public class ScrollManager extends RecyclerView.OnScrollListener {

    private static final String TAG = ScrollManager.class.getName();

    private static final int MIN_SCROLL_TO_HIDE = 10;
    private boolean hidden;
    private int accummulatedDy;
    private int totalDy;
    private int initialOffset;
    private HashMap<View, Direction> viewsToHide = new HashMap<>();


    public ScrollManager() {
    }

    public static void setMargins(View v, int t) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(0, t, 0, 0);
            v.requestLayout();
        }
    }

    public void attach(RecyclerView recyclerView) {
        recyclerView.setOnScrollListener(this);
    }

    public void addView(final View view, Direction direction) {
        viewsToHide.put(view, direction);
    }

    public void setInitialOffset(int initialOffset) {
        this.initialOffset = initialOffset;
    }


    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        totalDy += dy;

        if (totalDy < initialOffset) {
            return;
        }

        if (dy > 0) {
            accummulatedDy = accummulatedDy > 0 ? accummulatedDy + dy : dy;
            if (accummulatedDy > MIN_SCROLL_TO_HIDE) {
                hideViews();
            }
        } else if (dy < 0) {
            accummulatedDy = accummulatedDy < 0 ? accummulatedDy + dy : dy;
            if (accummulatedDy < -MIN_SCROLL_TO_HIDE) {
                showViews();
            }
        }
    }

    public void hideViews() {
        if (!hidden) {
            hidden = true;
            for (View view : viewsToHide.keySet()) {
                hideView(view, viewsToHide.get(view));

            }
        }
    }

    private void showViews() {
        if (hidden) {
            hidden = false;
            for (View view : viewsToHide.keySet()) {

                showView(view);
            }
        }
    }

    private void hideView(View view, Direction direction) {
        int height = calculateTranslation(view);
        int translateY = direction == Direction.UP ? -height : height;
        runTranslateAnimation(view, translateY, new AccelerateInterpolator(3));
    }

    /**
     * Takes height + margins
     *
     * @param view View to translate
     * @return translation in pixels
     */
    private int calculateTranslation(View view) {
        int height = view.getHeight();

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int margins = params.topMargin + params.bottomMargin;

        return height + margins;
    }

    private void showView(View view) {
        runTranslateAnimation(view, 0, new DecelerateInterpolator(3));
    }

    private void runTranslateAnimation(final View view, final int translateY, Interpolator interpolator) {
        Animator slideInAnimation = ObjectAnimator.ofFloat(view, "translationY", translateY);
        slideInAnimation.setDuration(view.getContext().getResources().getInteger(android.R.integer.config_mediumAnimTime));
        slideInAnimation.setInterpolator(interpolator);
        slideInAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(TAG, "onAnimationStart");
                if (translateY == 0) {
                    if (view.getVisibility() == View.GONE) {
                        view.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG, "onAnimationEnd");
                if (translateY != 0) {
                    if (view.getVisibility() == View.VISIBLE) {
                        view.setVisibility(View.GONE);
                    }

                } else {
                    if (view.getVisibility() == View.GONE) {
                        view.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d(TAG, "onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.d(TAG, "onAnimationRepeat");
            }
        });
        slideInAnimation.start();
    }

    public static enum Direction {UP, DOWN}
}
