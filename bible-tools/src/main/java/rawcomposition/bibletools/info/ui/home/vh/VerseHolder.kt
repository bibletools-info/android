package rawcomposition.bibletools.info.ui.home.vh

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_reference_verse_item.*
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.model.Reference
import rawcomposition.bibletools.info.utils.inflateView
import rawcomposition.bibletools.info.utils.renderHtml

class VerseHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(reference: Reference) {

        shortRef.text = reference.textRef ?: ""
        textRef.renderHtml(reference.verse ?: "")
    }

    companion object {
        fun inflate(parent: ViewGroup): VerseHolder {
            val holder = VerseHolder(inflateView(R.layout.layout_reference_verse_item, parent, false))
            holder.textRef.maxLines = Int.MAX_VALUE

            return holder
        }
    }
}