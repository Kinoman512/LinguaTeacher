package odyssey.ru.testapp;

import android.app.Application;

import odyssey.ru.googlelibrary.GoogleDrive;
import odyssey.ru.linglibrary.LingLibrary;

/**
 * Created by Dmitry on 29.10.2016.
 */
public class TestApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        LingLibrary.initLib(this);
        GoogleDrive.initLib(this);
    }
}
