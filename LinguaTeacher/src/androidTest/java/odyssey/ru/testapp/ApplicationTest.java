package odyssey.ru.testapp;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import org.ling.MyApplication;
import org.ling.server.YandexServer;
import org.ling.server.result.YandexWord;
import org.ling.utils.IActionHandler;

import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<MyApplication> {
    public ApplicationTest() {
        super(MyApplication.class);
    }




    private Context context;



}