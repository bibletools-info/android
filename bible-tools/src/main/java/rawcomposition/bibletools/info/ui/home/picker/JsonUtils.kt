package rawcomposition.bibletools.info.ui.home.picker

import android.content.res.Resources
import com.google.gson.GsonBuilder
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringWriter

object JsonUtils {

    /**
     * Open a json file from raw and construct as class using Gson.
     *
     * @param resources
     * @param resId
     * @return
     */
    fun getJson(resources: Resources, resId: Int): String {

        val resourceReader = resources.openRawResource(resId)
        val writer = StringWriter()

        try {
            val reader = BufferedReader(InputStreamReader(resourceReader, "UTF-8"))
            var line: String? = reader.readLine()
            while (line != null) {
                writer.write(line)
                line = reader.readLine()
            }
            return writer.toString()
        } catch (e: Exception) {
            Timber.e("Unhandled exception while using JSONResourceReader")
        } finally {
            try {
                resourceReader.close()
            } catch (e: Exception) {
                Timber.e(e, "Unhandled exception while using JSONResourceReader")
            }

        }
        return ""
    }

    /**
     * Build an object from the specified JSON resource using Gson.
     *
     * @param type The type of the object to build.
     * @return An object of type T, with member fields populated using Gson.
     */
    private fun <T> constructUsingGson(type: Class<T>, jsonString: String): T {
        val gson = GsonBuilder().create()
        return gson.fromJson(jsonString, type)
    }


}
