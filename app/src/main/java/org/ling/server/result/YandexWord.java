package org.ling.server.result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitry on 09.07.2016.
 */
public class YandexWord {

    String lang1;
    String lang2;

    String jword = "";
    String classf = "";
    String tscr = "";
    String gender = "";

    List<Tword> listtword= new ArrayList<>();

    public String getTranslate(){
        if(listtword.size() != 0){
            return listtword.get(0).getText();
        }
        return "";
    }

    public YandexWord( String jword , String lang1, String lang2 ) {
        this.lang1 = lang1;
        this.lang2 = lang2;
        this.jword = jword;
    }

    public YandexWord( String jword , String lang1, String lang2, List<Tword> listtword) {
        this.lang1 = lang1;
        this.lang2 = lang2;
        this.jword = jword;
        this.listtword = listtword;
    }

    public String getLang1() {
        return lang1;
    }

    public void setLang1(String lang1) {
        this.lang1 = lang1;
    }

    public String getLang2() {
        return lang2;
    }

    public void setLang2(String lang2) {
        this.lang2 = lang2;
    }

    public List<Tword> getListtword() {
        return listtword;
    }

    public void setListtword(List<Tword> listtword) {
        this.listtword = listtword;
    }

    public String getJword() {
        return jword;
    }

    public void setJword(String jword) {
        this.jword = jword;
    }

    public String getClassf() {
        return classf;
    }

    public void setClassf(String classf) {
        this.classf = classf;
    }

    public String getTscr() {
        return tscr;
    }

    public void setTscr(String tscr) {
        this.tscr = tscr;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
