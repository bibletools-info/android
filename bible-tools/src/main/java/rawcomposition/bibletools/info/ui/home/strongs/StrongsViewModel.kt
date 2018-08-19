package rawcomposition.bibletools.info.ui.home.strongs

import android.arch.lifecycle.MutableLiveData
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.exceptions.ReferenceExeption
import rawcomposition.bibletools.info.data.model.StrongsResponse
import rawcomposition.bibletools.info.data.model.ViewState
import rawcomposition.bibletools.info.data.model.ViewStateData
import rawcomposition.bibletools.info.data.model.Word
import rawcomposition.bibletools.info.data.repository.ReferencesRepository
import rawcomposition.bibletools.info.ui.base.RxAwareViewModel
import rawcomposition.bibletools.info.ui.base.SingleLiveEvent
import rawcomposition.bibletools.info.utils.RxSchedulers
import timber.log.Timber
import javax.inject.Inject

class StrongsViewModel @Inject constructor(private val rxSchedulers: RxSchedulers,
                                           private val repository: ReferencesRepository) : RxAwareViewModel() {

    var viewState = SingleLiveEvent<ViewStateData>()
    var strongs = MutableLiveData<StrongsResponse>()

    init {
        viewState.value = ViewStateData(ViewState.LOADING)
    }

    override fun subscribe() {

    }

    fun fetchStrongs(word: Word) {
        val disposable = repository.getStrongs(word.id)
                .subscribeOn(rxSchedulers.network)
                .observeOn(rxSchedulers.main)
                .doOnSubscribe { viewState.value = ViewStateData(ViewState.LOADING) }
                .subscribe({
                    viewState.value = ViewStateData(ViewState.SUCCESS)
                    strongs.value = it
                }, {
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