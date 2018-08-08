package rawcomposition.bibletools.info.ui.custom

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet

/**
 * Prevent TextView from scrolling
 *
 * https://stackoverflow.com/a/36193267
 */
class ResourceTextView : AppCompatTextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun scrollTo(x: Int, y: Int) {
        //do nothing
    }
}