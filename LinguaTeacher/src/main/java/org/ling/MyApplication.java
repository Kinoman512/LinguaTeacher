package org.ling;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;

import org.ling.model.SetWords;
import org.ling.model.Stage;
import org.ling.model.Video;
import org.ling.model.agent.SetWordsService;
import org.ling.model.agent.StageAgent;
import org.ling.utils.Lang;
import org.videolan.vlc.MediaDatabase;
import org.videolan.vlc.gui.audio.AudioUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import odyssey.ru.googlelibrary.Firebase;
import odyssey.ru.googlelibrary.GoogleLibrary;
import odyssey.ru.linglibrary.FileHelp;
import odyssey.ru.linglibrary.LingLibrary;

/**
 * Created by Dmitry on 30.06.2016.
 */


public class MyApplication extends Application {

    String TAG_INIT = "MyInitTag";
    int forUpdate = 5;
    private static MyApplication instance;

    public  static SetWords currentSet;


    public static Context getAppContext()

    {
        return instance;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        instance = this;
        ActiveAndroid.initialize(this);

        LingLibrary.initLib(this);
        GoogleLibrary.initLib(this);
        Firebase.mobileAdsInit();





        Setting.init(this);
        //Setting.setBool("FirebaseStop", false);
        // Are we using advanced debugging - locale?
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String p = pref.getString("set_locale", "");
        if (p != null && !p.equals("")) {
            Locale locale;
            // workaround due to region code
            if (p.equals("zh-TW")) {
                locale = Locale.TRADITIONAL_CHINESE;
            } else if (p.startsWith("zh")) {
                locale = Locale.CHINA;
            } else if (p.equals("pt-BR")) {
                locale = new Locale("pt", "BR");
            } else if (p.equals("bn-IN") || p.startsWith("bn")) {
                locale = new Locale("bn", "IN");
            } else {
                /**
                 * Avoid a crash of
                 * java.lang.AssertionError: couldn't initialize LocaleData for locale
                 * if the user enters nonsensical region codes.
                 */
                if (p.contains("-"))
                    p = p.substring(0, p.indexOf('-'));
                locale = new Locale(p);
            }
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }


        // Initialize the database soon enough to avoid any race condition and crash
        MediaDatabase.getInstance();
        // Prepare cache folder constants
        AudioUtil.prepareCacheFolder(this);


        boolean isFirstRun = Setting.getBool(TAG_INIT);
        int up = Setting.getInt("ForUpdate");
        if (!isFirstRun || forUpdate > up ) {
            //init bd
            Setting.setInt("ForUpdate", forUpdate++);
            initBD();

        }


        try {
            Set<String> libs = new HashSet<String>();
            String mapsFile = "/proc/" + android.os.Process.myPid() + "/maps";
            BufferedReader reader = new BufferedReader(new FileReader(mapsFile));
            String line;
            while ((line = reader.readLine()) != null) {
               // if (line.endsWith(".so")) {
                    int n = line.lastIndexOf(" ");
                    libs.add(line.substring(n + 1));
               // }
            }
            Log.d("Ldd", libs.size() + " libraries:");
            for (String lib : libs) {
                Log.d("Ldd", lib);
            }
        } catch (FileNotFoundException e) {
            // Do some error handling...
        } catch (IOException e) {
            // Do some error handling...
        }


//        loadInterstitial();
//        showInterstitial();


//        GoogleLibrary.initLib(this);
//        Firebase.mobileAdsInit();


//        List<SimpleWord> lsw = SimpleWordAgent.getAll();
//        List<Pronounce> lprs = PronounceAgent.getAll();
//
//
//        String srt1 = FileHelper.getPath() + "GOT.RUS.srt";
//        String srt2 = FileHelper.getPath() + "GOT.ENG.srt";
//        List<String> listWords = new ArrayList<>();
//        String str = "";
//        SubtitleFile exampleFile = null;
//        try {
//            exampleFile = new SubtitleFile(new File(srt2));
//            List<Subtitle> list = exampleFile.getSubtitles();
//
//            int y = 0;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InvalidTimestampFormatException e) {
//            e.printStackTrace();
//        }

//        Subtitle sb = exampleFile.getSubtitleByTime(8000);
//        int x = 0;

//        String str2 = "(Man): Man \" Man \"That's @ () it.\n" +
//                "Get it right to the top.!#";
//        try {
//            exampleFile = new SubtitleFile(new File(srt2));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InvalidTimestampFormatException e) {
//            e.printStackTrace();
//        }
//        Subtitle sb = exampleFile.getSubtitle(116);
//        if (sb == null) return;
//        String subString = "";
//        subString = sb.getFullLines();


//        List<Chip> mTagList1 = new ArrayList<Chip>();
//        final List<Chip> mTagListDefault = new ArrayList<Chip>();
//
//        List<String> lstrs = sb.getFullWords();
//        final StringBuilder sb3 = new StringBuilder();
//        for (String e : lstrs) {
//            sb3.append(e + "\n");
//            mTagList1.add(new WordTag(e));
//        }
//        long seed = System.nanoTime();
//        mTagListDefault.addAll(mTagList1);
//        Collections.shuffle(mTagList1, new Random(seed));
//
//        String finalSubString = "";
//        String tempSubString1 = subString.toLowerCase();
//
//        String tempSubString =      tempSubString1 .replaceAll("\n", " ");
//
//        for (Chip e : mTagListDefault) {
//            WordTag tag = (WordTag) e;
//            String word = tag.getText();
//            int start = tempSubString.indexOf(tag.getText());
//            String partString = tempSubString.substring(0, start + tag.getText().length() + 1);
//            finalSubString += partString;
//            if (tempSubString.length() <= tag.getText().length()) {
//                tempSubString = tempSubString.substring(tempSubString.length());
//            } else {
//                tempSubString = tempSubString.substring(partString.length());
//            }
//
//        }


//        Cache cache = new Cache("123");
//        cache.put("123","qwerty");
//        String str = (String) cache.get("123");


//        final YandexServer ys = new YandexServer(this);
//        ys.translate("ru", "ja", "кот");
    }


