package rawcomposition.bibletools.info.data.prefs

import rawcomposition.bibletools.info.data.model.FontType

interface AppPrefs {

    fun getLastRef(): String?

    fun setLastRef(ref: String)

    fun isNightMode(): Boolean

    fun backHistoryEnabled(): Boolean

    @FontType
    fun getFontType(): String
}