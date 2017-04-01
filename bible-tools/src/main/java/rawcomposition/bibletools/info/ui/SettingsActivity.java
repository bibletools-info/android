package rawcomposition.bibletools.info.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.preference.Preference;

import de.psdev.licensesdialog.LicensesDialog;
import rawcomposition.bibletools.info.BuildConfig;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.ui.base.BaseActivity;
import rawcomposition.bibletools.info.util.PreferenceUtil;

public class SettingsActivity extends BaseActivity {

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
    protected int layoutRes() {
        return R.layout.activity_settings;
    }

    @Override
    protected boolean showHomeAsUp() {
        return true;
    }

    @Override
    protected boolean isSettings() {
        return true;
    }


    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);

    }

    @Override
    public void hookUpPresenter() {

    }

    public static class SettingsFragment extends PreferenceFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences);

            initialize();
        }

        private void initialize() {

            getPreferenceManager()
                    .findPreference(getString(R.string.pref_key_version))
                    .setSummary(BuildConfig.VERSION_NAME);

            String entries = PreferenceUtil.getValue(getActivity(), getString(R.string.pref_key_history_entries), "10");
            getPreferenceManager()
                    .findPreference(getString(R.string.pref_key_history_entries))
                    .setTitle(getString(R.string.settings_history_count_title) + " :" + entries);

            getPreferenceManager()
                    .findPreference(getString(R.string.pref_feedback))
                    .setOnPreferenceClickListener(preference -> {

                        ((BaseActivity) getActivity())
                                .sendFeedBack();

                        return true;
                    });
            getPreferenceManager()
                    .findPreference("pref_donate")
                    .setOnPreferenceClickListener(preference -> {

                        ((BaseActivity) getActivity())
                                .onDonateButtonClicked();

                        return true;
                    });

            findPreference(getString(R.string.pref_theme_type))
                    .setOnPreferenceChangeListener(this);

            findPreference(getString(R.string.pref_font_weight))
                    .setOnPreferenceChangeListener(this);

            findPreference("pref_open_source")
                    .setOnPreferenceClickListener(preference -> {
                        new LicensesDialog.Builder(getActivity())
                                .setNotices(R.raw.notices)
                                .setIncludeOwnLicense(true)
                                //.setThemeResourceId(R.style.custom_theme)
                                // .setDividerColorId(R.color.custom_divider_color)
                                .build().show();

                        return true;
                    });

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (key.equals(getString(R.string.pref_key_history_entries))) {
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

            if (preference.getKey().equals(getString(R.string.pref_theme_type))) {

                getActivity().recreate();

                return true;
            } else if (preference.getKey().equals(getString(R.string.pref_font_weight))) {

               // ((SettingsActivity) getActivity())
                        //.setSettingChanged(true);

                return true;
            }


            return false;
        }
    }
}
