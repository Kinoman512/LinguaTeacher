package org.ling.model.agent;

import com.activeandroid.query.Select;

import java.util.List;

import org.ling.model.Stage;

/**
 * Created by Dmitry on 01.07.2016.
 */
public class StageAgent {
    public static Stage create(Stage st) {
        Stage temp = find(st.getWeight());
        if (temp == null) {
            st.save();
            temp = st;
        }
        return temp;
    }

    public static void delete(Long w) {
        Stage st = new Select().from(Stage.class).where("weight = ?", w).executeSingle();
        if (st != null)
            st.delete();
    }


    public static Stage find(long w) {
        return new Select().from(Stage.class).where("weight = ?", w).executeSingle();
    }

    public static Stage findById(long id) {
        return Stage.load(Stage.class, id);
    }

    public static List<Stage> getAll() {
        return new Select().from(Stage.class).execute();
    }


    public static void deleteAll() {
        List<Stage> lsw = getAll();
        for (Stage sw : lsw) {
            sw.delete();
        }
    }

    public static Stage update(Stage st) {
        st.save();
        return st;
    }

    public static Stage getCloseStageWeight(long weight) {
        List<Stage> lss = getAll();
        Stage stage = null;
        Stage stageTempMin = null;
        Stage stageTempMax = null;
        long min = Long.MAX_VALUE - 1;
        long max = Long.MIN_VALUE + 1;
        for (Stage e : lss) {
            if (min > e.getWeight() && weight < e.getWeight()) {
                min = e.getWeight();
                stageTempMin = e;
            }
            if (max < e.getWeight() && weight >= e.getWeight()) {
                max = e.getWeight();
                stageTempMax = e;
            }
        }
        long deltaMax = Math.abs(max - weight);
        long deltaMin = Math.abs(min - weight);
        if (deltaMax < deltaMin) {
            stage = stageTempMax;
        } else {
            stage = stageTempMin;
        }

        return stage;
    }


    public static Stage getCloseStageWeight(long weight, boolean directUp) {
        List<Stage> lss = getAll();
        Stage stage = null;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        for (Stage e : lss) {
            if (directUp) {
                if (min > e.getWeight() && weight < e.getWeight()) {
                    stage = e;
                    min = e.getWeight();
                }
            } else {
                if (max < e.getWeight() && weight > e.getWeight()) {
                    stage = e;
                    max = e.getWeight();
                }
            }
        }
        return stage;
    }

    public static long findMax() {
        Stage ss = new Select().from(Stage.class).orderBy("weight DESC").executeSingle();
        return ss.getWeight();
    }

    public static long findMin() {
        Stage ss = new Select().from(Stage.class).orderBy("weight ASC").executeSingle();
        return ss.getWeight();
    }
}
