package rawcomposition.bibletools.info.api;

import rawcomposition.bibletools.info.model.json.ReferencesResponse;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;


/**
 * Created by tinashe on 2015/02/15.
 */
public interface BibleToolsApi {

    @GET("/resources/get/{book}/{chapter}/{verse}")
    Observable<ReferencesResponse> deliverReferences(
            @Path("book") int book,
            @Path("chapter") int chapter,
            @Path("verse") int verse
    );
}
