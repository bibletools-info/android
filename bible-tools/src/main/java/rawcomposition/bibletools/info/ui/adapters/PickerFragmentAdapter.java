package rawcomposition.bibletools.info.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import rawcomposition.bibletools.info.ui.callbacks.VersePickerController;
import rawcomposition.bibletools.info.ui.fragments.PickerContentFragment;

/**
 * Created by tinashe on 2016/01/28.
 */
public class PickerFragmentAdapter extends FragmentStatePagerAdapter {

    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public PickerFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return PickerContentFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Book".toUpperCase();
            case 1:
                return "Chapter".toUpperCase();
            default:
                return "Verse".toUpperCase();
        }

    }
}
