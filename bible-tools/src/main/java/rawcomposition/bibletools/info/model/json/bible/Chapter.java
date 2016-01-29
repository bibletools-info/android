package rawcomposition.bibletools.info.model.json.bible;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tinashe on 2016/01/28.
 */
public class Chapter implements Serializable {

    private static final long serialVersionUID = 4881800875338337371L;

    @SerializedName("number")
    private int number;

    @SerializedName("verses")
    private int verses;

    public int getVerses() {
        return verses;
    }

    public void setVerses(int verses) {
        this.verses = verses;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
