package rawcomposition.bibletools.info.ui.custom

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.support.v7.widget.RecyclerView
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.arlib.floatingsearchview.FloatingSearchView

class SearchViewAttachedListener(private val searchView: FloatingSearchView) : RecyclerView.OnScrollListener() {

    private var animating = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

        if (dy > 0 && !animating) { //hide
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

        } else if (dy < 0 && !animating) { //show
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