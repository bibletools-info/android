package rawcomposition.bibletools.info.data.model

import com.google.gson.annotations.SerializedName

data class Reference(var id: String) {

    @SerializedName("main_resources")
    var resources = arrayListOf<Resource>()

    @SerializedName("text_ref")
    var textRef: String? = null

    @SerializedName("short_ref")
    var shortRef: String? = null

    var verse: String? = null

    @SerializedName("nav")
    var navigation: Navigation? = null
}