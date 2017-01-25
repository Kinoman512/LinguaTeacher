package odyssey.ru.linglibrary;

import android.app.Application;

/**
 * Created by Dmitry on 29.10.2016.
 */
public class LingLibrary {

    static Application app;

    public static  Application getApp(){
        return app;
    }

    public static void initLib(Application ap){
        app = ap;
    }
}
