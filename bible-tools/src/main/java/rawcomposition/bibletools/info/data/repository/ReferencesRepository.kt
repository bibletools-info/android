package rawcomposition.bibletools.info.data.repository

import io.reactivex.Completable
import io.reactivex.Observable
import rawcomposition.bibletools.info.data.model.Reference
import rawcomposition.bibletools.info.data.model.StrongsResponse

interface ReferencesRepository {

    fun getLastReference(): Observable<Reference>

    fun getReference(ref: String): Observable<Reference>

    fun submitHelpful(resourceId: String, reference: Reference): Completable

    fun submitUnHelpful(resourceId: String, reference: Reference): Completable

    fun getStrongs(wordId: String): Observable<StrongsResponse>
}