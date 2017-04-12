package rawcomposition.bibletools.info.ui.downloads.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.ui.downloads.BookFragment;

/**
 * Created by tinashe on 2017/04/01.
 */

public class BooksPagerAdapter extends FragmentStatePagerAdapter {

    private final String[] BOOKS;

    public BooksPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        BOOKS = context.getResources().getStringArray(R.array.bible_books_full);
    }

    @Override
    public Fragment getItem(int position) {
        return new BookFragment();
    }

    @Override
    public int getCount() {
        return BOOKS.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return BOOKS[position];
    }
}
