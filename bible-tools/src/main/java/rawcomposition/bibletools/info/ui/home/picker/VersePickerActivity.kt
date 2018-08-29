package rawcomposition.bibletools.info.ui.home.picker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.view.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_verse_picker.*
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.model.Book
import rawcomposition.bibletools.info.data.model.Chapter
import rawcomposition.bibletools.info.ui.base.BaseThemedActivity

class VersePickerActivity : BaseThemedActivity(), VersePickerCallback {

    private lateinit var pagerAdapter: PickerPagerAdapter

    private var books = arrayListOf<Book>()
    private var book: Book? = null
    private var chapter: Chapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verse_picker)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loadResources()

        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { bar, offset ->
            bar.isActivated = offset < 0
        })

        pagerAdapter = PickerPagerAdapter(supportFragmentManager, this)
        viewPager.adapter = pagerAdapter

        tabLayout.setupWithViewPager(viewPager)

        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val fragment = pagerAdapter.registeredFragments[position]
                if (fragment is PickerFragment) {
                    fragment.updateView()
                }

                toolbar.title = when (position) {
                    0 -> ""
                    1 -> book?.title
                    2 -> "${book?.title} ${chapter?.number}"
                    else -> ""
                }
            }
        })
    }

    private fun loadResources() {
        val jsonString = JsonUtils.getJson(resources, R.raw.bible_books)

        val type = object : TypeToken<ArrayList<Book>>() {

        }.type

        books = Gson().fromJson(jsonString, type)
        if (books.isEmpty()) {
            return
        }
        book = books.first()
        chapter = book?.chapters?.first()

    }

    override fun onBookSelected(position: Int) {
        book = books[position]

        if (book?.chapters?.size == 1) {
            onChapterSelected(0)
        } else {
            viewPager.currentItem = 1
        }
    }

    override fun onChapterSelected(position: Int) {
        chapter = book?.chapters?.get(position)

        viewPager.currentItem = 2
    }

    override fun onVerseSelected(verse: Int) {
        val title = book?.title ?: return
        val chap = chapter?.number ?: return

        val data = Intent()
        data.putExtra(VERSE, "$title $chap:${(verse + 1)}")
        setResult(Activity.RESULT_OK, data)
        supportFinishAfterTransition()
    }

    override fun getBooks(): List<String> {
        val abbr = arrayListOf<String>()
        books.forEach { abbr.add(it.abr ?: "") }

        return abbr
    }

    override fun getChapters(): List<String> {
        val chapters = arrayListOf<String>()

        for (chap in 0 until (book?.chapters?.size ?: 1)) {
            chapters.add("${chap + 1}")
        }

        return chapters
    }

    override fun getVerses(): List<String> {
        val verses = arrayListOf<String>()

        for (verse in 0 until (chapter?.verses ?: 1)) {
            verses.add("${verse + 1}")
        }

        return verses
    }

    companion object {
        const val VERSE = "VERSE"
    }
}