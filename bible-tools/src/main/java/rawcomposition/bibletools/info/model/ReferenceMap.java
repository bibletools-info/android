package rawcomposition.bibletools.info.model;

import java.io.Serializable;

/**
 * Created by tinashe on 2015/06/07.
 */
public class ReferenceMap implements Serializable {

    private String mapTitle;

    private String mapUrl;

    public String getMapTitle() {
        return mapTitle;
    }

    public void setMapTitle(String mapTitle) {
        this.mapTitle = mapTitle;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }
}
