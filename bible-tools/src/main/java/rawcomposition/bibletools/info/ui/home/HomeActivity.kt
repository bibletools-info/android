package rawcomposition.bibletools.info.ui.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import com.arlib.floatingsearchview.FloatingSearchView
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_nav_header.view.*
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.model.Book
import rawcomposition.bibletools.info.data.model.Reference
import rawcomposition.bibletools.info.data.model.ViewState
import rawcomposition.bibletools.info.di.ViewModelFactory
import rawcomposition.bibletools.info.ui.base.BaseThemedActivity
import rawcomposition.bibletools.info.ui.custom.SearchViewAttachedListener
import rawcomposition.bibletools.info.utils.*
import rawcomposition.bibletools.info.utils.glide.GlideApp
import javax.inject.Inject

class HomeActivity : BaseThemedActivity(), ReferenceCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: HomeViewModel

    private lateinit var listAdapter: ReferencesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewModel = getViewModel(this, viewModelFactory)

        initUi()

        viewModel.viewState.observe(this, Observer { it ->
            it?.let {
                when (it.state) {
                    ViewState.LOADING -> {
                        errorView.hide()
                        listAdapter.isLoading = true
                        searchView.showProgress()
                    }
                    ViewState.ERROR -> {
                        searchView.hideProgress()
                        listAdapter.isLoading = false

                        errorView.renderHtml(String.format(getString(R.string.api_default_error), searchView.query))
                        errorView.show()
                    }
                    ViewState.SUCCESS -> {
                        searchView.hideProgress()
                        errorView.hide()
                        listAdapter.isLoading = false
                    }
                }
            }
        })

        viewModel.reference.observe(this, Observer {
            listAdapter.reference = it
        })

    }

    private fun initUi() {
        val header = navView.getHeaderView(0)
        header.appTitle.setCustomFontTitle()

        searchView.attachNavigationDrawerToMenuButton(drawerLayout)
        searchView.setDimBackground(false)
        searchView.setShowMoveUpSuggestion(true)
        searchView.setOnSearchListener(object : FloatingSearchView.OnSearchListener {
            override fun onSearchAction(currentQuery: String?) {

                if (currentQuery != null && currentQuery.isNotEmpty()) {
                    viewModel.fetchReference(currentQuery)
                }
            }

            override fun onSuggestionClicked(searchSuggestion: SearchSuggestion?) {
                searchView.setSearchText(searchSuggestion?.body + " ")
            }

        })

        if (darkTheme) {
            searchView.setDarkTheme()
        }

        val suggestions = arrayListOf<SearchSuggestion>()
        for (book in resources.getStringArray(R.array.bible_books_full)) {
            suggestions.add(Book(book))
        }

        searchView.setOnQueryChangeListener { _, newQuery ->

            if (newQuery.isNullOrEmpty()) {
                searchView.swapSuggestions(emptyList())
                return@setOnQueryChangeListener
            }

            val list = suggestions.filter { it.body.contains(newQuery, true) }.sortedBy { it.body }

            searchView.swapSuggestions(list)
        }

        navView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()

            when (it.itemId) {
                R.id.nav_fav -> {
                    true
                }
                R.id.action_settings -> {
                    true
                }
                R.id.action_feedback -> {
                    true
                }
                R.id.action_help -> {
                    true
                }
                R.id.action_donate -> {
                    true
                }
                else -> false

            }
        }

        listAdapter = ReferencesListAdapter(GlideApp.with(this), this)

        recycler.apply {
            vertical()
            adapter = listAdapter
            addOnScrollListener(SearchViewAttachedListener(searchView))
        }
    }

    override fun toggleFavorite(reference: Reference) {

    }

    override fun goToPrevious(prev: String) {
        viewModel.fetchReference(prev)
    }

    override fun goToNext(next: String) {
        viewModel.fetchReference(next)
    }
}