package rawcomposition.bibletools.info.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import rawcomposition.bibletools.info.BibleToolsApp;
import rawcomposition.bibletools.info.Bindings;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.model.ReferenceMap;
import rawcomposition.bibletools.info.model.json.Reference;
import rawcomposition.bibletools.info.ui.callbacks.OnNavigationListener;
import rawcomposition.bibletools.info.util.AnimUtil;
import rawcomposition.bibletools.info.util.FavouritesUtil;
import rawcomposition.bibletools.info.util.TextViewUtil;
import rawcomposition.bibletools.info.util.ThemeUtil;
import rawcomposition.bibletools.info.util.ToastUtil;
import rawcomposition.bibletools.info.util.enums.FontWeight;

/**
 * Created by tinashe on 2015/02/15.
 */
public class ReferenceListAdapter extends RecyclerView.Adapter<ReferenceListAdapter.ReferenceViewHolder> {

    private static final String TAG = ReferenceListAdapter.class.getName();

    private static final int TITLE = 0;
    private static final int NON_TITLE = 1;

    private List<Reference> mReferences = new ArrayList<>();

    private int mLastAnimatedPosition = -1;

    private OnNavigationListener mListener;

    private static FontWeight fontWeight = null;

    private LayoutInflater inflater;

    public ReferenceListAdapter(OnNavigationListener listener) {
        this.mListener = listener;

        fontWeight = ThemeUtil.getFontWeight(BibleToolsApp.getAppContext());

    }

