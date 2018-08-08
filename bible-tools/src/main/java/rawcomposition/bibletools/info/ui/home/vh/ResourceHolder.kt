package rawcomposition.bibletools.info.ui.home.vh

import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_reference_item.*
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.model.Resource
import rawcomposition.bibletools.info.utils.glide.GlideRequests
import rawcomposition.bibletools.info.utils.hide
import rawcomposition.bibletools.info.utils.inflateView
import rawcomposition.bibletools.info.utils.show
import timber.log.Timber

class ResourceHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(resource: Resource, glide: GlideRequests, callback: Callback) {

        glide.load(String.format(LOGO_URL, resource.logo))
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .circleCrop()
                .into(photo)

        author.text = resource.author
        name.text = resource.name

        setContentHtml(resource.content ?: "") {

            if (it.startsWith("http")) {

                callback.goToLink(it)

            } else if (it.startsWith("/")) {

                val ref = it.substring(it.indexOf("/") + 1)

                if (ref.isNotEmpty()) {
                    callback.goToReference(ref)
                }
            }
        }

        itemView.setOnClickListener {
            when (content.maxLines) {
                MAX_LINES -> {
                    content.maxLines = Int.MAX_VALUE
                    it.isActivated = true

                    resource.isExpanded = true

                    gradient.hide()
                }
                else -> {
                    content.maxLines = MAX_LINES
                    it.isActivated = false

                    resource.isExpanded = false

                    gradient.show()
                }
            }
        }

        gradient.setOnClickListener { itemView.performClick() }
        content.setOnClickListener { itemView.performClick() }

        if (content.maxLines == Int.MAX_VALUE) {
            itemView.performClick()
        }
    }

    private fun setContentHtml(html: String, callback: (String) -> Unit) {
        val sequence = if (Build.VERSION.SDK_INT >= 24) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
        val strBuilder = SpannableStringBuilder(sequence)
        val urls = strBuilder.getSpans(0, sequence.length, URLSpan::class.java)
        for (span in urls) {
            makeLinkClickable(strBuilder, span, callback)
        }
        content.text = strBuilder
        content.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun makeLinkClickable(strBuilder: SpannableStringBuilder, span: URLSpan, callback: (String) -> Unit) {
        val start = strBuilder.getSpanStart(span)
        val end = strBuilder.getSpanEnd(span)
        val flags = strBuilder.getSpanFlags(span)
        val clickable = object : ClickableSpan() {
            override fun onClick(view: View) {
                Timber.d("URL: ${span.url}")

                callback.invoke(span.url)
            }
        }
        strBuilder.setSpan(clickable, start, end, flags)
        strBuilder.removeSpan(span)
    }

    interface Callback {
        fun goToReference(ref: String)

        fun goToLink(link: String)
    }

    companion object {
        private const val MAX_LINES = 4
        private const val LOGO_URL = "https://bibletools.info/assets/img/authors/%s.png"

        fun inflate(parent: ViewGroup):
                ResourceHolder = ResourceHolder(inflateView(R.layout.layout_reference_item, parent, false))
    }
}