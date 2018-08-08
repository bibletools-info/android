package rawcomposition.bibletools.info.ui.settings

import android.os.Bundle
import android.support.v14.preference.PreferenceFragment
import android.support.v4.app.NavUtils
import android.support.v7.preference.Preference
import android.view.MenuItem
import de.psdev.licensesdialog.LicensesDialog
import kotlinx.android.synthetic.main.activity_settings.*
import rawcomposition.bibletools.info.BuildConfig
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.ui.base.BaseThemedActivity


class SettingsActivity : BaseThemedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.container, SettingsFragment())
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }

    class SettingsFragment : PreferenceFragment(), Preference.OnPreferenceChangeListener {


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preferences)

            initialize()
        }

        private fun initialize() {

            preferenceManager.findPreference(getString(R.string.pref_key_version)).summary = BuildConfig.VERSION_NAME


            preferenceManager.findPreference(getString(R.string.pref_feedback))
                    .setOnPreferenceClickListener { _ ->

                        if (activity is BaseThemedActivity) {
                            (activity as BaseThemedActivity).sendFeedback()
                        }

                        true
                    }
            preferenceManager.findPreference("pref_donate")
                    .setOnPreferenceClickListener { _ ->

                        if (activity is BaseThemedActivity) {
                            (activity as BaseThemedActivity).donateClicked()
                        }

                        true
                    }

            findPreference(getString(R.string.pref_theme_type)).onPreferenceChangeListener = this

            findPreference(getString(R.string.pref_font_weight)).onPreferenceChangeListener = this

            findPreference("pref_open_source")
                    .setOnPreferenceClickListener { _ ->
                        LicensesDialog.Builder(activity)
                                .setNotices(R.raw.notices)
                                .setIncludeOwnLicense(true)
                                .build().show()

                        true
                    }

        }

        override fun onPreferenceChange(preference: Preference, o: Any): Boolean {

            if (preference.key == getString(R.string.pref_theme_type)) {

                activity.recreate()

                return true
            } else if (preference.key == getString(R.string.pref_font_weight)) {

                return true
            }

            return false
        }
    }
}