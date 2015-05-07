package rawcomposition.bibletools.info.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.Set;

/**
 * Created by tinashe
 */
public class RequestHelper {

    public static final String TAG = RequestHelper.class.getName();
    private static RequestHelper ourInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private RemoteImageCache mImageCache;

    private RequestHelper(Context context) {
        mCtx = context;

        mRequestQueue = getRequestQueue();

        mImageCache = new RemoteImageCache();

        mImageLoader = new ImageLoader(mRequestQueue, mImageCache);

    }

    public static synchronized RequestHelper getInstance(Context context) {

        if (ourInstance == null) {
            ourInstance = new RequestHelper(context);
        }

        return ourInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {

        return mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public RemoteImageCache getImageCache() {
        return mImageCache;
    }


    public class RemoteImageCache implements ImageLoader.ImageCache {
        private final LruCache<String, Bitmap> cache = new LruCache<>(25);

        @Override
        public Bitmap getBitmap(String url) {
            return cache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            cache.put(url, bitmap);
        }

        public void findAndRemoveUrlFromCache(String url) {
            if (url == null) {
                return;
            }
            Set<String> keys = cache.snapshot().keySet();

            if (keys.size() == 0) {
                return;
            }

            for (String key : keys) {
                if (key.contains(url)) {
                    cache.remove(key);
                }
            }
        }
    }

}
