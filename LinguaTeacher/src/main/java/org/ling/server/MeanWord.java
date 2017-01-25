package org.ling.server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitry on 07.07.2016.
 */
public class MeanWord {
    String source;
    String lang1;
    String lang2;

    String transcription;
    String gender;
    String classf;


    List<String> means = new ArrayList<>();





    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getClassf() {
        return classf;
    }

    public void setClassf(String classf) {
        this.classf = classf;
    }

    public List<String> getMeans() {
        return means;
    }

    public void setMeans(List<String> means) {
        this.means = means;
    }
}
