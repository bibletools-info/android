package rawcomposition.bibletools.info.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tinashe on 2015/02/15.
 */
public abstract class BaseFragment extends Fragment {

    protected abstract int getLayoutResource();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResource(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);
    }

    protected void initialize(View rootView) {

    }


    protected void onDownScrolling() {
        ActionBar ab = ((ActionBarActivity) getActivity())
                .getSupportActionBar();

        if (ab == null) {
            return;
        }

        if (!ab.isShowing()) {

            ab.show();
        }
    }

    protected void onUpScrolling() {

        ActionBar ab = ((ActionBarActivity) getActivity())
                .getSupportActionBar();

        if (ab == null) {
            return;
        }

        if (ab.isShowing()) {

            ab.hide();
        }
    }
}
