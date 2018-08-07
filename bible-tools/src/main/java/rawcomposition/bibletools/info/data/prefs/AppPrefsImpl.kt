package rawcomposition.bibletools.info.data.prefs

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class AppPrefsImpl constructor(context: Context) : AppPrefs {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getLastRef(): String? {
        return prefs.getString(KEY_LAST_REF, null)
    }

    override fun setLastRef(ref: String) {
        prefs.edit()
                .putString(KEY_LAST_REF, ref)
                .apply()
    }

    override fun isNightMode(): Boolean {
        return prefs.getBoolean(KEY_NIGHT_MODE, false)
    }

    override fun setNightMode(enable: Boolean) {
        prefs.edit()
                .putBoolean(KEY_NIGHT_MODE, enable)
                .apply()
    }

    companion object {
        private const val KEY_LAST_REF = "KEY_LAST_REF"
        private const val KEY_NIGHT_MODE = "KEY_NIGHT_MODE"
    }
}