package rawcomposition.bibletools.info.ui.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_nav_header.view.*
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.model.ViewState
import rawcomposition.bibletools.info.di.ViewModelFactory
import rawcomposition.bibletools.info.ui.custom.SearchViewAttachedListener
import rawcomposition.bibletools.info.utils.getViewModel
import rawcomposition.bibletools.info.utils.glide.GlideApp
import rawcomposition.bibletools.info.utils.setCustomFontTitle
import rawcomposition.bibletools.info.utils.vertical
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: HomeViewModel

    private lateinit var listAdapter: ReferencesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_home)

        viewModel = getViewModel(this, viewModelFactory)

        initUi()

        viewModel.viewState.observe(this, Observer { it ->
            it?.let {
                when (it.state) {
                    ViewState.LOADING -> {
                        listAdapter.isLoading = true
                    }
                    ViewState.ERROR -> {
                        listAdapter.isLoading = false
                    }
                    ViewState.SUCCESS -> {
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

        listAdapter = ReferencesListAdapter(GlideApp.with(this))

        recycler.apply {
            vertical()
            adapter = listAdapter
            addOnScrollListener(SearchViewAttachedListener(searchView))
        }
    }
}