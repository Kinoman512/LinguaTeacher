package org.ling.forvo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ling.model.Pronounce;

/**
 * Created by Dmitry on 22.06.2016.
 */
public class ForvoWord implements Serializable{

    String word;
    String lingua;

    int choosedPronounce = 0;

    List<Pronounce> pronounces = new ArrayList<>();

    public Pronounce getCurrentPronounce(){
        return  pronounces.get(choosedPronounce);
    }



    public ForvoWord(String word, String lingua) {
        this.word = word;
        this.lingua = lingua;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getLingua() {
        return lingua;
    }

    public void setLingua(String lingua) {
        this.lingua = lingua;
    }

    public void setChoosedPronounce(int choosedPronounce) {
        this.choosedPronounce = choosedPronounce;
    }

    public List<Pronounce> getPronounces() {
        return pronounces;
    }

    public int getChoosedPronounce() {
        return choosedPronounce;
    }

    public void setPronounces(List<Pronounce> pronounces) {
        this.pronounces = pronounces;
    }
}
