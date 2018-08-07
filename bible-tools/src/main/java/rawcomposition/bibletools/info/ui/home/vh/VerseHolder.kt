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

    fun bind(reference: Reference, callback: Callback) {

        shortRef.text = reference.textRef ?: ""
        textRef.renderHtml(reference.verse ?: "")

        actionFavourite.setImageResource(if (reference.favorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)

        actionFavourite.setOnClickListener {
            callback.toggleFavorite(reference)

            reference.favorite = !reference.favorite

            actionFavourite.setImageResource(if (reference.favorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
        }

        acttionPrevious.setOnClickListener { _ ->
            reference.navigation?.prev?.let {
                callback.goToPrevious(it)
            }
        }

        actionNext.setOnClickListener { _ ->
            reference.navigation?.next?.let {
                callback.goToNext(it)
            }
        }
    }

    interface Callback {
        fun toggleFavorite(reference: Reference)

        fun goToPrevious(prev: String)

        fun goToNext(next: String)
    }

    companion object {
        fun inflate(parent: ViewGroup): VerseHolder {
            val holder = VerseHolder(inflateView(R.layout.layout_reference_verse_item, parent, false))
            holder.textRef.maxLines = Int.MAX_VALUE

            return holder
        }
    }
}