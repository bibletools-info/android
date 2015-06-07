package rawcomposition.bibletools.info.model.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tinashe on 2015/02/15.
 */
public class Reference extends JSONModel {

    private static final long serialVersionUID = 7095848971005609205L;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("text")
    private String text;

    @SerializedName("prev")
    private String previous;

    @SerializedName("next")
    private String next;

    @SerializedName("filename")
    private String fileName;

    private boolean isCollapsed = true;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }

    public void setCollapsed(boolean isCollapsed) {
        this.isCollapsed = isCollapsed;
    }
}
