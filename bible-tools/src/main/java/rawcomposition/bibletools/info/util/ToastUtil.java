package rawcomposition.bibletools.info.util;

import android.content.Context;
import android.widget.Toast;


/**
 * Created by tinashe on 2015/03/05.
 */
public class ToastUtil {

    public static void show(Context context, String msg) {

        Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }

}
