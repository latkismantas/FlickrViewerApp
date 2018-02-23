package uk.ac.kent.ml555.flickrviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;



public class NetworkMgr {
    private Context context;
    private static NetworkMgr instance;
    public RequestQueue requestQueue;
    ArrayList<ImageInfo> imageList;


    public ImageLoader imageLoader;

    private ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache(){
        private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

        @Override
        public Bitmap getBitmap(String url){
            return cache.get(url);

        }
        @Override
        public void putBitmap(String url, Bitmap bitmap){
            cache.put(url, bitmap);
        }
    };





    public NetworkMgr(Context context) {
        this.context = context;

        requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        imageList = new ArrayList<ImageInfo>();

        imageLoader = new ImageLoader(requestQueue, imageCache);
    }

    public static NetworkMgr getInstance(Context context){
        if(instance == null){
            instance = new NetworkMgr(context.getApplicationContext());
        }
        return instance;
    }



}
