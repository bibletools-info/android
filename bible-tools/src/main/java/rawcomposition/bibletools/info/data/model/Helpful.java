package rawcomposition.bibletools.info.data.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        Helpful.NEGATIVE, Helpful.POSITIVE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface Helpful {

    int NEGATIVE = -1;

    int POSITIVE = 1;
}
