package rawcomposition.bibletools.info.data.model

import com.google.gson.annotations.SerializedName

data class Resource(val id: String) {

    var source: String? = null

    var author: String? = null

    var content: String? = null

    @SerializedName("class")
    var type: String? = null

    var reference: String? = null

    var logo: String? = null

    var isExpanded = false

    var mapUrl: String? = null

}
