package org.ling.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ling.model.agent.PronounceAgent;

/**
 * Created by Dmitry on 30.06.2016.
 */
@Table(name = "SimpleWords"   )
public class SimpleWord  extends Model implements Serializable {



    @Column(name = "word")
    String word;
    @Column(name = "lang")
    String lang;
    @Column(name = "choosedPronounce")
    int choosedPronounce;

    @Column(name = "transcription")
    String trscr;






    List<Pronounce> pronounces = new ArrayList<>();

    public SimpleWord() {
        super();
    }

    public List<Pronounce> getInstPronounces(){
        return PronounceAgent.find(getId());
    }


    public  Pronounce getCurrentPronounce(){
        pronounces = PronounceAgent.find(getId());
        Collections.sort(pronounces, new Comparator<Pronounce>() {
            @Override
            public int compare(Pronounce o1, Pronounce o2) {
                return (int) (o2.getLikes() - o1.getLikes());
            }
        });

        if(choosedPronounce >=0 && pronounces.size() != 0){
            return pronounces.get(choosedPronounce);
        }
        return null;
    }

    public SimpleWord(String word, String lang ) {
        super();
        this.word = word;
        this.lang = lang;
    }


    public String getTrscr() {
        return trscr;
    }

    public void setTrscr(String trscr) {
        this.trscr = trscr;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getChoosedPronounce() {
        return choosedPronounce;
    }

    public void setChoosedPronounce(int choosedPronounce) {
        this.choosedPronounce = choosedPronounce;
    }

    public List<Pronounce> getPronounces() {
        return pronounces;
    }

    public void setPronounces(List<Pronounce> pronounces) {
        this.pronounces = pronounces;
    }
}
