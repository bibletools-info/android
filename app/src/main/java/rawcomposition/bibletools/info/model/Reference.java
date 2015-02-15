package rawcomposition.bibletools.info.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tinashe on 2015/02/15.
 */
public class Reference extends JSONModel {

    private static final long serialVersionUID = 6702518172174461265L;

    @SerializedName("id")
    private int id;

    @SerializedName("book")
    private String book;

    @SerializedName("chapter")
    private String chapter;

    @SerializedName("verse")
    private String verse;

    @SerializedName("title")
    private String title;

    @SerializedName("reference")
    private String reference;

    @SerializedName("content")
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getVerse() {
        return verse;
    }

    public void setVerse(String verse) {
        this.verse = verse;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
