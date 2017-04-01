package rawcomposition.bibletools.info.rx;


import rawcomposition.bibletools.info.ui.base.ProgressView;
import rx.Observable;

/**
 * Created by tinashe on 2016/11/15.
 */

public interface RxProgressComposer {

    RxProgressComposer DEFAULT = new RxProgressComposer() {
        @Override
        public <T> Observable.Transformer<T, T> applyIndicator(ProgressView view) {
            return observable -> observable
                    .doOnSubscribe(view::showProgressIndicator)
                    .doOnTerminate(view::hideProgressIndicator);
        }

        @Override
        public <T> Observable.Transformer<T, T> applyIndicator(ProgressView view, String message) {
            return observable -> observable
                    .doOnSubscribe(() -> view.showProgressIndicator(message))
                    .doOnTerminate(view::hideProgressIndicator);
        }
    };

    <T> Observable.Transformer<T, T> applyIndicator(ProgressView view);

    <T> Observable.Transformer<T, T> applyIndicator(ProgressView view, String message);
}
