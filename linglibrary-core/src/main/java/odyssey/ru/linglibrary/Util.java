package odyssey.ru.linglibrary;

import android.widget.Toast;

/**
 * Created by Dmitry on 03.11.2016.
 */
public class Util {


    public static void showMassage(String str){
        Toast.makeText(LingLibrary.getApp(),str,Toast.LENGTH_LONG).show();
    }

    public static void showMassage(int  str){
        Toast.makeText(LingLibrary.getApp(), getStringById(str),Toast.LENGTH_LONG).show();
    }


    public static String getStringById(int id) {
        return LingLibrary.getApp().getString(id);
    }


}
