package rawcomposition.bibletools.info.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
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
import rawcomposition.bibletools.info.ui.callbacks.OnNavigationListener;


/**
 * Created by tinashe on 2014/12/29.
 */
public class TextViewUtil {

    private static final String TAG = TextViewUtil.class.getName();


    @TargetApi(11)
    public static void setTextViewCallBack(final Activity activity, final TextView mTextView) {

        if (Build.VERSION.SDK_INT < 11) {
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

                        if (!TextUtils.isEmpty(text)) {
                            shareOrSearch(activity, null, text, true);
                        }
                        // Finish and close the ActionMode
                        mode.finish();
                        return true;
                    case R.id.action_search:
                        text = computeSelectedText(mTextView);

                        if (!TextUtils.isEmpty(text)) {
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

    private static CharSequence computeSelectedText(TextView mTextView) {

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

    public static void shareOrSearch(Context context, CharSequence subject, CharSequence text, boolean share) {

        if (share) {

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, text.toString());
            if (!TextUtils.isEmpty(subject)) {
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

    public static void copyText(Context context, String text) {
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
            ((Spannable) text).setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            SpannableString s = SpannableString.valueOf(text);
            s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            view.setText(s);
        }

        MovementMethod m = view.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            view.setMovementMethod(LinkMovementMethod.getInstance());

            Log.d(TAG, "Clickified: " + clickableText);
        }
    }


    public static void setCustomFontTitle(Context context, TextView textView) {
        String text = "Bible Tools.info";

        Typeface t1 = Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Heavy.ttf");
        Typeface t2 = Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Light.ttf");

        SpannableString sb = new SpannableString(text);
        sb.setSpan(new CustomTypefaceSpan("", t1), 0, text.indexOf('.'), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        sb.setSpan(new CustomTypefaceSpan("", t2), 11, text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(sb);
    }

    public static void stripVerses(String reference) {
        String start = "class=\"bibleref\">";
        String end = "</a>";
        String regex = "class=\\\"bibleref\\\">(.*?)<\\/a>";


        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(reference);

        List<String> allMatches = new ArrayList<>();

        while (matcher.find()) {
            allMatches.add(matcher.group().replace(start, "").replace(end, ""));

        }

        for (String verse : allMatches) {
            Log.d(TAG, verse);
        }

    }

    public static void setVerseClickListener(final TextView textView, final OnNavigationListener listener) {
        final String reference = "/reference/";
        final String publication = "publication.php";

        Spannable formattedContent = replaceAll((Spanned) textView.getText(), URLSpan.class, new URLSpanConverter(), new CustomClickableSpan.OnClickListener() {
            @Override
            public void onClick(String url) {

                if (url.contains(publication)) {
                    Uri data = Uri.parse(url);

                    String book = data.getQueryParameter("bookSubCode");
                    String chapter = data.getQueryParameter("chapter");
                    String verse = data.getQueryParameter("verse");

                    if (!TextUtils.isEmpty(book) && !TextUtils.isEmpty(chapter)) {

                        if (TextUtils.isEmpty(verse)) {
                            verse = "1";
                        }

                        String ref = book + " " + chapter + ":" + verse;

                        Log.d(TAG, ref);


                        listener.onVerseClick(ref);

                    } else {
                        Log.d(TAG, url);
                    }


                } else {

                    if (url.contains(reference)) {
                        url = url.replace(reference, "");

                        Log.d(TAG, "Contains Ref:\n" + url);

                        String ref = BibleQueryUtil.stripClickQuery(textView.getContext(), url);

                        if (!TextUtils.isEmpty(ref)) {
                            listener.onVerseClick(ref);
                        }

                    } else {
                        ///books/iv-vol1/Ge35.19
                        Log.d(TAG, "No Ref:\n" + url);

                        String[] arr = url.split("/");
                        if (arr.length > 1) {
                            url = arr[arr.length - 1];

                            String ref = BibleQueryUtil.stripClickQuery(textView.getContext(), url);

                            if (!TextUtils.isEmpty(ref)) {
                                listener.onVerseClick(ref);
                            }
                        }

                    }
                }
            }
        });


        textView.setText(formattedContent);
        MovementMethod m = textView.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }

    }


    public static <A extends CharacterStyle, B extends CharacterStyle> Spannable replaceAll(Spanned original,
                                                                                            Class<A> sourceType,
                                                                                            SpanConverter<A, B> converter,
                                                                                            final CustomClickableSpan.OnClickListener listener) {

        SpannableString result = new SpannableString(original);
        A[] spans = result.getSpans(0, result.length(), sourceType);

        for (A span : spans) {
            int start = result.getSpanStart(span);
            int end = result.getSpanEnd(span);
            int flags = result.getSpanFlags(span);

            result.removeSpan(span);
            result.setSpan(converter.convert(span, listener), start, end, flags);
        }

        return (result);
    }

    public static String implementBCbold(String text) {
        if (text.contains("/span>")) {

            String subString = text.substring(0, text.indexOf("/span>") - 2);

            return text.replace(subString, "<b>" + subString + "</b>");


        } else {
            return text;
        }
    }

    public interface SpanConverter<A extends CharacterStyle, B extends CharacterStyle> {
        B convert(A span, CustomClickableSpan.OnClickListener listener);
    }

    public static class CustomClickableSpan extends ClickableSpan {

        private String url;
        private OnClickListener mListener;

        public CustomClickableSpan(String url, OnClickListener mListener) {
            this.url = url;
            this.mListener = mListener;
        }

        @Override
        public void onClick(View widget) {
            if (mListener != null) mListener.onClick(url);
        }

        public interface OnClickListener {
            void onClick(String url);
        }
    }

    static class URLSpanConverter
            implements
            SpanConverter<URLSpan, CustomClickableSpan> {


        @Override
        public CustomClickableSpan convert(URLSpan span, CustomClickableSpan.OnClickListener listener) {
            return new CustomClickableSpan(span.getURL(), listener);
        }
    }

    /*
    <b> <p><span class="head">4. God saw</b>.</span> This expression, repeated six times (<a rel="popup" data-resourcename="iv-vol1" data-content="&lt;div class=\&quot;resourcetext\&quot;&gt;&lt;span class=\&quot;lang-en\&quot;&gt;verses&lt;br /&gt;&lt;/span&gt; &lt;/div&gt;" href="#">vs.</a> <a data-reference="Ge1.10" data-datatype="bible" href="/reference/Ge1.10" class="bibleref">10</a>, <a data-reference="Ge1.12" data-datatype="bible" href="/reference/Ge1.12" class="bibleref">12</a>, <a data-reference="Ge1.18" data-datatype="bible" href="/reference/Ge1.18" class="bibleref">18</a>, <a data-reference="Ge1.21" data-datatype="bible" href="/reference/Ge1.21" class="bibleref">21</a>, <a data-reference="Ge1.25" data-datatype="bible" href="/reference/Ge1.25" class="bibleref">25</a>, <a data-reference="Ge1.31" data-datatype="bible" href="/reference/Ge1.31" class="bibleref">31</a>), conveys in human language an activity of God—the evaluation of each single act of creation as meeting completely the plan and will of its Maker. As we, by beholding and examining the products of our efforts, are prepared to declare that they meet our plans and purpose, so God declares, after every creative act, that His products agree completely with His plan. </p>    <p><span class="head">God divided the light from the darkness.</span> At the outset only darkness existed on this formless earth. A change took place with the entrance of light. Now darkness and light exist side by side, but separate from each other. </p>

     */

}
