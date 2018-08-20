package rawcomposition.bibletools.info.data.prefs

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.model.FontType

class AppPrefsImpl constructor(private val context: Context) : AppPrefs {

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

    override fun backHistoryEnabled(): Boolean {
        return prefs.getBoolean(context.getString(R.string.pref_key_back_history), true)
    }

    @FontType
    override fun getFontType(): String {
        return prefs.getString(context.getString(R.string.pref_font_weight), FontType.REGULAR)
    }

    companion object {
        private const val KEY_LAST_REF = "KEY_LAST_REF"
        private const val KEY_PREF_THEME = "pref_theme"
    }
}