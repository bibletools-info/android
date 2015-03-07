package rawcomposition.bibletools.info.ui;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;

import rawcomposition.bibletools.info.BuildConfig;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.util.PreferenceUtil;

public class SettingsActivity extends BaseActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SettingsFragment())
                    .commit();
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_settings, menu);

        return super.onCreateOptionsMenu(menu);
    }
*/

    public static class SettingsFragment extends PreferenceFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

            initialize();
        }

        private void initialize(){

            getPreferenceManager()
                    .findPreference(getString(R.string.pref_key_version))
                    .setSummary(BuildConfig.VERSION_NAME);

            String entries = PreferenceUtil.getValue(getActivity(), getString(R.string.pref_key_history_entries), "10");
            getPreferenceManager()
                    .findPreference(getString(R.string.pref_key_history_entries))
                    .setTitle(getString(R.string.settings_history_count_title) + " :" + entries);

            getPreferenceManager()
                    .findPreference(getString(R.string.pref_feedback))
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {

                            ((BaseActivity)getActivity())
                                    .sendFeedBack();

                            return true;
                        }
                    });

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if(key.equals(getString(R.string.pref_key_history_entries))){
                String entries = PreferenceUtil.getValue(getActivity(), getString(R.string.pref_key_history_entries), "10");
                getPreferenceManager()
                        .findPreference(getString(R.string.pref_key_history_entries))
                        .setTitle(getString(R.string.settings_history_count_title) + " :" + entries);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }
        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}
