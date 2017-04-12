package rawcomposition.bibletools.info.ui.downloads;

import android.view.View;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.ui.downloads.util.ResourceCacheManager;
import rawcomposition.bibletools.info.ui.fragments.BaseFragment;

/**
 * Created by tinashe on 2017/04/01.
 */

public class BookFragment extends BaseFragment {
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_favourites;
    }

    @Override
    protected void initialize(View rootView) {
        super.initialize(rootView);

        ResourceCacheManager.getInstance()
                .getCacheState(getContext(), "Genesis 25");
    }
}
