package org.ling;

import android.text.format.DateUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import odyssey.ru.linglibrary.FileHelp;


/**
 * Created by Dmitry on 20.06.2016.
 */
public class Cache<T, K> implements Serializable {

    public class Data<M> implements Serializable {
        public M data;
    }


    long date;
    long delta = DateUtils.DAY_IN_MILLIS * 30;
    String file;
    String path;

    public Cache(String id) {
        file = "cache4" + id + ".cache";

        path = FileHelp.getPath() + file;


        long d = Setting.getLong(file);

        if (d == 0) {
            d = new Date().getTime();
            Setting.setLong(file, d);
        }
        date = d;

        String path = FileHelp.getPath() + file;
        File file = new File(path);
        File f2 = new File(FileHelp.getPath());
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (!f2.exists())
            file.mkdirs();

    }


    public void put(T key, K val) {
        long now = new Date().getTime();
        if (now - date > delta || val == null) {
            Setting.setLong(file, now);
            save(null);
            date = now;
        }
        LinkedHashMap<T, K> d = read().data;


        if (d == null) {
            d = new LinkedHashMap<T, K>();
        }

        d.put(key, val);
        Data data = new Data();
        data.data = d;
        save(data);

    }

    ;

    public K get(T key) {
        long now = new Date().getTime();
        if (now - date > delta) {
            Setting.setLong(file, now);
            save(null);
            date = now;
            return null;
        }


        LinkedHashMap<T, K> data = read().data;
        if (data == null) {
            return null;
        }


        if (data.keySet().contains(key)) {
            return data.get(key);

        }

        return null;
    }

    ;


    boolean save(Data<List<LinkedHashMap<T, K>>> data) {


        FileOutputStream fos = null;

        try {

            File f = new File(path);
            if(!f.exists()) f.createNewFile();
            fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);


            oos.writeObject(data);
            oos.flush();
            oos.close();
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    Data<LinkedHashMap<T, K>> read() {
        Data<LinkedHashMap<T, K>> data = new Data<>();
        FileInputStream fis = null;
        ObjectInputStream oin = null;

        try {
            fis = new FileInputStream(path);



            oin = new ObjectInputStream(fis);

            data = (Data<LinkedHashMap<T, K>>) oin.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (oin != null)
                    oin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (data == null) data = new Data<LinkedHashMap<T, K>>();
        return data;
    }
}
