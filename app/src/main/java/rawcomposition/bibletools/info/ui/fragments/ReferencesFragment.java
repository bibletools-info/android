package rawcomposition.bibletools.info.ui.fragments;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.model.json.Reference;
import rawcomposition.bibletools.info.model.json.References;
import rawcomposition.bibletools.info.ui.MainActivity;
import rawcomposition.bibletools.info.ui.adapters.ReferenceListAdapter;
import rawcomposition.bibletools.info.util.CacheUtil;
import rawcomposition.bibletools.info.util.DeviceUtil;
import rawcomposition.bibletools.info.util.GSonUtil;

/**
 * Created by tinashe on 2015/02/15.
 */
public class ReferencesFragment extends BaseFragment {

    private ReferenceListAdapter mAdapter;

    private ProgressBar mProgress;

    private List<Reference> mReferences = new ArrayList<>();

    private RecyclerView mRecycler;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_references;
    }

    @Override
    protected void initialize(View rootView) {
        super.initialize(rootView);

        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler);
      //  mRecycler.setHasFixedSize(true);

        if(DeviceUtil.isTablet(getActivity())){
            StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            mRecycler.setLayoutManager(manager);
            
        } else {
            mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        mProgress = (ProgressBar) rootView.findViewById(R.id.progress);

        mAdapter = new ReferenceListAdapter(mReferences, getActivity());
        mRecycler.setAdapter(mAdapter);

        showCache();


    }

    public void displayReferences(References references, boolean smoothScroll){
        mProgress.setVisibility(View.GONE);

        this.mReferences = references.getResources();
        mAdapter.setReferences(mReferences);
        mAdapter.notifyDataSetChanged();

        if(smoothScroll){
            mRecycler.smoothScrollToPosition(0);
        }
    }

    private void showCache(){
        String jsonString = CacheUtil.find(getActivity(), References.class.getName());

        if(!TextUtils.isEmpty(jsonString)){
            References references = GSonUtil.getInstance().fromJson(jsonString, References.class);

            if(references != null){
                displayReferences(references, false);

                return;
            }
        }

        //No cache fetch Genesis 1:1
        ((MainActivity)getActivity())
                .performQuery("1 1 1");
    }
}
