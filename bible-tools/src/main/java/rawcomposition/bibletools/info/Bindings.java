package rawcomposition.bibletools.info;

import android.databinding.BindingAdapter;
import android.widget.TextView;

import rawcomposition.bibletools.info.util.FontCache;

/**
 * Created by tinashe on 2015/10/23.
 */
public class Bindings {

    @BindingAdapter({"bind:font"})
    public static void setFont(TextView textView, String fontName) {
        textView.setTypeface(FontCache.getInstance().get(fontName));
    }
}
