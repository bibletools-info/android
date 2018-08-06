package rawcomposition.bibletools.info.data.repository

import io.reactivex.Observable
import rawcomposition.bibletools.info.data.model.Reference

interface ReferencesRepository {

    fun getReference(ref: String): Observable<Reference>
}