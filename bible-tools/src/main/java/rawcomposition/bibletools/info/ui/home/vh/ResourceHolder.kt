package rawcomposition.bibletools.info.ui.home.vh

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_reference_item.*
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.model.FontType
import rawcomposition.bibletools.info.data.model.Helpful
import rawcomposition.bibletools.info.data.model.Reference.Companion.MAP
import rawcomposition.bibletools.info.data.model.Resource
import rawcomposition.bibletools.info.utils.*
import rawcomposition.bibletools.info.utils.glide.GlideRequests


class ResourceHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(resource: Resource, glide: GlideRequests, callback: Callback, position: Int) {

        if (resource.type == null) {
            glide.load(String.format(LOGO_URL, resource.logo))
                    .placeholder(R.drawable.ic_account_circle)
                    .error(R.drawable.ic_account_circle)
                    .circleCrop()
                    .into(photo)

            author.text = resource.author
            name.text = resource.source

            photo.show()
            author.show()
            name.show()
        } else {
            photo.hide()
            name.hide()

            author.text = resource.source
        }


        when (resource.type) {
            MAP -> {
                contentContainer.hide()
                map.show()

                glide.load(resource.mapUrl)
                        .into(map)
            }
            else -> {
                contentContainer.show()
                map.hide()

                content.setContentHtml(resource.content ?: "") {

                    if (it.startsWith("http")) {

                        callback.goToLink(it)

                    } else if (it.startsWith("/")) {

                        val ref = it.substring(it.indexOf("/") + 1)

                        if (ref.isNotEmpty()) {
                            callback.goToReference(ref)
                        }
                    }
                }
            }
        }

        itemView.setOnClickListener {

            if (resource.type == MAP) {
                callback.viewMap(map, resource.mapUrl!!)
                return@setOnClickListener
            }

            when (content.maxLines) {
                MAX_LINES -> {
                    content.maxLines = Int.MAX_VALUE
                    it.isActivated = true

                    resource.isExpanded = true

                    gradient.hide()

                    if (resource.rating == null && resource.type == null) {
                        ratingContainer.show()
                    }
                }
                else -> {
                    if (ratingContainer.isVisible()) {
                        ratingContainer.hide()
                    }

                    content.maxLines = MAX_LINES
                    it.isActivated = false

                    resource.isExpanded = false

                    gradient.show()

                    callback.itemCollapsed(position, itemView)
                }
            }
        }

        gradient.setOnClickListener { itemView.performClick() }
        content.setOnClickListener { itemView.performClick() }

        btnRatingPositive.setOnClickListener {
            txtRating.setText(R.string.msg_helpful_submitted)

            btnRatingPositive.hide()
            btnRatingNegative.hide()

            resource.rating = Helpful.POSITIVE
            callback.submitHelpful(resource)
        }

        btnRatingNegative.setOnClickListener {
            txtRating.setText(R.string.msg_unhelpful_submitted)

            btnRatingPositive.hide()
            btnRatingNegative.hide()

            resource.rating = Helpful.NEGATIVE
            callback.submitUnHelpful(resource)
        }

        if (content.maxLines == Int.MAX_VALUE) {
            itemView.performClick()
        }
    }

    interface Callback {
        fun goToReference(ref: String)

        fun goToLink(link: String)

        fun viewMap(animView: View, url: String)

        fun submitHelpful(resource: Resource)

        fun submitUnHelpful(resource: Resource)

        fun itemCollapsed(position: Int, childView: View)
    }

    companion object {
        private const val MAX_LINES = 4
        private const val LOGO_URL = "https://bibletools.info/assets/img/authors/%s.png"

        fun inflate(parent: ViewGroup, @FontType fontType: String): ResourceHolder {
            val holder = ResourceHolder(inflateView(R.layout.layout_reference_item, parent, false))
            holder.content.setFont(fontType)
            return holder
        }
    }
}