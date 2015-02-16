package rawcomposition.bibletools.info.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.model.json.Reference;
import rawcomposition.bibletools.info.util.AnimUtil;

/**
 * Created by tinashe on 2015/02/15.
 */
public class ReferenceListAdapter extends RecyclerView.Adapter<ReferenceListAdapter.ReferenceViewHolder>{

    private List<Reference> mReferences;

    private Context context;

    private int mLastAnimatedPosition = -1;

    public ReferenceListAdapter(List<Reference> references, Context context) {
        this.mReferences = references;
        this.context = context;
    }

    public void setReferences(List<Reference> mReferences) {
        this.mReferences = mReferences;
    }

    @Override
    public ReferenceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_reference_item, viewGroup, false);

        return new ReferenceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReferenceViewHolder holder, int position) {

        if(position > mLastAnimatedPosition){
            mLastAnimatedPosition = position;

            AnimUtil.slideInEnterAnimation(context, holder.itemView);
        }

        final Reference reference = mReferences.get(position);

        holder.title.setText((reference.getTitle()));

        final TextView content = holder.content;
        content.setText(Html.fromHtml(reference.getContent()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(reference.isCollapsed()){
                    content.setMaxLines(Integer.MAX_VALUE);
                    reference.setCollapsed(false);
                } else {
                    content.setMaxLines(4);
                    reference.setCollapsed(true);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mReferences.size();
    }

    public static class ReferenceViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView content;

        public ReferenceViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.reference_title);
            content = (TextView) itemView.findViewById(R.id.reference_text);
        }
    }
}
