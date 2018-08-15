package rawcomposition.bibletools.info.ui.home

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import kotlinx.android.synthetic.main.reference_skeleton_item.*
import rawcomposition.bibletools.info.data.model.Reference
import rawcomposition.bibletools.info.data.model.Resource
import rawcomposition.bibletools.info.ui.home.vh.ResourceHolder
import rawcomposition.bibletools.info.ui.home.vh.SkeletonHolder
import rawcomposition.bibletools.info.ui.home.vh.VerseHolder
import rawcomposition.bibletools.info.utils.glide.GlideRequests

class ReferencesListAdapter constructor(private val glide: GlideRequests,
                                        private val callback: ReferenceCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var size = 0

    private var resources: List<Resource> = emptyList()

    var isLoading = false
        set(value) {
            field = value

            size = if (value) {
                6
            } else 0

            notifyDataSetChanged()
        }

    var reference: Reference? = null
        set(value) {
            field = value

            val list = arrayListOf<Resource>()
            field?.resources?.let { list.addAll(it) }
            field?.sidebarResources?.let { list.addAll(it) }

            this.resources = list

            size = this.resources.size.plus(1)

            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoading -> TYPE_SKELETON
            position == 0 -> TYPE_VERSE
            else -> TYPE_REFERENCE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SKELETON -> SkeletonHolder.inflate(parent)
            TYPE_VERSE -> VerseHolder.inflate(parent)
            TYPE_REFERENCE -> ResourceHolder.inflate(parent)
            else -> SkeletonHolder.inflate(parent)
        }
    }

    override fun getItemCount(): Int = size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is SkeletonHolder -> holder.bind()
            is VerseHolder -> holder.bind(reference!!, callback)
            is ResourceHolder -> {
                val pos = position - 1

                if (pos < resources.size) {
                    holder.bind(resources[pos], glide, callback, position)
                }
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        if (holder is SkeletonHolder) {
            holder.shimmerLayout.stopShimmer()
        }
        super.onViewDetachedFromWindow(holder)
    }

    companion object {
        private const val TYPE_SKELETON = 1
        private const val TYPE_VERSE = 2
        private const val TYPE_REFERENCE = 3
    }
}