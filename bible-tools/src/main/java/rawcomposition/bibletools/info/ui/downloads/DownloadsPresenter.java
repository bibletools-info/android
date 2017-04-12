package rawcomposition.bibletools.info.ui.downloads;

/**
 * Created by tinashe on 2017/04/01.
 */

public class DownloadsPresenter implements DownloadsContract.Presenter{

    private final DownloadsContract.View view;

    public DownloadsPresenter(DownloadsContract.View view) {
        this.view = view;
    }

    @Override
    public void startPresenting() {
        view.setUpTabs();
    }

    @Override
    public void clearSubscriptions() {

    }

    @Override
    public void resubscribeIfNeeded() {

    }
}
