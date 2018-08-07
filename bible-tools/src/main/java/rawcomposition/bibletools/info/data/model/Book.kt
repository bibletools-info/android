package rawcomposition.bibletools.info.data.model

import android.os.Parcel
import android.os.Parcelable
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.google.gson.annotations.SerializedName

data class Book(val title: String) : SearchSuggestion {
    override fun getBody(): String {
        return title
    }

    @SerializedName("abr")
    var abr: String? = null

    @SerializedName("chapters")
    var chapters: List<Chapter>? = null

    constructor(parcel: Parcel, title: String) : this(title) {
        abr = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel.readString())
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}
