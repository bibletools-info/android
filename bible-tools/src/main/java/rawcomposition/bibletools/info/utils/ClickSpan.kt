package rawcomposition.bibletools.info.utils

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView

class ClickSpan constructor(private val listener: OnClickListener) : ClickableSpan() {

    override fun onClick(view: View) {
        listener.onClick()
    }

    fun clickify(view: TextView, clickableText: String, listener: OnClickListener) {
        val text = view.text
        val string = text.toString()
        val span = ClickSpan(listener)

        val start = string.indexOf(clickableText)
        val end = start + clickableText.length
        if (start == -1) return

        val bold = StyleSpan(Typeface.BOLD)

        if (text is Spannable) {
            text.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            text.setSpan(bold, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else {
            val s = SpannableString.valueOf(text)
            s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            view.text = s
        }

        val m = view.movementMethod
        if (m == null || m !is LinkMovementMethod) {
            view.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    interface OnClickListener {
        fun onClick()
    }
}