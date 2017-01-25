package org.ling.model.agent;

import android.view.View;
import android.widget.Toast;

import org.ling.MainActivity;
import org.ling.forvo.ForvoHelper;
import org.ling.forvo.ForvoWord;
import org.ling.model.CommonWord;
import org.ling.model.Pronounce;
import org.ling.model.SimpleWord;
import org.ling.server.BingServer;
import org.ling.service.ImageService;
import org.ling.utils.IAction;
import org.ling.utils.IActionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitry on 30.06.2016.
 */
public class WordManager {

    int calls = 0;
    String src;
    String dest;
    String natv;
    String lang;
    String gender1;
    String trscr;
    String classf1 = "";
    long setWords;

    List<Pronounce> lprNatv = new ArrayList<>();
    List<Pronounce> lprLang = new ArrayList<>();
    String img = "";
    IAction act;
    IAction err;
    View loadingView;

    void forCanSave() {
        calls--;
        if (calls < 0) {
            //ошибка
            if (err != null)
                err.onSucces("");

        }
        if (calls == 0) {

            SimpleWord sw = new SimpleWord(src, lang);
            SimpleWord sw2 = new SimpleWord(dest, natv);


//            if(gender1 != null) sw.setGender(gender1);
//            if(gender2 != null) sw2.setGender(gender2);

            sw.setTrscr(trscr);
            sw = SimpleWordAgent.create(sw, lprNatv);
            sw2 = SimpleWordAgent.create(sw2, lprLang);

            CommonWord cw = new CommonWord(sw.getId(), sw2.getId(), setWords);
            cw.setImage(img);
            CommonWordAgent.create(cw);
            if (act != null)
                act.onSucces("");
        }
    }

    public void createCommonWord(String s, final String d, String trscr, final String natv, final String lang, long idSetWords, IAction act, IAction err) {

        //получить список всех звучаний в тч и робота
        //если нет произношений, все равно должен быть успех
        //если нет инета не добавлять
        // если нет звучаний добавлять
        this.act = act;
        this.err = err;
//        this.gender1 = g1;
//        this.gender2 = g2;
//        this.classf1 = cf1;
//        this.classf2 = cf2;


        src = s;
        this.trscr = trscr;
        dest = d;
        this.natv = natv;
        this.lang = lang;
        setWords = idSetWords;

        calls++;
        ForvoHelper.getForvoWord(s, lang, new IActionHandler() {
            @Override
            public void onSuccessAction(Object rs) {
                ForvoWord fw = (ForvoWord) rs;
                lprNatv.addAll(fw.getPronounces());
                forCanSave();

                calls++;
                ForvoHelper.getForvoWord(d, natv, new IActionHandler() {
                    @Override
                    public void onSuccessAction(Object rs) {
                        ForvoWord fw = (ForvoWord) rs;
                        lprLang.addAll(fw.getPronounces());
                        forCanSave();
                    }

                    @Override
                    public void onFailAction(String s, Throwable throwable) {
//                        calls = -100;
                        Toast.makeText(MainActivity.activity, throwable.getMessage(),
                                Toast.LENGTH_SHORT).show();

                        forCanSave();
                    }
                });
            }

            @Override
            public void onFailAction(String s, Throwable throwable) {
//                calls = -100;
                Toast.makeText(MainActivity.activity, throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
                forCanSave();
            }
        });


        calls++;
        ForvoHelper.getRobotVoice(s, lang, new IActionHandler() {
            @Override
            public void onSuccessAction(Object rs) {
                Pronounce pr = (Pronounce) rs;
                lprNatv.add(pr);


                ForvoHelper.getRobotVoice(d,natv , new IActionHandler() {
                    @Override
                    public void onSuccessAction(Object rs) {
                        Pronounce pr = (Pronounce) rs;
                        lprLang.add(pr);
                        forCanSave();
                    }

                    @Override
                    public void onFailAction(String s, Throwable throwable) {
                        forCanSave();
                    }
                });

            }

            @Override
            public void onFailAction(String s, Throwable throwable) {


                ForvoHelper.getRobotVoice(d, lang, new IActionHandler() {
                    @Override
                    public void onSuccessAction(Object rs) {
                        Pronounce pr = (Pronounce) rs;
                        lprLang.add(pr);
                        forCanSave();
                    }

                    @Override
                    public void onFailAction(String s, Throwable throwable) {
                        forCanSave();
                    }
                });

            }
        });


        //загрузить картинку, если нет то выбрать по умолчанию.
        //ножно создать imageService
        // если нет инета пропустить
        calls++;
        BingServer bs = new BingServer();
        bs.setServerActionListnerImage(new IActionHandler() {
            @Override
            public void onSuccessAction(Object rs) {

                List<String> imgs = (List<String>) rs;
                if (imgs.size() != 0) {
                    final String urlImg = imgs.get(0);
                    ImageService.getImage(urlImg, new IActionHandler() {

                        @Override
                        public void onSuccessAction(Object rs) {
                            img = urlImg;
                            forCanSave();
                        }

                        @Override
                        public void onFailAction(String s, Throwable throwable) {
                            forCanSave();
                        }
                    });
                }
            }

            @Override
            public void onFailAction(String s, Throwable throwable) {

            }
        });
        bs.searchImage(s);

    }


}
