package rawcomposition.bibletools.info.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.util.KeyBoardUtil;
import rawcomposition.bibletools.info.util.ThemeUtil;

/**
 * sub class of {@link android.widget.AutoCompleteTextView} that includes a clear (dismiss / close) button with
 * a OnClearListener to handle the event of clicking the button
 * based on code from {@link http://www.gubed.net/clearableautocompletetextview}
 *
 * @author Michael Derazon
 */
public class ClearAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    private static final String FONT_DIRECTORY = "fonts/";
    boolean justCleared = false;
    private Drawable imgClearButton;
    private Drawable mVoiceBtn;
    private OnClearListener defaultClearListener = new OnClearListener() {

        @Override
        public void onClear() {
            ClearAutoCompleteTextView et = ClearAutoCompleteTextView.this;
            et.setText("");

        }
    };
    private OnClearListener onClearListener = defaultClearListener;
    private OnTouchListener mVoiceTouchListener;
    private OnTouchListener mClearTouch = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ClearAutoCompleteTextView et = ClearAutoCompleteTextView.this;

            if (et.getCompoundDrawables()[2] == null)
                return false;

            if (event.getAction() != MotionEvent.ACTION_UP)
                return false;

            if (event.getX() > et.getWidth() - et.getPaddingRight() - imgClearButton.getIntrinsicWidth()) {
                onClearListener.onClear();
                justCleared = true;

                KeyBoardUtil.hideKeyboard(getContext(), et);
            }
            return false;
        }
    };

    /* Required methods, not used in this implementation */
    public ClearAutoCompleteTextView(Context context) {
        super(context);
        init(context, null);
    }

    /* Required methods, not used in this implementation */
    public ClearAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /* Required methods, not used in this implementation */
    public ClearAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    void init(Context ctx, AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.AppTheme_Base);

            String fontStyle = a.getString(R.styleable.AppTheme_Base_fontStyle);

            if (fontStyle != null) {
                Typeface typeface = Typeface.createFromAsset(ctx.getAssets(), FONT_DIRECTORY + fontStyle);

                this.setTypeface(typeface);
                a.recycle();
            }
        }

        initDrawables();

    }

    public void initDrawables() {
        imgClearButton = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_close_grey, getContext().getTheme());
        mVoiceBtn = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_mic_grey, getContext().getTheme());

        if (ThemeUtil.isDarkTheme(getContext())) {
            ThemeUtil.tintDrawable(imgClearButton, Color.WHITE);
            ThemeUtil.tintDrawable(mVoiceBtn, Color.WHITE);
        }
    }


    public void setVoiceTouchListener(OnTouchListener voiceTouchListener) {
        this.mVoiceTouchListener = voiceTouchListener;
    }

    public void hideClearButton() {
        if (mVoiceTouchListener == null) {
            this.setCompoundDrawables(null, null, null, null);
        } else {
            showVoiceButton(mVoiceTouchListener);
        }

    }


    public void showClearButton() {
        this.setCompoundDrawablesWithIntrinsicBounds(null, null, imgClearButton, null);

        this.setOnTouchListener(mClearTouch);
    }

    public void showVoiceButton(View.OnTouchListener listener) {
        this.setCompoundDrawablesWithIntrinsicBounds(null, null, mVoiceBtn, null);

        this.setOnTouchListener(listener);
    }

    public Drawable getVoiceBtn() {
        return mVoiceBtn;
    }

    public interface OnClearListener {
        void onClear();
    }

}