package rawcomposition.bibletools.info.ui.home.map

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_map_detail.*
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.utils.glide.GlideApp
import rawcomposition.bibletools.info.utils.glide.PhotoTarget


class MapDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_detail)

        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener { supportFinishAfterTransition() }

        GlideApp.with(this)
                .load(MAP_URL)
                .into(PhotoTarget(photo, progress))

        ViewCompat.setTransitionName(photo, EXTRA_IMAGE)
    }

    companion object {
        private const val EXTRA_IMAGE = "arg:img"

        private lateinit var MAP_URL: String

        fun view(activity: AppCompatActivity, transitionView: View, url: String) {
            MAP_URL = url

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity, transitionView, EXTRA_IMAGE)

            val intent = Intent(activity, MapDetailActivity::class.java)
            ActivityCompat.startActivity(activity, intent, options.toBundle())
        }
    }
}