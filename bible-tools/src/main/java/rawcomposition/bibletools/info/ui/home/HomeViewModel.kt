package rawcomposition.bibletools.info.ui.home

import android.arch.lifecycle.MutableLiveData
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

    init {
        fetchReference("Rev_1.1")
    }

    override fun subscribe() {

    }

    private fun fetchReference(ref: String) {
        val disposable = repository.getReference(ref)
                .subscribeOn(rxSchedulers.network)
                .observeOn(rxSchedulers.main)
                .doOnSubscribe { viewState.value = ViewStateData(ViewState.LOADING) }
                .subscribe({
                    viewState.value = ViewStateData(ViewState.SUCCESS)
                    reference.value = it
                }, {
                    Timber.e(it)
                    viewState.value = ViewStateData(ViewState.ERROR)
                })

        disposables.add(disposable)
    }
}