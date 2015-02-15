package rawcomposition.bibletools.info.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tinashe on 2015/02/15.
 */
public class References extends JSONModel {

    private static final long serialVersionUID = 915795466438092157L;

    @SerializedName("resources")
    private List<Reference> resources;

    @SerializedName("nav")
    private Navigation navigation;

    public List<Reference> getResources() {
        return resources;
    }

    public void setResources(List<Reference> resources) {
        this.resources = resources;
    }

    public Navigation getNavigation() {
        return navigation;
    }

    public void setNavigation(Navigation navigation) {
        this.navigation = navigation;
    }
}
