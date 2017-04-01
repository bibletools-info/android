package rawcomposition.bibletools.info.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import rawcomposition.bibletools.info.ui.references.MainActivity;

/**
 * Created by tinashe on 2015/10/26.
 */
public class AppLauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(this, MainActivity.class));

        new Handler().postDelayed(() -> finish(), 300);
    }
}
