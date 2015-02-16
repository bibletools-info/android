package rawcomposition.bibletools.info.util;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.model.QueryObject;

/**
 * Created by tinashe on 2015/02/16.
 */
public class BibleQueryUtil {

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
        return query.replaceAll("\\d","");
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
}
