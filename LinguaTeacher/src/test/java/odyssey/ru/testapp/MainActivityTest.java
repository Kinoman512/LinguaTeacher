package odyssey.ru.testapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.TextView;

import org.R;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ling.MainActivity;
import org.ling.MyApplication;
import org.ling.server.YandexServer;
import org.ling.server.result.YandexWord;
import org.ling.utils.IActionHandler;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.util.ActivityController;
import org.robolectric.util.ApplicationTestUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import odyssey.ru.linglibrary.LingLibrary;

import static java.lang.System.in;
import static java.lang.System.in;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=19,
        manifest="../LinguaTeacher/src/main/AndroidManifest.xml"
       )

public class MainActivityTest {

    private Context context;
    List<YandexWord> listYW = new ArrayList<>();
    @Before
    public void setUp() throws Exception {
        context = ShadowApplication.getInstance().getApplicationContext();
    }

    @Test
    public void test1() throws IOException, InterruptedException {

        String databaseName = "Application.bd";
        String dbPath = "C:/" + databaseName;
        AssetManager asset = context.getAssets();





//        Robolectric.buildService(MyService.class)
        MyApplication app = ApplicationTestUtil.buildApplication(MyApplication.class, context);
        app.onCreate();

        LingLibrary.initLib(app);


        YandexServer ys = new YandexServer(context);
        ys.SID = "9094d49f.58766fd6.2c00d616-3-0";

        ys.setServerActionListnerTanslate(new IActionHandler() {
            @Override
            public void onSuccessAction(Object rs){
                System.out.println("Success Action");
                listYW = (List<YandexWord>) rs;


            }

            @Override
            public void onFailAction(String s, Throwable throwable) {
                System.out.println("Fail ");
            }
        });

        ys.translate("ru","en", "Кот");
        //activity.
        System.out.println("\nMaster setup");

        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(1000);
                        //System.out.println("\nTHREADD");
                        Thread.sleep(1000);
                        //System.out.println("\nTHREADD");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        tr.start();

        while(true){
            Thread.sleep(1000);
            //System.out.println("\nMaster");
            Thread.sleep(1000);
            //System.out.println("\nMaster");
            Thread.sleep(1000);
        }
    }

}
