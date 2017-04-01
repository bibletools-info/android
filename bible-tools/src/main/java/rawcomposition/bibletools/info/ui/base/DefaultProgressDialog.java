package rawcomposition.bibletools.info.ui.base;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by tinashe on 2016/10/26.
 */

public class DefaultProgressDialog extends ProgressDialog {
    public DefaultProgressDialog(Context context) {
        super(context);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setProgressStyle(STYLE_SPINNER);
    }
}
