package rawcomposition.bibletools.info.utils

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView

class ClickSpan constructor(private val listener: OnClickListener) : ClickableSpan() {

    override fun onClick(view: View) {
        listener.onClick()
    }

    fun clickify(view: TextView, range: IntRange, listener: OnClickListener) {
        val text = view.text
        val span = ClickSpan(listener)

        val start = range.start
        val end = range.endInclusive
        if (start == -1) return

        if (text is Spannable) {
            text.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
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

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
    }

    interface OnClickListener {
        fun onClick()
    }
}