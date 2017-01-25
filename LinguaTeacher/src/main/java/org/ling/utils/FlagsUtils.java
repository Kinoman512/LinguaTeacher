package org.ling.utils;

import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.ling.MainActivity;

/**
 * Created by Dmitry on 06.08.2016.
 */
public class FlagsUtils {

    public static List<Drawable>  getFlagsByCode(List<String> listLangs){
        List<Drawable> bmps  = new ArrayList<>();

        for(String e: listLangs){
            try {
                InputStream ims = MainActivity.activity.getAssets().open("img/flags/" + e + ".png");
//                InputStream ims = MainActivity.activity.getAssets().open("img/flags/ja.png");
                Drawable d = Drawable.createFromStream(ims, null);
                bmps.add(d);
            }
            catch(IOException ex) {
                InputStream ims = null;
                try {
                    ims = MainActivity.activity.getAssets().open("img/flags/_unknown.png");
                } catch (IOException e1) {
                    e1.printStackTrace();
                    bmps.add(null);

                }
                Drawable d = Drawable.createFromStream(ims, null);
                bmps.add(d);

            }


        }

        return  bmps;
    }
}
