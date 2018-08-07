package rawcomposition.bibletools.info.data.db

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rawcomposition.bibletools.info.data.model.Navigation
import rawcomposition.bibletools.info.data.model.Resource

class DataTypeConverters {

    private val lock = Any()

    private var gson: Gson? = null

    private fun getGson(): Gson {
        synchronized(lock) {
            if (gson == null) {
                gson = Gson()
            }

            return gson as Gson
        }
    }

    @TypeConverter
    fun resourcesString(resources: ArrayList<Resource>): String {
        return getGson().toJson(resources)
    }

    @TypeConverter
    fun stringResources(jsonString: String): ArrayList<Resource> {

        val type = object : TypeToken<ArrayList<Resource>>() {

        }.type
        return getGson().fromJson(jsonString, type)
    }

    @TypeConverter
    fun navigationString(navigation: Navigation): String {
        return getGson().toJson(navigation)
    }

    @TypeConverter
    fun stringNavigation(jsonString: String): Navigation {
        return getGson().fromJson(jsonString, Navigation::class.java)
    }

}