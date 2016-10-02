package rawcomposition.bibletools.info.util;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
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
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rawcomposition.bibletools.info.custom.LinkMovementMethodOverride;
import rawcomposition.bibletools.info.ui.callbacks.OnNavigationListener;


/**
 * Created by tinashe on 2014/12/29.
 */
public class TextViewUtil {

    private static final String TAG = TextViewUtil.class.getName();


    public static void shareOrSearch(Context context, CharSequence subject, CharSequence text, boolean share) {

        if (share) {

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, text.toString());
            if (!TextUtils.isEmpty(subject)) {
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            }
            context.startActivity(Intent.createChooser(sharingIntent, "Send with:"));

        } else {

            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, text.toString());
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }

        }
    }


    public static void setCustomFontTitle(Context context, TextView textView) {
        String text = "BibleTools.info";

        Typeface t1 = Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Heavy.ttf");
        Typeface t2 = Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Light.ttf");

        SpannableString sb = new SpannableString(text);
        sb.setSpan(new CustomTypefaceSpan("", t1), 0, text.indexOf('.'), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        sb.setSpan(new CustomTypefaceSpan("", t2), text.indexOf('.'), text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
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
            textView.setOnTouchListener(new LinkMovementMethodOverride());
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
