package rawcomposition.bibletools.info.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.model.ReferenceMap;
import rawcomposition.bibletools.info.ui.base.BaseActivity;
import rawcomposition.bibletools.info.util.VersionUtils;
import rawcomposition.bibletools.info.util.glide.PhotoTarget;

/**
 * created by tinashe
 */
public class MapDetailActivity extends BaseActivity {

    private static final String MAP_OBJ = "arg:map";
    private static final String EXTRA_IMAGE = "arg:img";

    private static final int[] NORMAL = new int[]{480, 400};

    @BindView(R.id.photo)
    ImageView imageView;

    @BindView(R.id.progress)
    ProgressBar progressBar;


    @Override
    public void hookUpPresenter() {
        setUpMap();
    }

    private void setUpMap() {
        if (!getIntent().hasExtra(MAP_OBJ)) {
            finish();
            return;
        }

        ReferenceMap map = (ReferenceMap) getIntent().getSerializableExtra(MAP_OBJ);
        ViewCompat.setTransitionName(imageView, EXTRA_IMAGE);

        if (VersionUtils.isAtLeastL()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        Glide.with(this)
                .load(map.getMapUrl())
                .placeholder(R.color.theme_accent)
                .override(NORMAL[0], NORMAL[1])
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new PhotoTarget(imageView, progressBar));
    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_map_detail;
    }

    @Override
    protected boolean showHomeAsUp() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void launch(Activity activity, View transitionView, ReferenceMap referenceMap) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, transitionView, EXTRA_IMAGE);

        Intent intent = new Intent(activity, MapDetailActivity.class);
        intent.putExtra(MAP_OBJ, referenceMap);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
}
