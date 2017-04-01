package rawcomposition.bibletools.info.ui.callbacks;

import android.view.View;

import io.realm.Realm;
import rawcomposition.bibletools.info.model.ReferenceMap;

/**
 * Created by tinashe on 2015/02/18.
 */
public interface OnNavigationListener {

    void onPrevious(String text);

    void onNext(String text);

    void onVerseClick(String verse);

    void onScrollRequired(int position);

    void onMapSelected(View view, ReferenceMap map);

    Realm getRealm();
}
