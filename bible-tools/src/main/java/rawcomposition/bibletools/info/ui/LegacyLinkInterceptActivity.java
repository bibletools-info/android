package rawcomposition.bibletools.info.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import rawcomposition.bibletools.info.ui.references.MainActivity;

import static rawcomposition.bibletools.info.ui.references.MainActivity.VERSE_KEY;

/**
 * Created by tinashe on 2016/10/02.
 */

public class LegacyLinkInterceptActivity extends AppCompatActivity {

    private static final String TAG = LegacyLinkInterceptActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            String verse = data.getQueryParameter(VERSE_KEY);
            Log.d(TAG, "VERSE: " + verse);

            if (!TextUtils.isEmpty(verse)) {
                Intent main = new Intent(this, MainActivity.class);
                main.putExtra(VERSE_KEY, verse);
                startActivity(main);
            }
        }

        finish();
    }
}
