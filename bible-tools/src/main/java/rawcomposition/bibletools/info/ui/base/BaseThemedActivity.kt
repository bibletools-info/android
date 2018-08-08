package rawcomposition.bibletools.info.ui.base

import android.annotation.TargetApi
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.MenuItem
import android.view.View
import dagger.android.AndroidInjection
import rawcomposition.bibletools.info.BuildConfig
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.prefs.AppPrefs
import javax.inject.Inject

abstract class BaseThemedActivity : AppCompatActivity() {

    @Inject
    lateinit var appPrefs: AppPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        delegate.setLocalNightMode(if (appPrefs.isNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        })

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                disableLightStatusBar()
            }*/

        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun disableLightStatusBar() {
        val view = window.decorView
        var flags = view.systemUiVisibility
        flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        view.systemUiVisibility = flags
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    fun sendFeedback() {
        val subject = "BibleTools (Android - ${BuildConfig.VERSION_NAME})"

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.app_email)))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(intent, getString(R.string.label_email)))
        }
    }

    fun donateClicked() {

    }
}