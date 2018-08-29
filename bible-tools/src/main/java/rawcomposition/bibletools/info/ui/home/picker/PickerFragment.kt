package rawcomposition.bibletools.info.ui.home.picker

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_picker.*
import rawcomposition.bibletools.info.R

class PickerFragment : Fragment() {

    private lateinit var listAdapter: BibleListAdapter

    private var callback: VersePickerCallback? = null

    private var position: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_picker, container, false)
    }


    override fun onViewCreated(contentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(contentView, savedInstanceState)

        position = arguments?.getInt(ARG_POS) ?: 0

        listAdapter = BibleListAdapter {
            when (position) {
                0 -> callback?.onBookSelected(it)
                1 -> callback?.onChapterSelected(it)
                2 -> callback?.onVerseSelected(it)
            }
        }
        gridView.apply {
            layoutManager = GridLayoutManager(context, 5)
            adapter = listAdapter
        }

         updateView()
    }

    fun updateView() {

        val content = when (position) {
            0 -> callback?.getBooks()
            1 -> callback?.getChapters()
            2 -> callback?.getVerses()
            else -> return
        }

        listAdapter.items = content ?: listOf()
    }

    companion object {
        private const val ARG_POS = "arg:pos"

        fun newInstance(position: Int, callback: VersePickerCallback): PickerFragment {
            val fragment = PickerFragment()
            fragment.callback = callback

            fragment.arguments = Bundle().apply {
                putInt(ARG_POS, position)
            }
            return fragment
        }
    }
}