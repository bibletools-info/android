package rawcomposition.bibletools.info.custom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by tinashe on 2015/03/09.
 */
public class CustomRecycler extends RecyclerView {

    private static final String TAG = CustomRecycler.class.getName();

    public CustomRecycler(Context context) {
        super(context);
    }

    public CustomRecycler(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecycler(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void smoothScrollToPosition(int position) {

        try {
            super.smoothScrollToPosition(position);
        } catch (Exception ex) {
            Log.d(TAG, "Exception: smoothScrollToPosition");
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        try {
            super.scrollTo(x, y);
        } catch (Exception ex) {
            Log.d(TAG, "Exception: scrollTo");
        }

    }
}
