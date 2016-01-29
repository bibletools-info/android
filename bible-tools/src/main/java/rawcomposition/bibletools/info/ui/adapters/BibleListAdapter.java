package rawcomposition.bibletools.info.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rawcomposition.bibletools.info.R;

/**
 * Created by tinashe on 2016/01/28.
 */
public class BibleListAdapter extends RecyclerView.Adapter<BibleListAdapter.ItemViewHolder>{

    private List<String> mItems = new ArrayList<>();

    private ItemListener callback;

    public BibleListAdapter(ItemListener callback) {
        this.callback = callback;
    }

    public void setItems(List<String> mItems) {
        this.mItems = mItems;
        this.notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(
                LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        holder.textView.setText(
                mItems.get(position)
        );

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onItemSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.text)
        TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemListener {
        void onItemSelected(int position);
    }
}
