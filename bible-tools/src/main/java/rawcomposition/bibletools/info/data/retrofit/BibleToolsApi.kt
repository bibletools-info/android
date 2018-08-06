package rawcomposition.bibletools.info.data.retrofit

import io.reactivex.Observable
import rawcomposition.bibletools.info.data.model.Reference
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface BibleToolsApi {

    @GET("/resources/json/{ref}")
    fun getReferences(@Path("ref") ref: String): Observable<Response<Reference>>
}
