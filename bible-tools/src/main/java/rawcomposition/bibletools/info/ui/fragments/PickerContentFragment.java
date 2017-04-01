package rawcomposition.bibletools.info.ui.fragments;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.ui.adapters.BibleListAdapter;
import rawcomposition.bibletools.info.ui.callbacks.VersePickerController;

/**
 * Created by tinashe on 2016/01/28.
 */
public class PickerContentFragment extends BaseFragment implements BibleListAdapter.ItemListener {

    private VersePickerController controller;

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private BibleListAdapter mAdapter = new BibleListAdapter(this);

    private int mPosition = 0;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_picker_content;
    }

    public static PickerContentFragment newInstance(int position) {
        PickerContentFragment fragment = new PickerContentFragment();
        fragment.setPosition(position);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            controller = (VersePickerController) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.getClass().getName() + " must implement VersePickerController");
        }
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    @Override
    protected void initialize(View rootView) {
        super.initialize(rootView);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setAdapter(mAdapter);

        updateView();
    }

    public void updateView() {
        List<String> content = new ArrayList<>();

        switch (mPosition) {
            case 0:
                content = controller.getBooks();
                break;
            case 1:
                content = controller.getChapters();
                break;
            case 2:
                content = controller.getVerses();
                break;
        }

        mAdapter.setItems(content);
    }

    @Override
    public void onItemSelected(int position) {
        switch (mPosition) {
            case 0:
                controller.onBookSelected(position);
                break;
            case 1:
                controller.onChapterSelected(position);
                break;
            case 2:
                controller.onVerseSelected(position);
                break;
        }
    }
}
