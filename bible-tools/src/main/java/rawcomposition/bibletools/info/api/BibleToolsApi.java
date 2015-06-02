package rawcomposition.bibletools.info.api;

import rawcomposition.bibletools.info.model.json.ReferencesResponse;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.Callback;


/**
 * Created by tinashe on 2015/02/15.
 */
public interface BibleToolsApi {

    @GET("/resources/get/{book}/{chapter}/{verse}")
    void deliverReferences(
            @Path("book") int book,
            @Path("chapter") int chapter,
            @Path("verse") int verse,
            Callback<ReferencesResponse> callBack
    );
}
