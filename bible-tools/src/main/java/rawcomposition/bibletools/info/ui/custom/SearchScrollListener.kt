package rawcomposition.bibletools.info.ui.custom

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.support.v4.widget.NestedScrollView
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.arlib.floatingsearchview.FloatingSearchView

class SearchScrollListener constructor(private val searchView: FloatingSearchView) : NestedScrollView.OnScrollChangeListener {

    private var animating = false

    override fun onScrollChange(p0: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {

        if (oldScrollY in 251..(scrollY - 1) && !animating) {

            searchView.animate()
                    .translationY(-searchView.height.toFloat())
                    .setListener(object : AnimatorListenerAdapter() {

                        override fun onAnimationStart(animation: Animator) {
                            animating = true
                        }

                        override fun onAnimationCancel(animation: Animator) {
                            animating = false
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            animating = false
                        }
                    })
                    .interpolator = AccelerateInterpolator(2f)
        }

        if (scrollY < oldScrollY && !animating) {

            searchView.animate().translationY(0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            animating = true
                        }

                        override fun onAnimationCancel(animation: Animator) {
                            animating = false
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            animating = false
                        }
                    })
                    .interpolator = DecelerateInterpolator(2f)


        }
    }

}