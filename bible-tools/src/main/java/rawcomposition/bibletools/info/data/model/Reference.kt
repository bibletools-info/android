package rawcomposition.bibletools.info.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "references")
data class Reference(@PrimaryKey @SerializedName("short_ref") var shortRef: String) {

    @SerializedName("main_resources")
    var resources = arrayListOf<Resource>()

    @SerializedName("text_ref")
    var textRef: String? = null

    var verse: String? = null

    @SerializedName("nav")
    var navigation: Navigation? = null

    var favorite: Boolean = false
}