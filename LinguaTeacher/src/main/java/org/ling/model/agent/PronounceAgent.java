package org.ling.model.agent;

import com.activeandroid.query.Select;

import java.util.List;

import org.ling.model.Pronounce;
import org.ling.model.SimpleWord;

/**
 * Created by Dmitry on 01.07.2016.
 */
public class PronounceAgent {


    public static  Pronounce create(Pronounce pr, long simpleWord) {
        Pronounce temp = find(pr.getUrl());
        if(temp != null){
            return temp;
        }
        pr.setSimpleWord(simpleWord);
        pr.save();
        return pr;
    }


    public static Pronounce find(String url) {
        return  new Select().from(Pronounce.class).where("url = ?" , url ).executeSingle();
    }

    public static List<Pronounce> find(long idSimpleWord) {
        return new Select().from(Pronounce.class).where("simpleWord = ?", idSimpleWord ).execute();
    }

    public static Pronounce findById(long id){
        return  Pronounce.load(Pronounce.class, id);
    }

    public  static List<Pronounce> getAll(){
        return new Select().from(Pronounce.class).execute();
    }


    public static void deleteAll(){
        List<Pronounce> lsw =  getAll();
        for(Pronounce e : lsw){
            e.delete();
        }
    }

}
