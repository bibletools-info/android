package rawcomposition.bibletools.info.util.glide;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by tinashe on 2016/11/01.
 */

public class PhotoTarget extends GlideDrawableImageViewTarget {

    private ProgressBar progressBar;

    public PhotoTarget(ImageView view, ProgressBar progressBar) {
        super(view);
        this.progressBar = progressBar;
    }

    @Override
    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
        super.onResourceReady(resource, animation);
        progressBar.setVisibility(View.GONE);
        PhotoViewAttacher attacher = new PhotoViewAttacher(getView());
        attacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }
}
