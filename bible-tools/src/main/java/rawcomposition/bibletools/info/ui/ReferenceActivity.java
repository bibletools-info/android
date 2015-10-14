package rawcomposition.bibletools.info.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import rawcomposition.bibletools.info.R;

/**
 * Created by tinashe on 2015/10/14.
 */
public class ReferenceActivity extends AppCompatActivity {

    private static final String TAG = ReferenceActivity.class.getName();

    SlidingUpPanelLayout mPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reference);

        mPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        mPanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
                finish();
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });

        mPanel.setAnchorPoint(0.5f);
        mPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);

        findViewById(R.id.main_content).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                return false;
            }
        });
    }
}
