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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.arlib.floatingsearchview.FloatingSearchView
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.di.ViewModelFactory


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
        setLeftActionIconColor(Color.parseColor("#ffffff"))
        setClearBtnColor(Color.parseColor("#ffffff"))
        setDividerColor(Color.parseColor("#BEBEBE"))
        setMenuItemIconColor(Color.parseColor("#ffffff"))
    } else {
        setBackgroundColor(Color.parseColor("#ffffff"))
        setViewTextColor(Color.parseColor("#212121"))
        setHintTextColor(Color.parseColor("#727272"))
        setLeftActionIconColor(Color.parseColor("#989A9A"))
        setClearBtnColor(Color.parseColor("#989A9A"))
        setDividerColor(Color.parseColor("#e0e0e0"))
        setMenuItemIconColor(Color.parseColor("#989A9A"))
    }
}