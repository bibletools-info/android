package rawcomposition.bibletools.info.ui.base

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class RxAwareViewModel : ViewModel() {

    val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        unSubscribe()
    }

    abstract fun subscribe()

    open fun unSubscribe() {
        disposables.clear()
    }
}