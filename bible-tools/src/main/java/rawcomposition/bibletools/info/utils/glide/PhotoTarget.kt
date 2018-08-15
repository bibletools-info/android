package rawcomposition.bibletools.info.utils.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import rawcomposition.bibletools.info.utils.hide

class PhotoTarget constructor(imageView: ImageView,
                              private val progressBar: ProgressBar) : DrawableImageViewTarget(imageView) {

    override fun onResourceReady(resource: Drawable?, transition: Transition<in Drawable>?) {
        super.onResourceReady(resource, transition)

        progressBar.hide()
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)
        Toast.makeText(getView().context, "load failed!", Toast.LENGTH_SHORT).show()
    }
}