package rawcomposition.bibletools.info.ui.home.vh

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_reference_item.*
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.model.Resource
import rawcomposition.bibletools.info.utils.glide.GlideRequests
import rawcomposition.bibletools.info.utils.inflateView
import rawcomposition.bibletools.info.utils.renderHtml

class ResourceHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(resource: Resource, glide: GlideRequests) {

        glide.load(String.format(LOGO_URL, resource.logo))
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .circleCrop()
                .into(photo)

        author.text = resource.author
        name.text = resource.name
        content.renderHtml(resource.content ?: "")

        itemView.setOnClickListener {
            when (content.maxLines) {
                MAX_LINES -> content.maxLines = Int.MAX_VALUE
                else -> content.maxLines = MAX_LINES
            }
        }
    }

    companion object {
        private const val MAX_LINES = 4
        private const val LOGO_URL = "http://bibletools.info/assets/img/authors/%s.png"

        fun inflate(parent: ViewGroup):
                ResourceHolder = ResourceHolder(inflateView(R.layout.layout_reference_item, parent, false))
    }
}