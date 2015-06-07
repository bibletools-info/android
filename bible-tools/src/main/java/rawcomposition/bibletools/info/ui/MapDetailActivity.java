package rawcomposition.bibletools.info.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.model.ReferenceMap;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * created by tinashe
 */
public class MapDetailActivity extends AppCompatActivity {

    private static final String MAP_OBJ = "arg:map";
    private static final String EXTRA_IMAGE = "arg:img";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map_detail);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.app_action_bar);
        if (mToolbar != null) {
            try {

                ViewCompat.setTranslationZ(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));
                ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));


                setSupportActionBar(mToolbar);

                if (getSupportActionBar() != null)
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } catch (Throwable ex) {
                //
            }

        }
        setTitle("");

        if(getIntent().getExtras() != null){
            ReferenceMap map = (ReferenceMap)getIntent().getSerializableExtra(MAP_OBJ);
            ImageView imageView = (ImageView) findViewById(R.id.fullscreen_image);
            ViewCompat.setTransitionName(imageView, EXTRA_IMAGE);

            Glide.with(this)
                    .load(map.getMapUrl())
                    .into(imageView);

            new PhotoViewAttacher(imageView);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void launch(BaseActivity activity, View transitionView, ReferenceMap referenceMap) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, transitionView, EXTRA_IMAGE);

        Intent intent = new Intent(activity, MapDetailActivity.class);
        intent.putExtra(MAP_OBJ, referenceMap);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }


}
