package org.ling.model.agent;

import android.graphics.Color;

import java.util.Random;

import org.ling.model.SetWords;

/**
 * Created by Dmitry on 03.07.2016.
 */
public class SetWordsService {

    public void createSetWords(String name, String natv, String lang){
        //создать сет и сохранить
        //добавить рандомный цвет

        SetWords sw = new SetWords(name,lang ,natv);
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(126) + 120 , rnd.nextInt(126) + 120, rnd.nextInt(126)  + 120);
        sw.setBackcolor(color);


        SetWordsAgent.create(sw);
    }


}
