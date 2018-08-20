package rawcomposition.bibletools.info.ui.home

import android.arch.lifecycle.MutableLiveData
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import io.reactivex.Observable
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.exceptions.ReferenceExeption
import rawcomposition.bibletools.info.data.model.*
import rawcomposition.bibletools.info.data.prefs.AppPrefs
import rawcomposition.bibletools.info.data.repository.ReferencesRepository
import rawcomposition.bibletools.info.ui.base.RxAwareViewModel
import rawcomposition.bibletools.info.ui.base.SingleLiveEvent
import rawcomposition.bibletools.info.utils.RxSchedulers
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val repository: ReferencesRepository,
                                        private val rxSchedulers: RxSchedulers,
                                        private val prefs: AppPrefs) : RxAwareViewModel() {

    var viewState = SingleLiveEvent<ViewStateData>()
    var reference = MutableLiveData<Reference>()

    private var history = MutableLiveData<ArrayList<String>>()

    init {
        viewState.value = ViewStateData(ViewState.LOADING)
        history.value = arrayListOf()
    }

    override fun subscribe() {

    }

    /**
     * Only called when view initializes
     * with deeplink query or null
     */
    fun initReference(query: String?) {
        query?.let {
            fetchReference(it)
        } ?: handleResponse(repository.getLastReference())
    }

    fun fetchReference(ref: String) {
        handleResponse(repository.getReference(ref))
    }

    private fun handleResponse(observable: Observable<Reference>) {
        val disposable = observable
                .subscribeOn(rxSchedulers.network)
                .observeOn(rxSchedulers.main)
                .doOnSubscribe { viewState.value = ViewStateData(ViewState.LOADING) }
                .subscribe({
                    viewState.value = if (it.resources?.isEmpty() == true || it.verse == null) ViewStateData(ViewState.ERROR) else ViewStateData(ViewState.SUCCESS)

                    reference.value = it

                    if (history.value?.contains(it.shortRef) == false) {
                        history.value?.add(it.shortRef)
                    }

                    Answers.getInstance().logContentView(ContentViewEvent()
                            .putContentName("Reference-View")
                            .putCustomAttribute("verse", it.textRef))

                }, { it ->
                    Timber.e(it)

                    if (it is ReferenceExeption && it.message != null) {
                        viewState.value = ViewStateData(ViewState.ERROR, it.message!!)
                    } else {
                        viewState.value = ViewStateData(ViewState.ERROR, R.string.error_default)
                    }
                })

        disposables.add(disposable)
    }

    fun submitHelpful(resource: Resource) {
        val ref = reference.value ?: return

        ref.resources?.find { it.id == resource.id }?.rating = Helpful.POSITIVE

        val disposable = repository.submitHelpful(resource.id, ref)
                .subscribeOn(rxSchedulers.network)
                .observeOn(rxSchedulers.main)
                .subscribe({}, { Timber.e(it) })

        disposables.add(disposable)

    }

    fun submitUnHelpful(resource: Resource) {

        val ref = reference.value ?: return

        ref.resources?.find { it.id == resource.id }?.rating = Helpful.NEGATIVE

        val disposable = repository.submitUnHelpful(resource.id, ref)
                .subscribeOn(rxSchedulers.network)
                .observeOn(rxSchedulers.main)
                .subscribe({}, { Timber.e(it) })

        disposables.add(disposable)
    }

    fun navigateBack(): Boolean {
        if (!prefs.backHistoryEnabled()) {
            return true
        }

        val list = history.value ?: return true

        if (list.isEmpty()) {
            return true
        }

        if (list.size == 1 && reference.value?.shortRef == list.first()) {
            history.value = arrayListOf()
            return true
        }

        history.value = ArrayList(list.dropLast(1))
        val ref = history.value?.last()
        ref?.let { fetchReference(it) }

        return false
    }
}