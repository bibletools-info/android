package rawcomposition.bibletools.info.data.model

import com.google.gson.annotations.SerializedName

class Chapter {

    @SerializedName("number")
    var number: Int = 0

    @SerializedName("verses")
    var verses: Int = 0
}
