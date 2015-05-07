package rawcomposition.bibletools.info.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import rawcomposition.bibletools.info.R;


/**
 * Created by tinashe on 2015/01/06.
 */
public class AppTextView extends TextView {

    private static final String FONT_DIRECTORY = "fonts/";

    public AppTextView(Context context) {
        super(context);
        init(context, null);
    }

    public AppTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    public AppTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    private void init(Context ctx, AttributeSet attrs) {

        if (attrs == null) {
            return;
        }

        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.AppTheme_Base);

        String fontStyle = a.getString(R.styleable.AppTheme_Base_fontStyle);

        if (fontStyle != null) {
            Typeface typeface = Typeface.createFromAsset(ctx.getAssets(), FONT_DIRECTORY + fontStyle);

            this.setTypeface(typeface);
        }

        a.recycle();

    }


}
