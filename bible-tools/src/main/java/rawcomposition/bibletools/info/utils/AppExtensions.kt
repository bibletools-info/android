package rawcomposition.bibletools.info.utils

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.arlib.floatingsearchview.FloatingSearchView
import org.jsoup.Jsoup
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.model.FontType
import rawcomposition.bibletools.info.data.model.Word
import rawcomposition.bibletools.info.di.ViewModelFactory
import timber.log.Timber
import java.util.regex.PatternSyntaxException


inline fun <reified T : ViewModel> getViewModel(activity: FragmentActivity, factory: ViewModelFactory): T {
    return ViewModelProviders.of(activity, factory)[T::class.java]
}

inline fun <reified T : ViewModel> getViewModel(activity: Fragment, factory: ViewModelFactory): T {
    return ViewModelProviders.of(activity, factory)[T::class.java]
}

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE

fun RecyclerView.vertical() {
    this.layoutManager = LinearLayoutManager(context)
}

fun RecyclerView.horizontal() {
    this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
}

fun inflateView(@LayoutRes layoutResId: Int, parent: ViewGroup, attachToRoot: Boolean): View =
        LayoutInflater.from(parent.context).inflate(layoutResId, parent, attachToRoot)

fun Drawable.tint(color: Int) {
    DrawableCompat.wrap(this)
    DrawableCompat.setTint(this, color)
    DrawableCompat.unwrap<Drawable>(this)
}

fun TextView.setCustomFontTitle() {
    val text = "BibleTools.info"

    val t1 = ResourcesCompat.getFont(context, R.font.lato_heavy)
    val t2 = ResourcesCompat.getFont(context, R.font.lato_light)

    val sb = SpannableString(text)
    sb.setSpan(CustomTypefaceSpan("", t1!!), 0, text.indexOf('.'), Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
    sb.setSpan(CustomTypefaceSpan("", t2!!), text.indexOf('.'), text.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
    setText(sb)
}

fun TextView.renderHtml(html: String) {

    text = if (Build.VERSION.SDK_INT >= 24) {
        Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(html)
    }
}

fun FloatingSearchView.setTheme(darkMode: Boolean) {
    if (darkMode) {
        setBackgroundColor(Color.parseColor("#424242"))
        setViewTextColor(Color.parseColor("#ffffff"))
        setHintTextColor(Color.parseColor("#70FFFFFF"))
        setActionMenuOverflowColor(Color.parseColor("#ffffff"))
        setClearBtnColor(Color.parseColor("#ffffff"))
        setDividerColor(Color.parseColor("#BEBEBE"))
        setMenuItemIconColor(Color.parseColor("#ffffff"))
    } else {
        setBackgroundColor(Color.parseColor("#ffffff"))
        setViewTextColor(Color.parseColor("#212121"))
        setHintTextColor(Color.parseColor("#727272"))
        setActionMenuOverflowColor(Color.parseColor("#989A9A"))
        setClearBtnColor(Color.parseColor("#989A9A"))
        setDividerColor(Color.parseColor("#e0e0e0"))
        setMenuItemIconColor(Color.parseColor("#989A9A"))
    }
}

fun TextView.setContentHtml(html: String, callback: (String) -> Unit) {
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
    text = strBuilder
    movementMethod = LinkMovementMethod.getInstance()
}

private fun makeLinkClickable(strBuilder: SpannableStringBuilder, span: URLSpan, callback: (String) -> Unit) {
    val start = strBuilder.getSpanStart(span)
    val end = strBuilder.getSpanEnd(span)
    val flags = strBuilder.getSpanFlags(span)

    if (start == -1 || end == -1) {
        return
    }

    val clickable = object : ClickableSpan() {
        override fun onClick(view: View) {

            callback.invoke(span.url)
        }
    }
    strBuilder.setSpan(clickable, start, end, flags)
    strBuilder.removeSpan(span)
}

fun TextView.renderVerse(html: String, callback: (Word) -> Unit) {
    renderHtml(html)

    val doc = Jsoup.parse(html)
    for (element in doc.select("a")) {
        val word = element.text()
        val id = element.attr("id")

        if (word.isEmpty() || id.isEmpty()) {
            continue
        }

        val lister = object : ClickSpan.OnClickListener {
            override fun onClick() {
                callback.invoke(Word(id, word))
            }
        }

        for (range in text.toString().matchesIn(word)) {
            try {
                ClickSpan(lister).clickify(this, range, lister)
            } catch (ex: Exception) {
                Timber.e(ex, ex.message)
            }
        }
    }
}

/**
 * Return ranges where this word is matched in the string
 *
 * @return : IntRanges
 */
fun String.matchesIn(word: String): ArrayList<IntRange> {

    val regex: Regex
    try {
        regex = Regex("(?<![\\w\\d])($word)(?![\\w\\d])")
    } catch (ex: PatternSyntaxException) {
        Timber.e(ex, ex.message)
        return arrayListOf()
    }

    val matcher = regex.toPattern().matcher(this)

    val ranges = arrayListOf<IntRange>()

    while (matcher.find()) {
        ranges.add(IntRange(matcher.start(), matcher.end()))
    }

    return ranges
}

fun TextView.setFont(@FontType fontType: String) {

    val font = when (fontType) {
        FontType.LIGHT -> ResourcesCompat.getFont(context, R.font.lato_light)
        FontType.REGULAR -> ResourcesCompat.getFont(context, R.font.lato_regular)
        FontType.MEDIUM -> ResourcesCompat.getFont(context, R.font.lato_medium)
        FontType.HEAVY -> ResourcesCompat.getFont(context, R.font.lato_heavy)
        else -> return
    }

    typeface = font

}