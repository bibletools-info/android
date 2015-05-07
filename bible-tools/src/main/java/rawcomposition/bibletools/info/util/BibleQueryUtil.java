package rawcomposition.bibletools.info.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.ui.callbacks.SearchQueryStripListener;

/**
 * Created by tinashe on 2015/02/16.
 */
public class BibleQueryUtil {

    private static final String TAG = BibleQueryUtil.class.getName();


    private static String extractWord(String query) {
        query = query.replaceAll("\\d", "").trim();

        return query.replaceAll("[^a-zA-Z]+", "");
    }


    public static List<String> getAllQueries(Context context) {

        List<String> queries = new ArrayList<>();

        List<String> titles = Arrays.asList(context.getResources().getStringArray(R.array.bible_books_full));
        int[] chapters = context.getResources().getIntArray(R.array.book_num_of_chapters_array);

        int pstn = 0;
        for (String book : titles) {
            int numOfChapters = chapters[pstn];

            for (int i = 1; i <= numOfChapters; i++) {
                queries.add(book + " " + i);
            }

            pstn++;
        }

        return queries;
    }

    public static List<String> getAllQueries(Context context, String[] booksArray) {

        List<String> queries = new ArrayList<>();

        int[] chapters = context.getResources().getIntArray(R.array.book_num_of_chapters_array);

        int pstn = 0;
        for (String book : booksArray) {
            int numOfChapters = chapters[pstn];

            for (int i = 1; i <= numOfChapters; i++) {
                queries.add(book + i);
            }

            pstn++;
        }

        return queries;
    }

    public static void stripQuery(Context context, String query, SearchQueryStripListener listener) {
        List<String> titles = Arrays.asList(context.getResources().getStringArray(R.array.bible_books_full));

        /*
            Genesis 1:2
            2 Samuel 2:3
            1 Samuel 3:4
         */
        String bookTitle;

        String arr[] = query.split(" ");
        String temp = arr[0];
        if (TextUtils.isDigitsOnly(temp)) {
            bookTitle = temp + " " + extractWord(query);
        } else {
            bookTitle = extractWord(query);
        }

        Log.d(TAG, "Extracted: [ " + bookTitle + " ]");

        if (TextUtils.isEmpty(bookTitle)) {
            listener.onError();

            return;
        }

        int bookCode = 0;
        boolean found = false;

        if (bookTitle.equals("Psalms")) {
            bookTitle = "Psalm";
        } else {
            if (bookTitle.equalsIgnoreCase("SongofSolomon")) {
                bookTitle = "Song of Solomon";
            }
        }

        for (String book : titles) {
            bookCode++;

            if (book.toLowerCase().contains(bookTitle.toLowerCase())) {
                found = true;
                break;
            }

        }

        if (!found) {
            listener.onError();

            return;
        }


        if (TextUtils.isEmpty(query.replace(bookTitle, ""))) {
            listener.onSuccess(bookCode, 1, 1);

            return;
        }

        String nums = query.replace(bookTitle, "");
        if (nums.contains(":")) {
            arr = nums.split(":");

            int chapter = getNumber(arr[0]);
            int verse = getNumber(arr[1]);

            listener.onSuccess(bookCode, chapter, verse);
        } else {
            int chapter = getNumber(nums);

            listener.onSuccess(bookCode, chapter, 1);
        }
    }

    public static boolean isNewTestament(Context context, String verse) {
        List<String> books = Arrays.asList(context.getResources().getStringArray(R.array.bible_books_full));

        String bookTitle;

        String arr[] = verse.split(" ");
        String temp = arr[0];
        if (TextUtils.isDigitsOnly(temp)) {
            bookTitle = temp + " " + extractWord(verse);
        } else {
            bookTitle = extractWord(verse);
        }

        Log.d(TAG, "Extracted: [ " + bookTitle + " ]");

        if (bookTitle.equals("Psalms")) {
            bookTitle = "Psalm";
        }
        int bookCode = 0;
        for (String book : books) {
            bookCode++;

            if (book.toLowerCase().contains(bookTitle.toLowerCase())) {
                // found = true;
                break;
            }

        }

        return bookCode >= 40;
    }

    public static String stripClickQuery(Context context, String query) {
        String[] titles = context.getResources().getStringArray(R.array.bible_books_short);

        /*
            Ge35.19
            1Sa16.1
         */
        String bookTitle;

        String parts[] = query.split("\\.");
        if (parts.length > 0) {
            String first = parts[0];

            int bookCode = 0;
            String strChap = null;

            for (String ti : titles) {
                bookCode++;
                if (first.toLowerCase().contains(ti.toLowerCase())) {
                    strChap = first.toLowerCase().replace(ti.toLowerCase(), "");

                    break;
                }
            }

            if (bookCode > 0 && !TextUtils.isEmpty(strChap)) {
                bookTitle = context.getResources().getStringArray(R.array.bible_books_full)[bookCode - 1];

                int chapter = 1;
                int verse = 1;


                try {
                    chapter = Integer.parseInt(strChap);
                } catch (NumberFormatException ex) {
                    //
                }

                if (!TextUtils.isEmpty(parts[1])) {
                    try {
                        String vs = parts[1];
                        String[] vss = vs.split("\\-");
                        verse = Integer.parseInt(vss[0]);
                    } catch (NumberFormatException ex) {
                        Log.d(TAG, "NumberFormatException");
                    }
                }

                return bookTitle + " " + chapter + ":" + verse;

            }

        }

        return null;


    }

    public static int getNumber(String text) {
        try {
            text = text.replaceAll("\\D+", "");

            return Integer.parseInt(text);

        } catch (NumberFormatException ex) {
            return 1;
        }

    }

    public static int[] stripRequest(String text) {

        try {
            String[] arr = text.split(" ");
            String book = arr[0];

            String chap_verse = arr[1];

            arr = chap_verse.split(":");
            String chap = arr[0];

            String verse = arr[1];

            return new int[]{Integer.parseInt(book), Integer.parseInt(chap), Integer.parseInt(verse)};
        } catch (Exception ex) {
            return new int[]{0};
        }

    }

}
