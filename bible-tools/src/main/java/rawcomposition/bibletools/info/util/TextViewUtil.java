package rawcomposition.bibletools.info.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.ui.callbacks.ClickSpan;


/**
 * Created by tinashe on 2014/12/29.
 */
public class TextViewUtil {

    private static final String TAG = TextViewUtil.class.getName();



    @TargetApi(11)
    public static void setTextViewCallBack(final Activity activity, final TextView mTextView){

        if(Build.VERSION.SDK_INT < 11){
            return;
        }

        mTextView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Remove the "select all" option
                menu.removeItem(android.R.id.selectAll);
                // Remove the "cut" option
                menu.removeItem(android.R.id.cut);
                // Remove the "copy all" option
                //   menu.removeItem(android.R.id.copy);
                return true;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Called when action mode is first created. The menu supplied
                // will be used to generate action buttons for the action mode

                // Here is an example MenuItem
                // menu.add(0, DEFINITION, 0, "Definition").setIcon(R.drawable.ic_action_book);
                MenuInflater inflater = activity.getMenuInflater();
                inflater.inflate(R.menu.menu_context, menu);
                //menu.
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Called when an action mode is about to be exited and
                // destroyed
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_share:
                        CharSequence text = computeSelectedText(mTextView);

                        if(!TextUtils.isEmpty(text)){
                            shareOrSearch(activity, null, text, true);
                        }
                        // Finish and close the ActionMode
                        mode.finish();
                        return true;
                    case R.id.action_search:
                        text = computeSelectedText(mTextView);

                        if(!TextUtils.isEmpty(text)){
                            shareOrSearch(activity, null, text, false);
                        }
                        // Finish and close the ActionMode
                        mode.finish();

                        return true;
                    default:
                        break;
                }
                return false;
            }

        });

    }

    private static CharSequence computeSelectedText(TextView mTextView){

        int min = 0;
        int max = mTextView.getText().length();
        if (mTextView.isFocused()) {
            final int selStart = mTextView.getSelectionStart();
            final int selEnd = mTextView.getSelectionEnd();

            min = Math.max(0, Math.min(selStart, selEnd));
            max = Math.max(0, Math.max(selStart, selEnd));
        }

        return mTextView.getText().subSequence(min, max);


    }

    public static void shareOrSearch(Context context, CharSequence subject, CharSequence text, boolean share){

        if(share){

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, text.toString());
            if(!TextUtils.isEmpty(subject)){
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            }
            context.startActivity(sharingIntent);

        } else {

            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, text.toString());
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }

        }
    }

    public static void copyText(Context context, String text){
        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);

        clipboard.setText(text);
        Toast.makeText(context, "Copied to Clipboard", Toast.LENGTH_SHORT)
                .show();
    }




    public static void clickify(TextView view, String clickableText, ClickSpan.OnClickListener listener) {

        CharSequence text = view.getText();
        String string = text.toString();
        ClickSpan span = new ClickSpan(listener);

        int start = string.indexOf(clickableText);
        int end = start + clickableText.length();
        if (start == -1) return;

        if (text instanceof Spannable) {
            ((Spannable)text).setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            SpannableString s = SpannableString.valueOf(text);
            s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            view.setText(s);
        }

        MovementMethod m = view.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            view.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }


    public static void setCustomFontTitle(Context context, TextView textView){
        String text = "Bible Tools.info";

        Typeface t1 = Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Heavy.ttf");
        Typeface t2 = Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Light.ttf");

        SpannableString sb = new SpannableString(text);
        sb.setSpan(new CustomTypefaceSpan("", t1), 0, text.indexOf('.'), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        sb.setSpan(new CustomTypefaceSpan("", t2), 11, text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(sb);
    }

    public static void stripVerses(String reference){
        String start = "class=\"bibleref\">";
        String end = "</a>";
        String regex = "class=\\\"bibleref\\\">(.*?)<\\/a>";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(reference);

        List<String> allMatches = new ArrayList<>();

        while (matcher.find()){
            allMatches.add(matcher.group().replace(start, "").replace(end, ""));

        }

        for(String verse: allMatches){
            Log.d(TAG, verse);
        }

    }

    public static void setClickListener(final TextView textView){
      /*  myTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(myTextView.getSelectionStart()== -1 &&
                        myTextView.getSelectionEnd() == -1){
                   Log.d(TAG,  "You clicked outside the link");

                }
                else {

                    int start = myTextView.getSelectionStart();
                    int end = myTextView.getSelectionEnd();
                    String selected = myTextView.getText().toString().substring(start, end);

                    Log.d(TAG, selected);

                }
            }
        });*/


        textView.setMovementMethod(LinkMovementMethod.getInstance());

        CharSequence charSequence = textView.getText();
        SpannableStringBuilder sp = new SpannableStringBuilder(charSequence);

        URLSpan[] spans = sp.getSpans(0, charSequence.length(), URLSpan.class);

        for (URLSpan urlSpan : spans) {
            MySpan mySpan = new MySpan(urlSpan.getURL());
            sp.setSpan(mySpan, sp.getSpanStart(urlSpan),
                    sp.getSpanEnd(urlSpan), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }

        textView.setText(sp);

        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 2.if clicking a link
              //  if (!isClickingLink) {
                    Log.w("log", "not clicking link");
              //  }
              //  isClickingLink = false;
            }
        });
    }

    private static class MySpan extends ClickableSpan {

        private String mUrl;

        public MySpan(String url) {

            super();
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {

            Log.w("log", "clicking link");

           // isClickingLink = true;
            // 1. do url click
        }

    }
}
