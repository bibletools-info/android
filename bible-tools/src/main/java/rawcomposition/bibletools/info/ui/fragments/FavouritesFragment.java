package rawcomposition.bibletools.info.ui.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;

import io.realm.Realm;
import rawcomposition.bibletools.info.BibleToolsApplication;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.ui.FavouritesActivity;
import rawcomposition.bibletools.info.ui.adapters.FavouriteVersesAdapter;
import rawcomposition.bibletools.info.util.DeviceUtil;
import rawcomposition.bibletools.info.util.FavouritesUtil;
import rawcomposition.bibletools.info.util.enums.ViewType;

/**
 * Created by tinashe on 2015/03/05.
 */
public class FavouritesFragment extends BaseFragment {

    FavouriteVersesAdapter mAdapter;

    private ViewType mViewType = ViewType.ALL;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_favourites;
    }

    @Override
    protected void initialize(View rootView) {
        super.initialize(rootView);

        RecyclerView mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler);
        //  mRecycler.setHasFixedSize(true);

        if (DeviceUtil.isTablet(getActivity())) {
            StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            mRecycler.setLayoutManager(manager);

        } else {
            mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        }


        mAdapter = new FavouriteVersesAdapter(
                FavouritesUtil.getAllFavourites(getRealm()),
                getActivity());

        mRecycler.setAdapter(mAdapter);

        if(mAdapter.getItemCount() == 0){
            rootView.findViewById(R.id.txt_empty)
                    .setVisibility(View.VISIBLE);
        }


    }

    public void performSearch(String query){

        if(!TextUtils.isEmpty(query)){
            mAdapter.setVerses(FavouritesUtil.queryFavourites(getRealm(),
                    query));
        } else {
            mAdapter.setVerses(FavouritesUtil.getAllFavourites(getRealm()));
            this.mViewType = ViewType.ALL;
        }

    }

    private Realm getRealm(){
        return ((FavouritesActivity)getActivity())
                .getRealm();
    }

    public ViewType getViewType() {
        return mViewType;
    }

    public void setViewType(ViewType mViewType) {
        this.mViewType = mViewType;

        mAdapter.setVerses(FavouritesUtil.filterList(getActivity(),
                getRealm(),
                mViewType));
    }


}
