package org.ling;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by Dmitry on 21.06.2016.
 */
public class CacheImage {
    private static LruCache<String, Bitmap> mMemoryCache;

    //
    private static void init() {
        if(mMemoryCache == null){
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;

            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount() / 1024;
                }
            };
        }


    }

    public static  void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        init();
        return mMemoryCache.get(key);
    }

}
