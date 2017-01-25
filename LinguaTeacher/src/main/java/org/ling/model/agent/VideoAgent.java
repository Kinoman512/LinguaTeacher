package org.ling.model.agent;

import com.activeandroid.query.Select;

import org.ling.model.Video;

import java.util.List;

/**
 * Created by Dmitry on 03.11.2016.
 */
public class VideoAgent {

    public static Video create(Video vid) {
//        Video temp = find(vid.get());
//        if (temp == null) {
//            vid.save();
//            temp = vid;
//        }
        vid.save();
        return vid;
    }

    public static void delete(Video vid) {

        if (vid != null)
            vid.delete();
    }




    public static Video findById(long id) {
        return Video.load(Video.class, id);
    }

    public static List<Video> getAll() {
        return new Select().from(Video.class).execute();
    }


    public static void deleteAll() {
        List<Video> lsw = getAll();
        for (Video sw : lsw) {
            sw.delete();
        }
    }

    public static Video update(Video st) {
        st.save();
        return st;
    }


}
