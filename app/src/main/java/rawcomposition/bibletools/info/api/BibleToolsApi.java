package rawcomposition.bibletools.info.api;


import com.orhanobut.wasp.CallBack;
import com.orhanobut.wasp.http.GET;
import com.orhanobut.wasp.http.Path;

import rawcomposition.bibletools.info.model.References;


/**
 * Created by tinashe on 2015/02/15.
 */
public interface BibleToolsApi {

    @GET("/resources/get/{book}/{chapter}/{verse}")
    void deliverReferences(
            @Path("book") int book,
            @Path("chapter") int chapter,
            @Path("verse") int verse,
            CallBack<References> callBack
    );
}
