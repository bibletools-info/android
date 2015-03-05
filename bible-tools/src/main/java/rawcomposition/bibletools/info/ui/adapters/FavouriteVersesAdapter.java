package rawcomposition.bibletools.info.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.realm.Realm;
import rawcomposition.bibletools.info.BibleToolsApplication;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.model.FavouriteVerse;
import rawcomposition.bibletools.info.util.AnimUtil;
import rawcomposition.bibletools.info.util.FavouritesUtil;

/**
 * Created by tinashe on 2015/03/05.
 */
public class FavouriteVersesAdapter extends RecyclerView.Adapter<FavouriteVersesAdapter.VerseViewHolder>{

    private List<FavouriteVerse> mVerses;

    private Activity context;

    private int mLastAnimatedPosition = -1;

    public FavouriteVersesAdapter(List<FavouriteVerse> verses, Activity context) {
        this.mVerses = verses;
        this.context = context;
    }

    @Override
    public VerseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_reference_verse_item, parent, false);

        return new VerseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VerseViewHolder holder, int position) {
        if(position > mLastAnimatedPosition){
            mLastAnimatedPosition = position;

            AnimUtil.slideInEnterAnimation(context, holder.itemView);
        }

        final FavouriteVerse verse = mVerses.get(position);

        holder.title.setText(verse.getVerseCode());
        holder.content.setText(verse.getVerseText());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = ((BibleToolsApplication)context.getApplication())
                        .getRealm();
                FavouritesUtil.unFavourite(realm, verse.getVerseCode());
                setVerses(FavouritesUtil.getAllFavourites(realm));
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("bibletools://path?verse=" + verse.getVerseCode()));

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVerses.size();
    }

    public void setVerses(List<FavouriteVerse> mVerses) {
        this.mVerses = mVerses;
        this.notifyDataSetChanged();
    }

    public List<FavouriteVerse> getVerses() {
        return mVerses;
    }

    public static class VerseViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView content;

        ImageView delete;

        public VerseViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.reference_title);
            content = (TextView) itemView.findViewById(R.id.reference_text);
            content.setMaxLines(Integer.MAX_VALUE);

            itemView.findViewById(R.id.navigate_previous)
                    .setVisibility(View.GONE);
            delete = (ImageView) itemView.findViewById(R.id.navigate_next);
            delete.setVisibility(View.VISIBLE);
            delete.setImageResource(R.drawable.ic_delete);
            itemView.findViewById(R.id.action_favourite)
                    .setVisibility(View.GONE);
        }
    }
}
