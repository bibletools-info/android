package rawcomposition.bibletools.info.model.json.bible;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tinashe on 2016/01/28.
 */
public class Book implements Serializable {
    private static final long serialVersionUID = 457473970965929827L;

    @SerializedName("title")
    private String title;

    @SerializedName("abr")
    private String abr;

    @SerializedName("chapters")
    private List<Chapter> chapters;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbr() {
        return abr;
    }

    public void setAbr(String abr) {
        this.abr = abr;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }
}
