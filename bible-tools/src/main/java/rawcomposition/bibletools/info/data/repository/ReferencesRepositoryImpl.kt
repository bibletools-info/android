package rawcomposition.bibletools.info.data.repository

import io.reactivex.Observable
import rawcomposition.bibletools.info.data.model.Reference
import rawcomposition.bibletools.info.data.retrofit.BibleToolsApi

class ReferencesRepositoryImpl constructor(private val api: BibleToolsApi) : ReferencesRepository {

    override fun getReference(ref: String): Observable<Reference> {
        return api.getReferences(ref)
                .flatMap {
                    if (it.isSuccessful) {

                        Observable.just(it.body())
                    } else {
                        Observable.error(RuntimeException(""))
                    }
                }
    }
}