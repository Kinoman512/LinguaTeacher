package odyssey.ru.testapp;

import android.app.Instrumentation;
import android.content.Context;
import android.test.ActivityUnitTestCase;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestRunner;
import android.test.RenamingDelegatingContext;
import android.view.View;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ling.MainActivity;
import org.ling.server.YandexServer;
import org.ling.server.result.YandexWord;
import org.ling.utils.IActionHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import odyssey.ru.linglibrary.Picker;

/**
 * Created by User on 11.01.2017.
 */

public class TestYandex  {

    private Context context;






    @Test
    public void test1() throws IOException {



        YandexServer ys = new YandexServer(context);

        ys.setServerActionListnerTanslate(new IActionHandler() {
            @Override
            public void onSuccessAction(Object rs){
                System.out.println("Success Action");
                 List<YandexWord> listYW = (List<YandexWord>) rs;


        }

            @Override
            public void onFailAction(String s, Throwable throwable) {
                System.out.println("Fail ");
            }
        });

    }










    @BeforeClass
    public  void setUpClass() {
        System.out.println("Master setup");

       // context = new RenamingDelegatingContext(InstrumentationRegistry.getTargetContext(), "test_");

//        cards.add("1a");
//        cards.add("2b");
//        cards.add("3c");
//        cards.add("4d");
//        cards.add("55");
//        cards.add("66");
//        cards.add("77");
//        cards.add("88");
//        cards.add("99");
//        cards.add("111");

    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println("Master tearDown");
    }



}
