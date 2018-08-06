package rawcomposition.bibletools.info.data.model

import com.google.gson.annotations.SerializedName

class Book {

    @SerializedName("title")
    var title: String? = null

    @SerializedName("abr")
    var abr: String? = null

    @SerializedName("chapters")
    var chapters: List<Chapter>? = null
}