    public void setReferences(List<Reference> mReferences) {
        this.mReferences = mReferences;
        this.mLastAnimatedPosition = -1;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == TITLE) {
            return super.getItemViewType(position);
        } else {
            return NON_TITLE;
        }

    }

    @Override
    public ReferenceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView;

        if (inflater == null) {
            inflater = LayoutInflater.from(viewGroup.getContext());
        }

        if (viewType == TITLE) {
            itemView = inflater
                    .inflate(R.layout.layout_reference_verse_item, viewGroup, false);
        } else {
            itemView = inflater
                    .inflate(R.layout.layout_reference_item, viewGroup, false);
        }

        return new ReferenceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReferenceViewHolder holder, final int position) {

        Context context = holder.itemView.getContext();

        if (position > mLastAnimatedPosition) {
            mLastAnimatedPosition = position;

            AnimUtil.slideInEnterAnimation(context, holder.itemView);
        }

        final Reference reference = mReferences.get(position);

        if (fontWeight != FontWeight.LIGHT) {
            holder.title.setText(Html.fromHtml("<b>" + reference.getTitle() + "</b>"));
        } else {
            holder.title.setText(Html.fromHtml(reference.getTitle()));
        }


        if (!ThemeUtil.isDarkTheme(context)) {
            if (position != 0) {
                holder.referenceTop.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.reference_title_background));
            }

            holder.referenceWhiteView.setBackgroundColor(Color.WHITE);

        }


        final TextView content = holder.content;

        if (position == TITLE) {
            holder.itemView.setOnClickListener(view -> {
                //context.startActivity(new Intent(context, ReferenceActivity.class));
            });

            content.setText(reference.getText());
            content.setMaxLines(Integer.MAX_VALUE);

            if (TextUtils.isEmpty(reference.getPrevious())) {
                holder.navigatePrevious.setVisibility(View.GONE);
            } else {
                holder.navigatePrevious.setVisibility(View.VISIBLE);
                holder.navigatePrevious.setOnClickListener(v -> mListener.onPrevious(reference.getPrevious()));
            }

            if (TextUtils.isEmpty(reference.getNext())) {
                holder.navigateNext.setVisibility(View.GONE);
            } else {
                holder.navigateNext.setVisibility(View.VISIBLE);
                holder.navigateNext.setOnClickListener(v -> mListener.onNext(reference.getNext()));
            }

            boolean isInFav = FavouritesUtil.isInFavourites(getRealm(), reference.getTitle());

            if (isInFav) {
                holder.toggleFav.setImageResource(R.drawable.ic_favorite_white);

                setUnFavouriteListener(holder.toggleFav, reference);

            } else {
                holder.toggleFav.setImageResource(R.drawable.ic_favorite_outline);

                setFavouriteListener(holder.toggleFav, reference);
            }

        } else {

            if (TextUtils.isEmpty(reference.getFileName())) {

                content.setVisibility(View.VISIBLE);
                holder.refShareItem.setVisibility(View.VISIBLE);
                holder.refMap.setVisibility(View.GONE);

                if (position == 1) {
                    content.setText(Html.fromHtml(TextViewUtil.implementBCbold(reference.getContent())));
                } else {
                    content.setText(Html.fromHtml(reference.getContent()));
                }

                setOptionsListener(holder.refShareItem, reference);

                TextViewUtil.setVerseClickListener(content, mListener);

                content.setOnClickListener(v -> toggleReferenceView(reference, content, position));

                holder.itemView.setOnClickListener(v -> toggleReferenceView(reference, content, position));

                if (!reference.isCollapsed()) {
                    content.setMaxLines(4);
                }

            } else {

                content.setVisibility(View.GONE);
                holder.refShareItem.setVisibility(View.GONE);
                holder.refMap.setVisibility(View.VISIBLE);

                final String url = context.getString(R.string.base_maps_url) + reference.getFileName();

                Glide.with(context)
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(final GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                holder.itemView.setOnClickListener(v -> {

                                    ReferenceMap map = new ReferenceMap();
                                    map.setMapTitle(reference.getTitle());
                                    map.setMapUrl(url);

                                    mListener.onMapSelected(holder.refMap, map);
                                });
                                return false;
                            }
                        })
                        .into(holder.refMap);


            }


        }


    }

    private void toggleReferenceView(Reference reference, TextView content, final int position) {

        if (reference.isCollapsed()) {
            content.setMaxLines(4);
            reference.setCollapsed(false);
            new Handler().postDelayed(() -> mListener.onScrollRequired(position), 500);

            this.notifyItemChanged(0);

        } else {
            content.setMaxLines(Integer.MAX_VALUE);
            reference.setCollapsed(true);
        }
    }

    @Override
    public int getItemCount() {
        return mReferences.size();
    }

    private void setOptionsListener(final ImageView imageView, final Reference item) {

        Context context = imageView.getContext();

        Reference first = mReferences.get(0);

        final String subject = first.getTitle()
                + " - " + item.getTitle();

        final CharSequence text = TextUtils.isEmpty(first.getLink()) ? Html.fromHtml(item.getContent())
                : Html.fromHtml(item.getContent()) + "\n" + first.getLink();

        imageView.setOnClickListener(v -> TextViewUtil.shareOrSearch(context, subject, text, true));
    }


    private void showToast(String message) {
        ToastUtil.show(BibleToolsApp.getAppContext(), message);
    }

    private Realm getRealm() {
        return mListener.getRealm();
    }

    private void setFavouriteListener(final ImageView view, final Reference reference) {
        view.setOnClickListener(v -> {
            FavouritesUtil.addFavourite(getRealm(),
                    reference.getTitle(),
                    reference.getText());

            showToast("Added to Favourites");

            view.setImageResource(R.drawable.ic_favorite_white);

            setUnFavouriteListener(view, reference);
        });
    }

    private void setUnFavouriteListener(final ImageView view, final Reference reference) {
        view.setOnClickListener(v -> {
            FavouritesUtil.unFavourite(getRealm(),
                    reference.getTitle());

            showToast("Removed from Favourites");

            view.setImageResource(R.drawable.ic_favorite_outline);

            setFavouriteListener(view, reference);
        });
    }

    static class ReferenceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.reference_title)
        TextView title;
        @BindView(R.id.reference_text)
        TextView content;

        @Nullable
        @BindView(R.id.navigate_previous)
        View navigatePrevious;
        @Nullable
        @BindView(R.id.navigate_next)
        View navigateNext;

        @Nullable
        @BindView(R.id.action_share)
        ImageView refShareItem;
        @Nullable
        @BindView(R.id.reference_map)
        ImageView refMap;
        @Nullable
        @BindView(R.id.action_favourite)
        ImageView toggleFav;

        @Nullable
        @BindView(R.id.reference_top)
        View referenceTop;
        @Nullable
        @BindView(R.id.reference_white_view)
        View referenceWhiteView;

        ReferenceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (fontWeight != FontWeight.LIGHT) {
                Bindings.setFont(title, fontWeight.getName());
                Bindings.setFont(content, fontWeight.getName());
            }

            Context context = itemView.getContext();
            boolean dark = ThemeUtil.isDarkTheme(context);
            content.setTextColor(ContextCompat.getColor(context, dark ? android.R.color.white : R.color.text));
        }
    }
}
