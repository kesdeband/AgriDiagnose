package com.agrinett.agridiagnose.rest;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleyRequestQueue {
    private static VolleyRequestQueue SingletonInstance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context context;

    private VolleyRequestQueue(Context ctx) {
        context = ctx;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized VolleyRequestQueue getInstance(Context context) {
        if (SingletonInstance == null) {
            SingletonInstance = new VolleyRequestQueue(context);
        }
        return SingletonInstance;
    }

    private synchronized RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());


            // Create custom request queue
            // Instantiate the cache
//            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
//            Cache cache = new NoCache();

            // Set up the network to use HttpURLConnection as the HTTP client.
//            Network network = new BasicNetwork(new HurlStack());
//            Network network = new OkHttpStack(new OkHttpClient());
//
            // Instantiate the RequestQueue with the cache and network.
//            requestQueue = new RequestQueue(cache, network);

            // Start the queue
//            requestQueue.start();
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public void cancelPendingRequests(String tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
