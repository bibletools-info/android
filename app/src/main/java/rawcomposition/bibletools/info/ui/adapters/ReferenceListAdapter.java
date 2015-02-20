package rawcomposition.bibletools.info.ui.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.model.json.Reference;
import rawcomposition.bibletools.info.ui.callbacks.OnNavigationListener;
import rawcomposition.bibletools.info.util.AnimUtil;
import rawcomposition.bibletools.info.util.TextViewUtil;

/**
 * Created by tinashe on 2015/02/15.
 */
public class ReferenceListAdapter extends RecyclerView.Adapter<ReferenceListAdapter.ReferenceViewHolder>{

    private static final String TAG = ReferenceListAdapter.class.getName();

    private static final int TITLE = 0;
    private static final int NON_TITLE = 1;


    private List<Reference> mReferences;

    private Context context;

    private int mLastAnimatedPosition = -1;

    private OnNavigationListener mListener;

    public ReferenceListAdapter(Context context, List<Reference> references, OnNavigationListener listener) {
        this.mReferences = references;
        this.context = context;
        this.mListener = listener;

    }

    public void setReferences(List<Reference> mReferences) {
        this.mReferences = mReferences;
        this.mLastAnimatedPosition = -1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == TITLE){
            return super.getItemViewType(position);
        } else {
            return NON_TITLE;
        }

    }

    @Override
    public ReferenceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView;

        if(viewType == TITLE){
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.layout_reference_verse_item, viewGroup, false);
        } else {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.layout_reference_item, viewGroup, false);
        }


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

        if(position == TITLE){
            content.setText(reference.getText());

            if(TextUtils.isEmpty(reference.getPrevious())){
                holder.navigatePrevious.setVisibility(View.GONE);
            } else {
                holder.navigatePrevious.setVisibility(View.VISIBLE);
                holder.navigatePrevious.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onPrevious(reference.getPrevious());
                    }
                });
            }

            if(TextUtils.isEmpty(reference.getNext())){
                holder.navigateNext.setVisibility(View.GONE);
            } else {
                holder.navigateNext.setVisibility(View.VISIBLE);
                holder.navigateNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onNext(reference.getNext());
                    }
                });
            }

        } else {

            content.setText(Html.fromHtml(reference.getContent()));

            setOptionsListener(holder.refOptions, reference);
        }

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

        private View navigatePrevious;
        private View navigateNext;

        private ImageView refOptions;

        public ReferenceViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.reference_title);
            content = (TextView) itemView.findViewById(R.id.reference_text);

            navigatePrevious = itemView.findViewById(R.id.navigate_previous);
            navigateNext = itemView.findViewById(R.id.navigate_next);

            refOptions = (ImageView) itemView.findViewById(R.id.action_options);
        }
    }

    private void setOptionsListener(final ImageView imageView, final Reference item){
        final String subject = mReferences.get(0).getTitle()
                + " - " + item.getTitle();
        final CharSequence text = Html.fromHtml(item.getContent());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, imageView);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.menu_ref_options, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()){
                            case R.id.action_share:
                                TextViewUtil.shareOrSearch(context, subject, text, true);
                                return true;
                            case R.id.action_copy:
                                return true;
                            case R.id.action_favourite:
                                return true;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }
}
