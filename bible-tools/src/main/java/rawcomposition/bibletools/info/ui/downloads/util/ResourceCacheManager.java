package rawcomposition.bibletools.info.ui.downloads.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.model.json.bible.Book;
import rawcomposition.bibletools.info.util.GSonUtil;

/**
 * Created by tinashe on 2017/04/12.
 * <p>
 * This class should me able to:
 * 1) Tell us if a bible chapter has been cached eg, 'Genesis 2'
 * 2) Tell us if its busy caching a chapter
 * 3) Cache a resources by chapters eg, 'Genesis 2'
 * 4) Show progress notification while caching
 * 5) Notify subscribers when a chapter has been cached
 */

public class ResourceCacheManager {

    private static final String TAG = ResourceCacheManager.class.getName();

    private static final Object lock = new Object();

    private static ResourceCacheManager instance;

    private List<Book> mBibleBooks;

    private ResourceCacheManager() {
    }

    public static ResourceCacheManager getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new ResourceCacheManager();
            }

            return instance;
        }
    }


    private void initializeBooks(Context context) {
        String jsonString = GSonUtil.readJsonFile(context, "json/bible_books.json");
        if (TextUtils.isEmpty(jsonString)) {
            mBibleBooks = new ArrayList<>();
            return;
        }

        Type type = new TypeToken<List<Book>>() {
        }.getType();
        mBibleBooks = GSonUtil.getInstance().fromJson(jsonString, type);
    }

    @CacheState
    public int getCacheState(Context context, String resource) {
        //Convert Genesis 2 into 1, 2
        if (mBibleBooks == null) {
            initializeBooks(context);
        }

        String book = null;
        for (Book b : mBibleBooks) {
            if (resource.toLowerCase().contains(b.getTitle().toLowerCase())) {
                book = b.getTitle();
                break;
            }
        }

        if (book == null) {
            return CacheState.EMPTY;
        }

        String strChapter = resource.substring(resource.lastIndexOf(" ") + 1);
        int chapter;
        if (!TextUtils.isEmpty(strChapter) && TextUtils.isDigitsOnly(strChapter.trim())) {
            chapter = Integer.parseInt(strChapter.trim());
        } else {
            return CacheState.EMPTY;
        }

        Log.d(TAG, "Book: " + book + ", Chapter: " + chapter);

        return getCacheState(context, book, chapter);
    }

    @CacheState
    public int getCacheState(Context context, String book, int chapter) {


        return CacheState.COMPLETE;
    }
}
