package rawcomposition.bibletools.info.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;

import de.psdev.licensesdialog.LicensesDialog;
import rawcomposition.bibletools.info.BuildConfig;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.util.PreferenceUtil;

public class SettingsActivity extends BaseActivity {

    private boolean mSettingChanged = false;

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

    @Override
    protected boolean isSettings() {
        return true;
    }

    public void setSettingChanged(boolean mSettingChanged) {
        this.mSettingChanged = mSettingChanged;
    }

    @Override
    public void onBackPressed(){
        if(mSettingChanged){
            startAnActivity(new Intent(this, MainActivity.class));
            finish();
        }

        super.onBackPressed();

    }

    public static class SettingsFragment extends PreferenceFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener{

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

                            ((BaseActivity) getActivity())
                                    .sendFeedBack();

                            return true;
                        }
                    });
            getPreferenceManager()
                    .findPreference("pref_donate")
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {

                            ((BaseActivity) getActivity())
                                    .onDonateButtonClicked();

                            return true;
                        }
                    });

            findPreference(getString(R.string.pref_theme_type))
                    .setOnPreferenceChangeListener(this);

            findPreference(getString(R.string.pref_font_weight))
                    .setOnPreferenceChangeListener(this);

            findPreference("pref_open_source")
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            new LicensesDialog.Builder(getActivity())
                                    .setNotices(R.raw.notices)
                                    .setIncludeOwnLicense(true)
                                    //.setThemeResourceId(R.style.custom_theme)
                                   // .setDividerColorId(R.color.custom_divider_color)
                                   .build().show();

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

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {

            if(preference.getKey().equals(getString(R.string.pref_theme_type))){
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();

                return true;
            } else if(preference.getKey().equals(getString(R.string.pref_font_weight))){

                ((SettingsActivity)getActivity())
                        .setSettingChanged(true);

                return true;
            }


            return false;
        }
    }
}
