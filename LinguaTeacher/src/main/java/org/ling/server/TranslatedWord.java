package org.ling.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitry on 28.06.2016.
 */
public class TranslatedWord {

    String source;
    String lang;
    String natv;

    String gender;
    String classf;


    List<TranslatedWord> translation = new ArrayList<>();


    public TranslatedWord(String source) {
        this.source = source;
    }

    public String getTranslate(){
        String res = "";
        for(int i = 0;i < translation.size();i++ ){
            if(!translation.get(i).getSource().equals(source) ){
                res = translation.get(i).getSource();
                return res;
            }
        }
        return "Nothing";
    }

    void addTraslate( TranslatedWord sw){
        translation.add(sw);
    }

    public List<TranslatedWord> getTranslation() {
        return translation;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getNatv() {
        return natv;
    }

    public void setNatv(String natv) {
        this.natv = natv;
    }
}
