package rawcomposition.bibletools.info.ui.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.model.References;
import rawcomposition.bibletools.info.ui.MainActivity;
import rawcomposition.bibletools.info.ui.adapters.ReferenceListAdapter;

/**
 * Created by tinashe on 2015/02/15.
 */
public class ReferencesFragment extends BaseFragment {

    private RecyclerView mRecycler;

    private ReferenceListAdapter mAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_references;
    }

    @Override
    protected void initialize(View rootView) {
        super.initialize(rootView);

        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Default to Genesis 1:1
        ((MainActivity)getActivity())
                .onQueryTextSubmit("1 1 1");


    }

    public void displayReferences(References references){
        mAdapter = new ReferenceListAdapter(references.getResources(), getActivity());

        mRecycler.setAdapter(mAdapter);
    }
}
