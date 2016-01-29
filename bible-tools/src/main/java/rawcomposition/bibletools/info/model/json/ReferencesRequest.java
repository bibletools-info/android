package rawcomposition.bibletools.info.model.json;

import java.io.Serializable;

/**
 * Created by tinashe on 2016/01/29.
 */
public class ReferencesRequest implements Serializable {

    private int book;

    private int chapter;

    private int verse;

    public ReferencesRequest() {
    }

    public ReferencesRequest(int book, int chapter, int verse) {
        this.book = book;
        this.chapter = chapter;
        this.verse = verse;
    }

    public int getBook() {
        return book;
    }

    public void setBook(int book) {
        this.book = book;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int getVerse() {
        return verse;
    }

    public void setVerse(int verse) {
        this.verse = verse;
    }
}
