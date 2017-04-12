package rawcomposition.bibletools.info.ui.downloads.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tinashe on 2017/04/12.
 */

@IntDef({
        CacheState.EMPTY,
        CacheState.PARTIAL,
        CacheState.COMPLETE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheState {

    int EMPTY = 1;
    int PARTIAL = 2;
    int COMPLETE = 3;
}
