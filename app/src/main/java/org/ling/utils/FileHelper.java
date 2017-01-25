package org.ling.utils;

import android.os.Environment;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import odyssey.ru.linguateacher.MainActivity;
import odyssey.ru.linguateacher.WebClient;

/**
 * Created by Dmitry on 29.06.2016.
 */
public class FileHelper {


    private static AsyncHttpClient mClient;

    public static String getPath() {
        return Environment.getExternalStorageDirectory() + "/Android/data/ru.odyssey.linguateacher/";
    }

    ;

    public static String getPathToImages(String word) {
        File dir = new File(getPath() + "images");
        dir.mkdirs();
        return dir.getPath() + "/" + word;
    }

    public static String getPathToWordMp3(String word, String lang) {
        File dir = new File(getPath() + "mp3/" + lang);
        dir.mkdirs();
        return dir.getPath() + "/" + word + ".mp3";
    }


    public static void downloadFile(final String url, final String pathToSave, final IActionHandler handler) {
        AsyncHttpClient client = WebClient.client;

        if(mClient == null){
            client = WebClient.client;
        }else {
            client = mClient;
        }

        client.removeAllHeaders();

        client.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0");
        client.addHeader("Accept", "audio/webm,audio/ogg,audio/wav,audio/*;q=0.9,application/ogg;q=0.7,video/*;q=0.6,*/*;q=0.5");


        client.get(url, new FileAsyncHttpResponseHandler(MainActivity.activity) {


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                Log.d("FileHelper", "onFailure1 " + throwable.getMessage());
                if (handler != null) handler.onFailAction("", throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                Log.d("FileHelper", "onSuccess1 " + response.getAbsolutePath());
                File file = new File(pathToSave );
                try {
                    FileUtils.copyFile(response, file);
                    if (handler != null) if (handler != null) handler.onSuccessAction(file);
                } catch (IOException e) {
                    e.printStackTrace();
                    if (handler != null) if (handler != null) handler.onFailAction("", new Throwable(e));
                }

            }
    });
        client.removeAllHeaders();

    }

    public static void downloadMp3(String url, String word, String lang, AsyncHttpClient c, IActionHandler handler) {
        mClient = c;
        downloadFile(url, getPathToWordMp3(word, lang), handler);
    }

    public static void downloadImage(String url, String image, IActionHandler handler) {
        downloadFile(url, getPathToImages(image) + ".png", handler);
    }

}
