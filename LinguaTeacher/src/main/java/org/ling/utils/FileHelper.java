package org.ling.utils;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.ling.MainActivity;
import org.ling.WebClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cz.msebera.android.httpclient.Header;
import odyssey.ru.linglibrary.FileHelp;

/**
 * Created by Dmitry on 29.06.2016.
 */
public class FileHelper {


    private static AsyncHttpClient mClient;




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
                    copy(response, file);
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
        downloadFile(url, FileHelp.getPathToWordMp3(word, lang), handler);
    }

    public static void downloadImage(String url, String image, IActionHandler handler) {
        downloadFile(url, FileHelp.getPathToImages(image) + ".png", handler);
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

}
