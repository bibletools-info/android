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
        val prefVal = prefs.getString(KEY_PREF_THEME, "zero")
        return when (prefVal) {
            "one" -> true
            else -> false
        }
    }

    companion object {
        private const val KEY_LAST_REF = "KEY_LAST_REF"
        private const val KEY_PREF_THEME = "pref_theme"
    }
}