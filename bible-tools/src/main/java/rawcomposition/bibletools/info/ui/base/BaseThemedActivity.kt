package rawcomposition.bibletools.info.ui.base

import android.annotation.TargetApi
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.MenuItem
import android.view.View
import dagger.android.AndroidInjection
import rawcomposition.bibletools.info.data.prefs.AppPrefs
import javax.inject.Inject

abstract class BaseThemedActivity : AppCompatActivity() {

    @Inject
    lateinit var appPrefs: AppPrefs

    protected var darkTheme = false
        get() = appPrefs.isNightMode()

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
            darkTheme = true

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
}