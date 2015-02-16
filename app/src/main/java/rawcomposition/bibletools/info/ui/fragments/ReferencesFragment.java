package rawcomposition.bibletools.info.ui.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.model.json.Reference;
import rawcomposition.bibletools.info.model.json.References;
import rawcomposition.bibletools.info.ui.MainActivity;
import rawcomposition.bibletools.info.ui.adapters.ReferenceListAdapter;

/**
 * Created by tinashe on 2015/02/15.
 */
public class ReferencesFragment extends BaseFragment {

    private ReferenceListAdapter mAdapter;

    private ProgressBar mProgress;

    private List<Reference> mReferences = new ArrayList<>();

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_references;
    }

    @Override
    protected void initialize(View rootView) {
        super.initialize(rootView);

        RecyclerView mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProgress = (ProgressBar) rootView.findViewById(R.id.progress);

        mAdapter = new ReferenceListAdapter(mReferences, getActivity());
        mRecycler.setAdapter(mAdapter);

        //Default to Genesis 1:1
        ((MainActivity)getActivity())
                .performQuery("1 1 1");


    }

    public void displayReferences(References references){
        mProgress.setVisibility(View.GONE);

        this.mReferences = references.getResources();
        mAdapter.setReferences(mReferences);
        mAdapter.notifyDataSetChanged();
    }
}
