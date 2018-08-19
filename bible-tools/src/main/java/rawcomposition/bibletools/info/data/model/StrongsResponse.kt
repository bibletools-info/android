package rawcomposition.bibletools.info.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StrongsResponse(val pronunciation: String,

                           @SerializedName("original_word")
                           val originalWord: String,

                           val definition: String,

                           @SerializedName("connected_words")
                           val connectedWords: List<Word>,

                           val resources: List<StrongsResource>) : Serializable