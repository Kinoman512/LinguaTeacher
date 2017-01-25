package org.ling.service;

import android.graphics.Bitmap;

import com.squareup.picasso.Picasso;

import org.ling.MainActivity;
import org.ling.utils.FileHelper;
import org.ling.utils.IActionHandler;
import org.ling.utils.Md5;

import java.io.File;
import java.io.IOException;

import odyssey.ru.linglibrary.FileHelp;

/**
 * Created by Dmitry on 03.07.2016.
 */
public class ImageService {

    public static void  getImage(String url, final IActionHandler handler){

        //возвращает картинку из  карты памяти, если нету, то загружает

        //для получения имени использовать md5
        //проверить наличие в памяти
        //если нет загрузть с инета и сохранить
        // нет инета  - ошибка

        String img = Md5.md5(url);
        final String path = FileHelp.getPathToImages(img + ".png");
        File f = new File(path);
        if(f.exists()){
            handleDo(path, handler);
            return;
        };

        FileHelper.downloadImage(url, img, new IActionHandler() {
            @Override
            public void onSuccessAction(Object rs) {
                handleDo(path, handler);

            }

            @Override
            public void onFailAction(String s, Throwable throwable) {
                handler.onFailAction(s,throwable);
            }
        });
    }


    public static void handleDo(final String path,final IActionHandler handler){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp = null;
                try {
                    bmp = Picasso.with(MainActivity.activity).load(new File(path)).get();
                    final Bitmap finalBmp = bmp;
                    MainActivity.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handler.onSuccessAction(finalBmp);
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
