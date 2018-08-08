package rawcomposition.bibletools.info.ui.home

import android.arch.lifecycle.MutableLiveData
import io.reactivex.Observable
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.exceptions.ReferenceExeption
import rawcomposition.bibletools.info.data.model.Reference
import rawcomposition.bibletools.info.data.model.ViewState
import rawcomposition.bibletools.info.data.model.ViewStateData
import rawcomposition.bibletools.info.data.repository.ReferencesRepository
import rawcomposition.bibletools.info.ui.base.RxAwareViewModel
import rawcomposition.bibletools.info.ui.base.SingleLiveEvent
import rawcomposition.bibletools.info.utils.RxSchedulers
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val repository: ReferencesRepository,
                                        private val rxSchedulers: RxSchedulers) : RxAwareViewModel() {

    var viewState = SingleLiveEvent<ViewStateData>()
    var reference = MutableLiveData<Reference>()

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
}