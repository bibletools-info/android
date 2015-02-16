package rawcomposition.bibletools.info.model.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tinashe on 2015/02/15.
 */
public class Navigation extends JSONModel {
    private static final long serialVersionUID = 8217534150926010564L;

    @SerializedName("prev")
    private String previous;

    @SerializedName("next")
    private String next;

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }
}
