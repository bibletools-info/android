package rawcomposition.bibletools.info.data.retrofit

import io.reactivex.Completable
import io.reactivex.Observable
import rawcomposition.bibletools.info.data.model.Reference
import rawcomposition.bibletools.info.data.model.StrongsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface BibleToolsApi {

    @GET("/api/v1.0/verse/{ref}")
    fun getReference(@Path("ref") ref: String): Observable<Response<Reference>>

    @POST("/api/v1.0/helpful/{resource_id}")
    fun submitHelpful(@Path("resource_id") resId: String): Completable

    @POST("/api/v1.0/unhelpful/{resource_id}")
    fun submitUnHelpful(@Path("resource_id") resId: String): Completable

    @GET("/api/v1.0/word/{word_id}")
    fun getStrongs(@Path("word_id") wordId: String): Observable<Response<StrongsResponse>>
}
