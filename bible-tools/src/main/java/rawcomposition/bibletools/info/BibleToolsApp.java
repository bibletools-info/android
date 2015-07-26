package rawcomposition.bibletools.info;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * Created by tinashe on 2015/07/26.
 */
public class BibleToolsApp extends Application {

    private static final String TAG = BibleToolsApp.class.getName();

    private static final int REALM_VERSION = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .migration(new BibleToolsRealmMigration())
                .schemaVersion(REALM_VERSION)
                .build();
        Realm.setDefaultConfiguration(config);
    }

    class BibleToolsRealmMigration implements RealmMigration{

        @Override
        public long execute(Realm realm, long version) {
            return REALM_VERSION;
        }
    }
}
