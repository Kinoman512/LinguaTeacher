package odyssey.ru.googlelibrary;

import android.app.Application;

/**
 * Created by Dmitry on 29.11.2016.
 */
public class GoogleLibrary {


    static Application app;

    public static  Application getApp(){
        return app;
    }

    public static void initLib(Application ap){
        app = ap;
    }
}
