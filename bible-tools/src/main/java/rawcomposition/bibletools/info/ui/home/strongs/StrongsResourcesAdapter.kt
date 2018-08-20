package rawcomposition.bibletools.info.ui.home.strongs

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.strongs_resource_item.*
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.model.FontType
import rawcomposition.bibletools.info.data.model.StrongsResource
import rawcomposition.bibletools.info.utils.*

class StrongsResourcesAdapter constructor(@FontType private val fontType: String) : RecyclerView.Adapter<StrongsResourcesAdapter.ResourceHolder>() {

    var resources: List<StrongsResource> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ResourceHolder = ResourceHolder.inflate(parent, fontType)

    override fun getItemCount(): Int = resources.size

    override fun onBindViewHolder(holder: ResourceHolder, position: Int) {
        holder.bind(resources[position])
    }

    class ResourceHolder constructor(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(resource: StrongsResource) {

            title.text = resource.title
            content.renderHtml(resource.content)

            itemView.setOnClickListener {

                when (content.maxLines) {
                    MAX_LINES -> {
                        content.maxLines = Int.MAX_VALUE
                        it.isActivated = true

                        gradient.hide()
                    }
                    else -> {

                        content.maxLines = MAX_LINES
                        it.isActivated = false

                        gradient.show()
                    }
                }
            }

            content.setOnClickListener { itemView.performClick() }

            if (content.maxLines == Int.MAX_VALUE) {
                itemView.performClick()
            }
        }

        companion object {
            private const val MAX_LINES = 4

            fun inflate(parent: ViewGroup, @FontType fontType: String): ResourceHolder {
                val holder = ResourceHolder(inflateView(R.layout.strongs_resource_item, parent, false))
                holder.content.setFont(fontType)
                return holder
            }
        }
    }
}