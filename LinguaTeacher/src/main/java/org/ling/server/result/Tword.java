package org.ling.server.result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitry on 09.07.2016.
 */
public class Tword {

    String text;
    String classf = "";
    String tscr = "";
    String gender = "";

    public Tword(String text) {
        this.text = text;
    }

    public Tword(String text, List<String> means) {
        this.text = text;
        this.means = means;
    }

    List<String> means = new ArrayList<>();
    List<Tword> syns = new ArrayList<>();


    public String getMeansTxt(int min){

        String mean = "";
        if(means.size() != 0){
            for(int i = 0; i< means.size() && i< min;i++){
                mean += means.get(i) + ", ";
            }
            mean = mean.substring(0,mean.length() - 2);
        }
        return  mean;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public List<String> getMeans() {
        return means;
    }

    public void setMeans(List<String> means) {
        this.means = means;
    }

    public List<Tword> getSyns() {
        return syns;
    }

    public void setSyns(List<Tword> syns) {
        this.syns = syns;
    }
}
