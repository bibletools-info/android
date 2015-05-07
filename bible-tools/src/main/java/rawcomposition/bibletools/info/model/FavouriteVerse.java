package rawcomposition.bibletools.info.model;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by tinashe on 2015/03/05.
 */
public class FavouriteVerse extends RealmObject {

    @PrimaryKey
    @Index
    private String verseCode;

    private String verseText;

    public String getVerseCode() {
        return verseCode;
    }

    public void setVerseCode(String verseCode) {
        this.verseCode = verseCode;
    }

    public String getVerseText() {
        return verseText;
    }

    public void setVerseText(String verseText) {
        this.verseText = verseText;
    }
}
