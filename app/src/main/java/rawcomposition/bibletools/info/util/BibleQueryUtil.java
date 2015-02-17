package rawcomposition.bibletools.info.util;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.model.QueryObject;
import rawcomposition.bibletools.info.ui.callbacks.SearchQueryStripListener;

/**
 * Created by tinashe on 2015/02/16.
 */
public class BibleQueryUtil {

    private static final String TAG = BibleQueryUtil.class.getName();

    public static List<QueryObject> getSuggestions(Context context, String query){
        List<QueryObject> queryObjects = new ArrayList<>();

        String bookText = extractWord(query);

        if(TextUtils.isEmpty(bookText)){
            return queryObjects;
        }

        String[] booksArray = context.getResources().getStringArray(R.array.bible_books_full);
        for(String book: booksArray){

            if(book.toLowerCase().contains(bookText.toLowerCase())){

                int position = Arrays.asList(booksArray).indexOf(book);

                return getQueries(context, book, position);

            }
        }

        return queryObjects;
    }

    private static String extractWord(String query){
        query = query.replaceAll("\\d","").trim();

        return query.replaceAll("[^a-zA-Z]+","");
    }

    private static List<QueryObject> getQueries(Context context, String book, int position){
        List<QueryObject> queryObjects = new ArrayList<>();

        int chapters = context.getResources()
                .getIntArray(R.array.book_num_of_chapters_array)[position];

        for(int i = 1; i <= chapters; i++){
            QueryObject object = new QueryObject();

            object.setDisplayText(book + " " + i);
            object.setBookNum(Arrays.asList(context.getResources().getStringArray(R.array.bible_books_full))
                  .indexOf(book) + 1);
            object.setChapter(i);

            queryObjects.add(object);
        }


        return queryObjects;
    }


    public static Cursor getQueryCursor(List<QueryObject> objects){

        String[] columnNames = {"_id","text", "bookCode", "chapter"};
        MatrixCursor cursor = new MatrixCursor(columnNames);

        String[] temp = new String[4];
        int id = 0;
        for(QueryObject object: objects){
            temp[0] = String.valueOf(id);
            temp[1] = object.getDisplayText();
            temp[2] = String.valueOf(object.getBookNum());
            temp[3] = String.valueOf(object.getChapter());

            cursor.addRow(temp);
        }


        return cursor;

    }


    public static List<String> getAllQueries(Context context){

        List<String> queries = new ArrayList<>();

        List<String> titles = Arrays.asList(context.getResources().getStringArray(R.array.bible_books_full));
        int[] chapters = context.getResources().getIntArray(R.array.book_num_of_chapters_array);

        int pstn = 0;
        for(String book: titles){
            int numOfChapters = chapters[pstn];

            for(int i = 1; i <= numOfChapters; i++){
                queries.add(book + " " + i);
            }

            pstn++;
        }

        return queries;
    }

    public static void stripQuery(Context context, String query, SearchQueryStripListener listener){
        List<String> titles = Arrays.asList(context.getResources().getStringArray(R.array.bible_books_full));
       // int[] chapters = context.getResources().getIntArray(R.array.book_num_of_chapters_array);

        String bookTitle = extractWord(query);

        Log.d(TAG, "Extracted: [ " + bookTitle + " ]");

        if(TextUtils.isEmpty(bookTitle)){
            listener.onError();

            return;
        }

        int bookCode = 0;
        boolean found = false;
        for(String book: titles){
            bookCode++;

            if(book.toLowerCase().contains(bookTitle.toLowerCase())){
                found = true;
               break;
            }

        }

        if(!found){
            listener.onError();

            return;
        }


        if(TextUtils.isEmpty(query.replace(bookTitle, ""))){
            listener.onSuccess(bookCode, 1, 1);

            return;
        }

        String nums = query.replace(bookTitle, "");
        if(nums.contains(":")){
            String[] arr = nums.split(":");

            int chapter = getNumber(arr[0]);
            int verse = getNumber(arr[1]);

            listener.onSuccess(bookCode, chapter, verse);
        } else {
            int chapter = getNumber(nums);

            listener.onSuccess(bookCode, chapter, 1);
        }
    }

    public static int getNumber(String text){
        try{
            text = text.replaceAll("\\D+","");

            return Integer.parseInt(text);

        }catch (NumberFormatException ex){
            return 1;
        }

    }

}
