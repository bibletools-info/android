package rawcomposition.bibletools.info.data.repository

import android.content.Context
import android.net.ConnectivityManager
import io.reactivex.Observable
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.db.BibleToolsDb
import rawcomposition.bibletools.info.data.model.Reference
import rawcomposition.bibletools.info.data.prefs.AppPrefs
import rawcomposition.bibletools.info.data.retrofit.BibleToolsApi
import rawcomposition.bibletools.info.utils.RxSchedulers
import java.util.concurrent.TimeUnit

class ReferencesRepositoryImpl constructor(private val api: BibleToolsApi,
                                           private val context: Context,
                                           private val database: BibleToolsDb,
                                           private val rxSchedulers: RxSchedulers,
                                           private val prefs: AppPrefs) : ReferencesRepository {

    override fun getLastReference(): Observable<Reference> {

        val lastRef = prefs.getLastRef()

        return if (lastRef == null) {
            getReference(DEFAULT_START)
        } else {
            database.referencesDao().findReference(lastRef)
                    .defaultIfEmpty(dummy)
                    .toObservable()
                    .flatMap {
                        if (it.shortRef == dummy.shortRef) {
                            getReference(DEFAULT_START)
                        } else {
                            Observable.just(it)
                        }
                    }
        }
    }

    override fun getReference(ref: String): Observable<Reference> {

        return database.referencesDao()
                .findReference(ref)
                .defaultIfEmpty(dummy)
                .toObservable()
                .subscribeOn(rxSchedulers.database)
                .flatMap {
                    if (it.shortRef == dummy.shortRef) {
                        fetchRemoteReference(ref).subscribeOn(rxSchedulers.network)
                    } else {
                        prefs.setLastRef(it.shortRef)

                        Observable.just(it).delay(100, TimeUnit.MILLISECONDS)
                    }
                }
    }

    private fun fetchRemoteReference(query: String): Observable<Reference> {

        return if (hasConnection(context)) {
            if (query.contains("_")) {
                api.getReference(query)
            } else {
                api.queryReference(query)
            }.flatMap {
                if (it.isSuccessful) {

                    val reference = it.body()

                    if (reference != null && reference.resources.isNotEmpty() && reference.verse != null) {

                        rxSchedulers.database.run {
                            database.referencesDao().insert(reference)
                        }

                        prefs.setLastRef(reference.shortRef)
                    }

                    Observable.just(reference)
                } else {
                    Observable.error(RuntimeException(""))
                }
            }
        } else {
            Observable.error(Exception(context.getString(R.string.error_no_connection)))
        }
    }

    private fun hasConnection(context: Context): Boolean {
        val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnected
    }

    companion object {
        private const val DEFAULT_START = "Matthew 1:1"
        private val dummy = Reference("dummy_id")
    }
}