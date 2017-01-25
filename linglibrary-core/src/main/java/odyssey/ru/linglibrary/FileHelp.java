package odyssey.ru.linglibrary;

import java.io.File;

/**
 * Created by Dmitry on 29.10.2016.
 */
public class FileHelp {

    public static String getPath(String str) {
        return LingLibrary.getApp().getExternalFilesDir("").getPath() + "/" + str;
    };

    public static String getPath() {
        String str = LingLibrary.getApp().getExternalFilesDir("").getPath() + "/";
        return str ;
    };

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
}
