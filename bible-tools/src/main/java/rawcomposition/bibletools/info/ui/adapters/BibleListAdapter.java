package rawcomposition.bibletools.info.ui.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.util.ThemeUtil;

/**
 * Created by tinashe on 2016/01/28.
 */
public class BibleListAdapter extends RecyclerView.Adapter<BibleListAdapter.ItemViewHolder> {

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
        String text = mItems.get(position);

        holder.textView.setText(text);

        Context context = holder.itemView.getContext();


        if (!TextUtils.isDigitsOnly(text) && position > 38) {
            holder.textView.setTextColor(ContextCompat.getColor(context, R.color.nt_red));
        } else {
            holder.textView.setTextColor(ContextCompat.getColor(
                    context, ThemeUtil.isDarkTheme(context) ? android.R.color.white : R.color.text));
        }

        holder.itemView.setOnClickListener(v -> callback.onItemSelected(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text)
        TextView textView;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemListener {
        void onItemSelected(int position);
    }
}
