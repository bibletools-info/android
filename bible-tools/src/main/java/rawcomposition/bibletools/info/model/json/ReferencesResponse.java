package rawcomposition.bibletools.info.model.json;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tinashe on 2015/02/15.
 */
public class ReferencesResponse extends JSONModel {

    private static final long serialVersionUID = 915795466438092157L;

    @SerializedName("resources")
    private List<Reference> resources;

    public List<Reference> getResources() {
        List<Reference> temp = new ArrayList<>();

        for (Reference reference : resources) {
            if (!TextUtils.isEmpty(reference.getTitle())) {
                temp.add(reference);
            }
        }
        return temp;
    }

    public void setResources(List<Reference> resources) {
        this.resources = resources;
    }

}
