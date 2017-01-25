package org.ling.model.agent;

import com.activeandroid.query.Select;

import java.util.List;

import org.ling.model.CommonWord;
import org.ling.model.Pronounce;
import org.ling.model.SetWords;
import org.ling.model.SimpleWord;

/**
 * Created by Dmitry on 01.07.2016.
 */
public class CommonWordAgent {

    public static CommonWord create(CommonWord cw) {

        CommonWord temp = find(cw.getSource(),cw.getDest());
        SetWords sws = SetWordsAgent.findById(cw.getIdSet());
        if(sws != null){
            sws.save();
        }


        if(temp == null){
            cw.save();
            temp = cw;
        }
        return temp;
    }




    public static CommonWord find(long sourceSimpleWord,  long destSimpleWord) {
        return  new Select().from(CommonWord.class).where("source = ?" , sourceSimpleWord ).where("dest = ?", destSimpleWord).executeSingle();
    }

    public static List<CommonWord> findBySetWord(long set) {
        return  new Select().from(CommonWord.class).where("idSet = ?" , set ).execute();
    }

    public static CommonWord findById(long id){
        return  CommonWord.load(CommonWord.class, id);
    }

    public static List<CommonWord> getAll(){
        return new Select().from(CommonWord.class).execute();
    }


    public static void deleteAll(){
        List<CommonWord> lsw =  getAll();
        for(CommonWord sw : lsw){
            sw.delete();
        }
    }


}
