package rawcomposition.bibletools.info.data.prefs

interface AppPrefs {

    fun getLastRef(): String?

    fun setLastRef(ref: String)

    fun isNightMode(): Boolean

    fun backHistoryEnabled(): Boolean
}