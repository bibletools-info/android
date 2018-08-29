package rawcomposition.bibletools.info.ui.home.picker

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.grid_item.*
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.utils.inflateView

class PickerPagerAdapter constructor(manager: FragmentManager,
                                     private val callback: VersePickerCallback) : FragmentStatePagerAdapter(manager) {

    val registeredFragments = SparseArray<Fragment>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        registeredFragments.put(position, fragment)
        return fragment
    }

    override fun getItem(position: Int): Fragment {
        return PickerFragment.newInstance(position, callback)
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Book".toUpperCase()
            1 -> "Chapter".toUpperCase()
            else -> "Verse".toUpperCase()
        }
    }
}

class BibleListAdapter constructor(private val callback: (Int) -> Unit) : RecyclerView.Adapter<BibleListAdapter.ItemViewHolder>() {

    var items = listOf<String>()
        set(value) {
            field = value
            this.notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ItemViewHolder = ItemViewHolder.inflate(parent)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val text = items[position]
        holder.text.text = text

        val context = holder.itemView.context

        if (!TextUtils.isDigitsOnly(text) && position > 38) {
            holder.text.setTextColor(ContextCompat.getColor(context, R.color.nt_red))
        } else {
            holder.text.setTextColor(ContextCompat.getColor(context, R.color.text))
        }

        holder.itemView.setOnClickListener {
            callback.invoke(position)
        }
    }

    class ItemViewHolder constructor(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        companion object {
            fun inflate(parent: ViewGroup): ItemViewHolder = ItemViewHolder(inflateView(
                    R.layout.grid_item, parent, false))
        }
    }

}

interface VersePickerCallback {

    fun onBookSelected(position: Int)

    fun onChapterSelected(position: Int)

    fun onVerseSelected(verse: Int)

    fun getBooks(): List<String>

    fun getChapters(): List<String>

    fun getVerses(): List<String>
}