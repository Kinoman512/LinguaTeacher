package org.ling.model.agent;

import com.activeandroid.query.Select;

import java.util.List;

import org.ling.model.CommonWord;
import org.ling.model.SetWords;
import org.ling.model.SimpleWord;

/**
 * Created by Dmitry on 01.07.2016.
 */
public class SetWordsAgent {


    public static SetWords create(SetWords sw) {
        SetWords temp = find(sw.getName());
        if(temp == null){
            sw.save();
            temp = sw;
        }
        return temp;
    }


    public static SetWords find(String name) {
        return  new Select().from(SetWords.class).where("name = ?" , name ).executeSingle();
    }
    public static List<SetWords> findByLang(String  lang) {
        return  new Select().from(SetWords.class).where("lang = ?" , lang ).execute();
    }

    public static SetWords findById(long id){
        return  SetWords.load(SetWords.class, id);
    }

    public static List<SetWords> getAll(){
        return new Select().from(SetWords.class).execute();
    }


    public static void deleteAll(){
        List<SetWords> lsw =  getAll();
        for(SetWords sw : lsw){
            sw.delete();
        }
    }


    public static void remove(SetWords sw) {
        List<CommonWord> lcw = CommonWordAgent.findBySetWord(sw.getId());

        for(CommonWord e : lcw){
            e.delete();
        }
        sw.delete();
    }
}
