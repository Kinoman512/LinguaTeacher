package odyssey.ru.linguateacher;

import android.app.Application;
import android.text.format.DateUtils;

import com.activeandroid.ActiveAndroid;
import com.plumillonforge.android.chipview.Chip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.ling.model.Pronounce;
import org.ling.model.SimpleWord;
import org.ling.model.Stage;
import org.ling.model.agent.PronounceAgent;
import org.ling.model.agent.SetWordsService;
import org.ling.model.agent.SimpleWordAgent;
import org.ling.model.agent.StageAgent;
import org.ling.srt.InvalidTimestampFormatException;
import org.ling.srt.Subtitle;
import org.ling.srt.SubtitleFile;
import org.ling.utils.FileHelper;

/**
 * Created by Dmitry on 30.06.2016.
 */


public class MyApplication extends Application {

    String TAG_INIT = "MyInitTag";


    @Override
    public void onCreate() {
        super.onCreate();
        String dbPath = FileHelper.getPath();
        ActiveAndroid.initialize(this);
        Setting.init(this);

        

        boolean isFirstRun = Setting.getBool(TAG_INIT);

        if (!isFirstRun) {
            //init bd
            initBD();

        }

        List<SimpleWord> lsw = SimpleWordAgent.getAll();
        List<Pronounce> lprs = PronounceAgent.getAll();


        String srt1 = FileHelper.getPath() + "GOT.RUS.srt";
        String srt2 = FileHelper.getPath() + "GOT.ENG.srt";
        List<String> listWords = new ArrayList<>();
        String str = "";
        SubtitleFile exampleFile = null;
        try {
            exampleFile = new SubtitleFile(new File(srt2));
            List<Subtitle> list = exampleFile.getSubtitles();

            int y = 0;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidTimestampFormatException e) {
            e.printStackTrace();
        }

//        Subtitle sb = exampleFile.getSubtitleByTime(8000);
//        int x = 0;

        String str2 = "(Man): Man \" Man \"That's @ () it.\n" +
                "Get it right to the top.!#";
        try {
            exampleFile = new SubtitleFile(new File(srt2));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidTimestampFormatException e) {
            e.printStackTrace();
        }
        Subtitle sb = exampleFile.getSubtitle(116);
        if (sb == null) return;
        String subString = "";
        subString = sb.getFullLines();


        List<Chip> mTagList1 = new ArrayList<Chip>();
        final List<Chip> mTagListDefault = new ArrayList<Chip>();

        List<String> lstrs = sb.getFullWords();
        final StringBuilder sb3 = new StringBuilder();
        for (String e : lstrs) {
            sb3.append(e + "\n");
            mTagList1.add(new WordTag(e));
        }
        long seed = System.nanoTime();
        mTagListDefault.addAll(mTagList1);
        Collections.shuffle(mTagList1, new Random(seed));

        String finalSubString = "";
        String tempSubString1 = subString.toLowerCase();

        String tempSubString =      tempSubString1 .replaceAll("\n", " ");

        for (Chip e : mTagListDefault) {
            WordTag tag = (WordTag) e;
            String word = tag.getText();
            int start = tempSubString.indexOf(tag.getText());
            String partString = tempSubString.substring(0, start + tag.getText().length() + 1);
            finalSubString += partString;
            if (tempSubString.length() <= tag.getText().length()) {
                tempSubString = tempSubString.substring(tempSubString.length());
            } else {
                tempSubString = tempSubString.substring(partString.length());
            }

        }


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

        String lang = Locale.getDefault().getLanguage();

        SetWordsService sws = new SetWordsService();
        sws.createSetWords("My Vocabulary", "ru", "en");
        Setting.setBool(TAG_INIT, true);

    }
}
