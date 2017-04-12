package rawcomposition.bibletools.info.ui.downloads;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import butterknife.BindView;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.ui.base.BaseActivity;
import rawcomposition.bibletools.info.ui.downloads.adapter.BooksPagerAdapter;

/**
 * Created by tinashe on 2017/04/01.
 */

public class DownloadsActivity extends BaseActivity implements DownloadsContract.View {

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.pager)
    ViewPager viewPager;

    private BooksPagerAdapter pagerAdapter;

    private DownloadsPresenter presenter;

    @Override
    public void hookUpPresenter() {
        presenter = new DownloadsPresenter(this);
        presenter.startPresenting();
    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_downloads;
    }

    @Override
    protected boolean showHomeAsUp() {
        return true;
    }

    @Override
    public void setUpTabs() {
        pagerAdapter = new BooksPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
