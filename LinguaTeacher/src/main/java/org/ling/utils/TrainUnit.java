package org.ling.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.ling.model.CommonWord;

/**
 * Created by Dmitry on 16.07.2016.
 */
public class TrainUnit implements Serializable {

    CommonWord commonWord;
    Map<Integer,Long> map = new HashMap<>();

    int mistakes = 0;

    public int getAttempts(){
        return  map.size();
    }

    public void addTime(long deltaTime){
        map.put(map.size() + 1,deltaTime);
    }

    public long getMiddleTime(){
        long res = 0;

        for( Long e : map.values()){
            res+=e;
        }
        return  res/(map.size()+ 1);
    }

    public void incMistakes(){
        mistakes++;
    }

    public CommonWord getCommonWord() {
        return commonWord;
    }

    public void setCommonWord(CommonWord commonWord) {
        this.commonWord = commonWord;
    }

    public Map<Integer, Long> getMap() {
        return map;
    }

    public void setMap(Map<Integer, Long> map) {
        this.map = map;
    }

    public int getMistakes() {
        return mistakes;
    }

    public void setMistakes(int mistakes) {
        this.mistakes = mistakes;
    }
}