    public void initBD() {
        //нужны стейджи и 1 сетворд

        long dateArr[] = {
                DateUtils.DAY_IN_MILLIS,
                DateUtils.DAY_IN_MILLIS,
                DateUtils.DAY_IN_MILLIS,
                DateUtils.DAY_IN_MILLIS,
                DateUtils.DAY_IN_MILLIS * 7,
                DateUtils.DAY_IN_MILLIS * 30,
                DateUtils.YEAR_IN_MILLIS
        };


        for (int i = 0; i < dateArr.length; i++) {
            Stage stage = new Stage();
            stage.setWeight(i + 1);
            stage.setDelta(dateArr[i]);
            StageAgent.create(stage);
        }
        SetWordsService sws = new SetWordsService();

        Locale natv = Locale.getDefault();
        Lang natvLang = null;


        String str2 = natv.getDisplayLanguage();
        String str3 = natv.getDisplayName();

        Lang.getDefault();
        for (Lang lang : Lang.getAllLocale()) {

            String str5 = natv.getDisplayName();
            String[] arr = str5.split(" ");
            String lang1 = arr[0];
            String lang2 = lang.getLocale().getDisplayName();

            if (lang1.equals( lang2) && !lang1.isEmpty()&& !lang2.isEmpty()) {
                natvLang = lang;
            }
        }

        if (natv.getDisplayLanguage().equals(Lang.stringToLocale("en").getDisplayLanguage()) || natvLang == null) {
            sws.createSetWords("My Vocabulary", "en", "es");
        } else {
            sws.createSetWords("My Vocabulary", natvLang.getCodeLingua(), "en");
        }


        Video vid = new Video();
        vid.setName("My Video");
        vid.save();

        Setting.setBool(TAG_INIT, true);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }



    public static int getDisplayHeight() {
        WindowManager wm = (WindowManager) getAppContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static void showInterstitial() {

        int x = Setting.getInt("Firebase");
        if ( x >= 1) {
            Firebase.showInterstitial();
            x=-1;
        }
        x++;
        Setting.setInt("Firebase",x);
    }

    public static void loadInterstitial() {
        Firebase.loadInterstitial();
    }
}
