package rawcomposition.bibletools.info.data.model

import com.google.gson.annotations.SerializedName

class Resource {

    var name: String? = null

    var author: String? = null

    var content: String? = null

    @SerializedName("filename")
    var fileName: String? = null

    var reference: String? = null

    var logo: String? = null

    var isExpanded = false

    override fun toString(): String {
        return "Resource(name=$name, author=$author, content=$content, logo=$logo)"
    }


}
