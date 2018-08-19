package rawcomposition.bibletools.info.ui.home.strongs

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_strongs.*
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.model.ViewState
import rawcomposition.bibletools.info.data.model.Word
import rawcomposition.bibletools.info.di.ViewModelFactory
import rawcomposition.bibletools.info.ui.base.RoundedBottomSheetDialogFragment
import rawcomposition.bibletools.info.utils.*
import javax.inject.Inject

class StrongsDialogFragment : RoundedBottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: StrongsViewModel

    private lateinit var listAdapter: StrongsResourcesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_strongs, container, false)
    }

    override fun onViewCreated(contentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(contentView, savedInstanceState)
        AndroidSupportInjection.inject(this)

        val word = arguments?.getSerializable(ARG_WORD) as Word
        source.text = word.text

        listAdapter = StrongsResourcesAdapter()

        resourcesList.apply {
            vertical()
            adapter = listAdapter
            isNestedScrollingEnabled = false
        }

        viewModel = getViewModel(this, viewModelFactory)
        viewModel.viewState.observe(this, Observer { it ->
            it?.let {
                when (it.state) {
                    ViewState.LOADING -> {
                        shimmerLayout.startShimmer()
                        resourcesList.hide()
                        shimmerLayout.show()
                    }
                    ViewState.SUCCESS -> {
                        shimmerLayout.stopShimmer()
                        shimmerLayout.hide()
                        resourcesList.show()
                    }
                    ViewState.ERROR -> {
                        shimmerLayout.stopShimmer()
                        shimmerLayout.hide()
                        resourcesList.hide()

                        it.errorMessage?.let { msg ->
                            description.renderHtml(msg)
                            description.show()
                        }
                    }
                    else -> {
                    }
                }
            }

        })
        viewModel.strongs.observe(this, Observer { it ->
            it?.let { response ->
                originalWord.text = "${response.originalWord} ${response.pronunciation}"
                description.text = response.definition
                originalWord.show()
                description.show()
                listAdapter.resources = response.resources
                listAdapter.resources = response.resources
            }
        })

        viewModel.fetchStrongs(word)

    }

    companion object {
        private const val ARG_WORD = "ARG_WORD"

        fun newInstance(word: Word): StrongsDialogFragment {
            val fragment = StrongsDialogFragment()

            val args = Bundle()
            args.putSerializable(ARG_WORD, word)
            fragment.arguments = args

            return fragment
        }
    }
}