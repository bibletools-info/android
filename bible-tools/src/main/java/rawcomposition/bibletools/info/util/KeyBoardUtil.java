package rawcomposition.bibletools.info.util;

import android.app.Service;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by tinashe on 2015/02/17.
 */
public class KeyBoardUtil {

    public static void hideKeyboard(Context context, View view){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyBoard(Context context, View view){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }
}
