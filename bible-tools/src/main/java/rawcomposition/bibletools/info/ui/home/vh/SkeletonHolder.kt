package rawcomposition.bibletools.info.ui.home.vh

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.reference_skeleton_item.*
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.utils.inflateView

class SkeletonHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind() {
        shimmerLayout.startShimmer()
    }

    companion object {
        fun inflate(parent: ViewGroup):
                SkeletonHolder = SkeletonHolder(inflateView(R.layout.reference_skeleton_item, parent, false))
    }
}