package rawcomposition.bibletools.info.ui.home

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
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
import rawcomposition.bibletools.info.ui.settings.SettingsActivity
import rawcomposition.bibletools.info.utils.*
import rawcomposition.bibletools.info.utils.glide.GlideApp
import timber.log.Timber
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

                        it.errorMessage?.let { message ->
                            errorView.renderHtml(message)
                            errorView.show()
                        }

                        it.errorRes?.let { errorRes ->
                            errorView.renderHtml(getString(errorRes))
                            errorView.show()
                        }

                    }
                    ViewState.SUCCESS -> {
                        searchView.hideProgress()
                        errorView.hide()
                        listAdapter.isLoading = false
                    }
                    else -> {
                    }
                }
            }
        })

        viewModel.reference.observe(this, Observer {
            listAdapter.reference = it
        })

        checkDeepLink()

    }

    private fun initUi() {
        val header = navView.getHeaderView(0)
        header.appTitle.setCustomFontTitle()

        searchView.attachNavigationDrawerToMenuButton(drawerLayout)
        searchView.setDimBackground(false)
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

        searchView.setTheme(appPrefs.isNightMode())

        val suggestions = arrayListOf<SearchSuggestion>()
        for (book in resources.getStringArray(R.array.bible_books_full)) {
            suggestions.add(Book(book))
        }

        searchView.setOnQueryChangeListener { _, query ->

            if (query.isNullOrEmpty()) {
                searchView.swapSuggestions(emptyList())
                return@setOnQueryChangeListener
            }

            val list = suggestions.filter { it.body.contains(query, true) }.sortedBy { it.body }

            searchView.swapSuggestions(list)
        }

        searchView.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_voice -> displaySpeechRecognizer()
            }
        }

        navView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()

            when (it.itemId) {
                R.id.nav_fav -> {
                    true
                }
                R.id.action_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                R.id.action_feedback -> {
                    sendFeedback()
                    true
                }
                R.id.action_about -> {
                    showWebUrl("https://bibletools.info/about/info")
                    true
                }
                R.id.action_donate -> {
                    true
                }
                else -> false

            }
        }

        navView.menu.findItem(R.id.nam_home).isChecked = true

        listAdapter = ReferencesListAdapter(GlideApp.with(this), this)

        val manager: RecyclerView.LayoutManager

        if (resources.getBoolean(R.bool.is_tablet)) {
            manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        } else {
            manager = LinearLayoutManager(this)
        }

        recycler.apply {
            layoutManager = manager
            adapter = listAdapter
            addOnScrollListener(SearchViewAttachedListener(searchView))
        }
    }

    private fun checkDeepLink() {
        val intent = intent
        val data = intent.data

        if (data != null) {
            val verse = data.getQueryParameter(VERSE_KEY)
            Timber.d("VERSE: $verse")

            viewModel.initReference(verse)

            searchView.setSearchText(verse)
        } else {
            viewModel.initReference(null)
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

    override fun goToReference(ref: String) {
        viewModel.fetchReference(ref)
    }

    override fun goToLink(link: String) {
        showWebUrl(link)
    }

    private fun displaySpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

        intent.resolveActivity(packageManager)?.let {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (results.isNotEmpty()) {
                val query = results.first()
                searchView.setSearchText(query)

                viewModel.fetchReference(query)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val VERSE_KEY = "verse"
        private const val SPEECH_REQUEST_CODE = 1234
    }
}