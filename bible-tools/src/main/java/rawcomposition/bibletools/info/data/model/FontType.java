package rawcomposition.bibletools.info.data.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({FontType.LIGHT, FontType.REGULAR, FontType.MEDIUM, FontType.HEAVY})
@Retention(RetentionPolicy.RUNTIME)
public @interface FontType {

    String LIGHT = "lig";
    String REGULAR = "reg";
    String MEDIUM = "med";
    String HEAVY = "hev";

}
