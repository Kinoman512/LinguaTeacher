package org.ling.model.agent;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ling.model.CommonWord;
import org.ling.model.Pronounce;
import org.ling.model.SimpleWord;

/**
 * Created by Dmitry on 01.07.2016.
 */
public class SimpleWordAgent {

    public static SimpleWord create(SimpleWord sw, List<Pronounce> lps) {
        SimpleWord temp = find(sw.getWord(),sw.getLang() );


        if(temp == null){
            sw.save();
            temp = sw;
        }

        Collections.sort(lps, new Comparator<Pronounce>() {
            @Override
            public int compare(Pronounce o1, Pronounce o2) {
                return (int) (o2.getLikes() - o1.getLikes());
            }
        });

        temp.setChoosedPronounce(0);


        for(Pronounce e :lps){
            PronounceAgent.create(e,temp.getId());
        }
        if(lps.size()!= 0){
            sw.setChoosedPronounce(0);
        }
        return temp;
    }


    public static SimpleWord find(String word, String lang ) {
        return  new Select().from(SimpleWord.class)
                .where("word = ?", word)
                .where("lang = ?", lang).executeSingle();
    }

    public static SimpleWord findById(long id){
        return  SimpleWord.load(SimpleWord.class, id);
    }

    public static List<SimpleWord> getAll(){
        List<SimpleWord> lsw = new ArrayList<>();
        return new Select().from(SimpleWord.class).execute();
    }


    public static void deleteAll(){
        List<SimpleWord> lsw =  getAll();
        for(SimpleWord sw : lsw){
            sw.delete();
        }
    }

}
