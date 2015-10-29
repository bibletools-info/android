package rawcomposition.bibletools.info;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import rawcomposition.bibletools.info.util.FontCache;
import rawcomposition.bibletools.info.util.enums.FontWeight;

/**
 * Created by tinashe on 2015/07/26.
 */
public class BibleToolsApp extends Application {

    private static final String TAG = BibleToolsApp.class.getName();

    private static final int REALM_VERSION = 2;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .migration(new BibleToolsRealmMigration())
                .schemaVersion(REALM_VERSION)
                .build();
        Realm.setDefaultConfiguration(config);

        //Init Fonts
        for (FontWeight weight : FontWeight.values()) {
            FontCache.getInstance().addFont(weight.getName(), weight.getFileName());
        }
    }

    class BibleToolsRealmMigration implements RealmMigration {

        @Override
        public long execute(Realm realm, long version) {
            return REALM_VERSION;
        }
    }

    public static Context getAppContext() {
        return BibleToolsApp.context;
    }
}
