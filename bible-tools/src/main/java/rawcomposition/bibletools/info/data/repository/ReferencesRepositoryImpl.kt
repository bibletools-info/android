package rawcomposition.bibletools.info.data.repository

import android.content.Context
import android.net.ConnectivityManager
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import io.reactivex.Completable
import io.reactivex.Observable
import rawcomposition.bibletools.info.BuildConfig
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.db.BibleToolsDb
import rawcomposition.bibletools.info.data.exceptions.ReferenceExeption
import rawcomposition.bibletools.info.data.model.Reference
import rawcomposition.bibletools.info.data.model.Reference.Companion.MAP
import rawcomposition.bibletools.info.data.model.StrongsResponse
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
            api.getReference(query)
                    .flatMap { it ->
                        if (it.isSuccessful) {

                            val reference = it.body()

                            if (reference?.resources != null && reference.resources?.isNotEmpty() == true && reference.verse != null) {

                                reference.sidebarResources?.map { item ->
                                    if (item.type == MAP) {
                                        val content = item.content ?: return@map
                                        val first = content.indexOfFirst { it == '/' }
                                        val last = content.indexOfFirst { it == '>' }

                                        if (first < 0 || last < 0) {
                                            return@map
                                        }

                                        item.mapUrl = "${BuildConfig.URL_BASE}${content.substring(first, last - 1)}"
                                    }
                                }

                                rxSchedulers.database.run {
                                    database.referencesDao().insert(reference)
                                }

                                prefs.setLastRef(reference.shortRef)
                                Observable.just(reference)
                            } else {

                                Answers.getInstance().logCustom(CustomEvent("Load-Reference-Fail-Event")
                                        .putCustomAttribute("query", query))

                                Observable.error(ReferenceExeption(String.format(context.getString(R.string.api_default_error), query)))
                            }
                        } else {
                            Answers.getInstance().logCustom(CustomEvent("Load-Reference-Fail-Event")
                                    .putCustomAttribute("query", query))

                            Observable.error(ReferenceExeption(String.format(context.getString(R.string.api_default_error), query)))
                        }
                    }
        } else {
            Observable.error(ReferenceExeption(context.getString(R.string.error_no_connection)))
        }
    }

    private fun hasConnection(context: Context): Boolean {
        val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnected
    }

    override fun submitHelpful(resourceId: String, reference: Reference): Completable {
        return api.submitHelpful(resourceId)
                .doOnSubscribe {
                    rxSchedulers.database.run {
                        database.referencesDao().update(reference)
                    }
                }
    }

    override fun submitUnHelpful(resourceId: String, reference: Reference): Completable {
        return api.submitUnHelpful(resourceId)
                .doOnSubscribe {
                    rxSchedulers.database.run {
                        database.referencesDao().update(reference)
                    }
                }
    }

    override fun getStrongs(wordId: String): Observable<StrongsResponse> {
        return if (hasConnection(context)) {
            api.getStrongs(wordId)
                    .flatMap {
                        if (it.isSuccessful) {

                            val response = it.body()
                            if (response?.pronunciation != null && response.originalWord != null) {
                                Observable.just(response)
                            } else {

                                Answers.getInstance().logCustom(CustomEvent("Load-Strongs-Fail-Event")
                                        .putCustomAttribute("wordId", wordId))

                                Observable.error(RuntimeException(""))
                            }

                        } else {

                            Answers.getInstance().logCustom(CustomEvent("Load-Strongs-Fail-Event")
                                    .putCustomAttribute("wordId", wordId))

                            Observable.error(RuntimeException(""))
                        }
                    }
        } else {
            Observable.error(ReferenceExeption(context.getString(R.string.error_no_connection)))
        }
    }

    companion object {
        private const val DEFAULT_START = "Matthew 1:1"
        private val dummy = Reference("dummy_id")
    }
}