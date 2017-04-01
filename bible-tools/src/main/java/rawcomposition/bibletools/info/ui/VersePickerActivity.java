package rawcomposition.bibletools.info.ui;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.model.json.ReferencesRequest;
import rawcomposition.bibletools.info.model.json.bible.Book;
import rawcomposition.bibletools.info.model.json.bible.Chapter;
import rawcomposition.bibletools.info.ui.adapters.PickerFragmentAdapter;
import rawcomposition.bibletools.info.ui.base.BaseActivity;
import rawcomposition.bibletools.info.ui.callbacks.VersePickerController;
import rawcomposition.bibletools.info.ui.fragments.PickerContentFragment;
import rawcomposition.bibletools.info.util.GSonUtil;
import rawcomposition.bibletools.info.util.enums.BundledExtras;

/**
 * Created by tinashe on 2016/01/28.
 */
public class VersePickerActivity extends BaseActivity implements VersePickerController {

    private static final String TAG = VersePickerActivity.class.getName();

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.pager)
    ViewPager pager;

    private List<Book> mBibleBooks;

    private Book mBook;
    private Chapter mChapter;

    private PickerFragmentAdapter mPagerAdapter;


    @Override
    protected int layoutRes() {
        return R.layout.activity_verse_picker;
    }

    @Override
    protected boolean showHomeAsUp() {
        return true;
    }

    @Override
    public void hookUpPresenter() {
        initializeBooks();

        mPagerAdapter = new PickerFragmentAdapter(getSupportFragmentManager());
        pager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(pager);

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                ((PickerContentFragment) mPagerAdapter.getRegisteredFragment(position))
                        .updateView();
            }
        });
    }

    private void initializeBooks() {
        String jsonString = GSonUtil.readJsonFile(this, "json/bible_books.json");
        if (TextUtils.isEmpty(jsonString)) {
            finish();
            return;
        }

        Type type = new TypeToken<List<Book>>() {
        }.getType();
        mBibleBooks = GSonUtil.getInstance().fromJson(jsonString, type);
        mBook = mBibleBooks.get(0);
        mChapter = mBook.getChapters().get(0);
    }


    @Override
    public void onBookSelected(int book) {
        mBook = mBibleBooks.get(book);

        pager.setCurrentItem(1);

        setTitle(mBook.getTitle());
    }

    @Override
    public void onChapterSelected(int chapter) {
        mChapter = mBook.getChapters().get(chapter);

        pager.setCurrentItem(2);

        setTitle(mBook.getTitle() + " " + mChapter.getNumber());
    }

    @Override
    public void onVerseSelected(int verse) {

        int book = mBibleBooks.indexOf(mBook) + 1;
        int chapter = mBook.getChapters().indexOf(mChapter) + 1;

        Log.d(TAG, "Selected: " + book + ":" + chapter + ":" + (verse + 1));

        navigateBack(new ReferencesRequest(book, chapter, (verse + 1)));
    }

    @Override
    public List<String> getBooks() {
        List<String> arr = new ArrayList<>();
        for (Book book : mBibleBooks) {
            arr.add(book.getAbr());
        }
        return arr;
    }

    @Override
    public List<String> getChapters() {
        List<String> arr = new ArrayList<>();

        for (int i = 0; i < mBook.getChapters().size(); i++) {
            arr.add(String.valueOf((i + 1)));
        }

        return arr;
    }

    @Override
    public List<String> getVerses() {
        List<String> arr = new ArrayList<>();
        for (int i = 0; i < mChapter.getVerses(); i++) {
            arr.add(String.valueOf((i + 1)));
        }
        return arr;
    }

    private void navigateBack(ReferencesRequest request) {
        Intent intent = new Intent();
        intent.putExtra(BundledExtras.DATA_OBJECT.name(), request);
        setResult(RESULT_OK, intent);
        finish();
    }
}
