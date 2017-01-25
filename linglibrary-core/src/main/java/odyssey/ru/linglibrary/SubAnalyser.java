package odyssey.ru.linglibrary;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import odyssey.ru.linglibrary.subtitle.Subtitle;
import odyssey.ru.linglibrary.subtitle.SubtitleFile;
import odyssey.ru.linglibrary.subtitle.converter.SubtitleConvert;

/**
 * Created by Dmitry on 29.11.2016.
 */
public class SubAnalyser {


    private static final int REQUEST_CODE_RESOLUTION = 11;
    private static final int SKEEP_TO_BREAK = 20;
    private static final int SIZE_SMALL_SUB = 7;
    private static final String TAG = "SubAnalyserTag";


    String pathToNSUB = FileHelp.getPath() + "temp1.srt";
    String pathToFSUB = FileHelp.getPath() + "temp2.srt";


    public String getHtmlFromSubs(String pathToFSUB) throws Exception {

        //List<UnionSub> list = analiseSub(path1, path2);
        SubtitleFile subFile1 = new SubtitleFile(new File(pathToFSUB));

        String font_text1 = "<font color=\"#F48E60\">" ;
        String font_text2 = "<font color=\"#6077F4\">" ;
        String font_text_close ="</font>";

        int y = 0;
        StringBuilder sb = new StringBuilder();



        for (int i =0 ; i < subFile1.getSubtitles().size();i++) {


            List<String> ls1 = subFile1.getSubtitles().get(i).getLines();

            sb.append(y + " " + font_text1 );
            for (String e : ls1) {
                sb.append(e + "<br />");
            }

            sb.append(font_text_close);
            y++;
            sb.append("<br />");
        }
        return sb.toString();
    }


    public String getHtmlFromSubs(String path1, String path2) throws Exception {

        List<UnionSub> list = analiseSub(path1, path2);

        String font_text1 = "<font color=\"#F48E60\">" ;
        String font_text2 = "<font color=\"#6077F4\">" ;
        String font_text_close ="</font>";

        int y = 0;
        StringBuilder sb = new StringBuilder();
        for (UnionSub us : list) {

            List<String> ls1 = us.getListSubF();
            List<String> ls2 = us.getListSubN();
            sb.append(y + " " + font_text1 );
            for (String e : ls1) {
                sb.append(e + "<br />");
            }
            sb.append(font_text_close + " "  + font_text2);
            for (String e : ls2) {
                sb.append(e + "<br />");
            }
            sb.append(font_text_close);
            y++;
            sb.append("<br />");
        }
        return sb.toString();
    }

    public List<UnionSub> analiseSub(String pathToFSUB) throws Exception {


        List<UnionSub> unionSubs = new ArrayList<>();

        SubtitleFile subFile1 = new SubtitleFile(new File(pathToFSUB));

        int maxSub1 = subFile1.getSubtitles().size();

        int posSub = 0;

        while (true) {
            Subtitle sub = subFile1.getSubtitles().get(posSub);

            UnionSub us = new UnionSub();
            Fsub fsub = new Fsub();
            fsub.setSub(sub);

            us.addSubF(fsub);
            unionSubs.add(us);

            if (posSub >= maxSub1 - 1) {
                //Util.showMassage("Вы достигли конца субтиров");
                break;
            }
            posSub++;

        }
        return unionSubs;

    }



    public List<UnionSub> analiseSub(String pathToFSUB, String pathToNSUB) throws Exception {


        List<UnionSub> unionSubs = new ArrayList<>();
        //считать 2 саба и найти ближайшие сабы для иностр языка

//        String pathToFSUB = convert(path1, false);
//        String pathToNSUB = convert(path2, true);

        SubtitleFile subFile1 = new SubtitleFile(new File(pathToFSUB));
        SubtitleFile subFile2 = new SubtitleFile(new File(pathToNSUB));

        int maxSub1 = subFile1.getSubtitles().size();
        int maxSub2 = subFile2.getSubtitles().size();

        int posSub = 0;
        int posSearchNatv = 0;

//        int endNatvSub = 0;

        List<Subtitle> tempList = new ArrayList<>();

        int prefStart = 0;
        int prefEnd = 0;
        UnionSub prefUS = null;


        int posSub2 = 0;
        while (true) {
            Subtitle sub = subFile1.getSubtitles().get(posSub);
            int start = sub.getStartTime().getAllMilliseconds();
            int end = sub.getEndTime().getAllMilliseconds();
            int middle = (end - start) / 2;

            Subtitle sub2 = subFile2.getSubtitles().get(posSearchNatv);
            posSub2 = posSearchNatv;

            boolean hasNow = false;
            UnionSub us = new UnionSub();
            Fsub fsub = new Fsub();
            fsub.setSub(sub);


            us.addSubF(fsub);
            unionSubs.add(us);


            if (tempList.size() > 0) {

                int prefMiddle = (prefEnd - prefStart) / 2;
                boolean skeep = false;

                for (Subtitle s : tempList) {
                    Nsub nsub = new Nsub();
                    nsub.setSub(s);

                    if (skeep) {


                        us.addSubN(nsub);
                        break;
                    }
                    int start2 = s.getStartTime().getAllMilliseconds();
                    int end2 = s.getEndTime().getAllMilliseconds();

                    int middle2 = (end2 - start2) / 2;

                    int delta1 = Math.abs(middle2 - middle);
                    int delta2 = Math.abs(middle2 - prefMiddle);

                    if (delta1 < delta2) {
                        us.addSubN(nsub);
                        skeep = true;
                    } else {
                        prefUS.addSubN(nsub);
                    }


                }
                tempList.clear();


            }
            int skeeped = 0;
            while (posSub2 < maxSub2 - 1) {


                int start2 = sub2.getStartTime().getAllMilliseconds();
                int end2 = sub2.getEndTime().getAllMilliseconds();

                if (belong(start, start2, end2) || belong(end, start2, end2) || end2 < start) {
                    hasNow = true;
                    posSearchNatv = posSub2 + 1;
                    //добавить в структуру
                    tempList.add(sub2);
//                    us.addSubN(sub2.getLines());
                } else {
                    skeeped++;
                    if (hasNow) {

                        if (skeeped > SKEEP_TO_BREAK) {
                            break;
                        }
                        break;
                    }
                }


                posSub2++;
                sub2 = subFile2.getSubtitles().get(posSub2);


            }

            if (posSub >= maxSub1 - 1) {
                 //Util.showMassage("Вы достигли конца субтиров");
                break;
            }
            posSub++;
            prefUS = us;
            prefStart = start;
            prefEnd = end;

        }
       // approximateSubs(unionSubs);
        //approximateSubs(unionSubs);
        //approximateSubs(unionSubs);
       // approximateSubs(unionSubs);
        return unionSubs;

    }



    boolean belong(int x, int start, int end) {
        if (x >= start && x <= end) {
            return true;
        }
        return false;
    }



    int s(int len1, int len2) {
        return 1 - Math.abs(len1 - len2) / (len2 + len1) / 2;
    }


    void calculateAccel(UnionSub us, Nsub nsub) {
        int m1 = us.getFsub().getSub().getMiddle();
        int m2 = nsub.getSub().getMiddle();

        int len1 = us.getFsub().getSub().getLenght();
        int len2 = nsub.getSub().getLenght();

        int st = us.getFsub().getSub().getStrs();
        int st0 = us.getFsub().getSub().getStrs();

        int q = (len1 / SIZE_SMALL_SUB + len1);

        int S0 = s(len1, len2);

        int Dist = Math.abs(m1 - m2);

        int SM = 1;

//        for (Nsub nsub_f : us.getNsubs()) {
//            int len1f = nsub_f.getSub().getLenght();
//            SM += s(len1f, len2);
//        }


        int G = (st * Dist * q) / (st * SM * S0 * st0 + 1);

        int accel = G * Dist;

        //direct right
        if (m1 > m2) {

            nsub.setAccelRight(accel);

        } else {
            nsub.setAccelLeft(accel);
        }




    }

    void approximateSubs(List<UnionSub> unionSubs) {
        //пройтись по всем fsub, найти и отсортировать nsub,  вычислить для всех nsub их ускорения, присвоить нужным fsub
        // найти nsub > middlePrev, и  < middleNext добавить в лист по очереди

        for (int n = 0; n < unionSubs.size(); n++) {

            if (n == 0 || unionSubs.size() - 1 == n) {
                continue;
            }
            if(n == 13){
                int x = 4;
                x = x + 3;
            }
            UnionSub us = unionSubs.get(n);
            UnionSub prevUs = unionSubs.get(n - 1);
            UnionSub nextUs = unionSubs.get(n + 1);

            final int middle = us.getFsub().getSub().getMiddle();
            int middlePrev = prevUs.getFsub().getSub().getMiddle();
            int middleNext = nextUs.getFsub().getSub().getMiddle();

            List<Nsub> listNsubtemp = new ArrayList<>();

            List<Nsub> t1 = prevUs.getNsubsByPoint(middlePrev, false);
            List<Nsub> t2 = us.getNsubs();
            List<Nsub> t3 = nextUs.getNsubsByPoint(middleNext, true);

            listNsubtemp.addAll(t1);
            listNsubtemp.addAll(t2);
            listNsubtemp.addAll(t3);


            Collections.sort(listNsubtemp, new Comparator<Nsub>() {
                @Override
                public int compare(Nsub n1, Nsub n2) {
                    return n1.compareTo(n1, middle);
                }
            });


            //применить законы ньютона

            for (Nsub ns : listNsubtemp) {
                calculateAccel(us, ns);
            }
        }
        //притянуть nсабы к fsub

        for (int n = 0; n < unionSubs.size(); n++) {

            if (n == 0 || unionSubs.size() - 1 == n) {
                continue;
            }
            if(n == 14){
                int x = 4;
                x = x + 3;
            }

            UnionSub usNext = unionSubs.get(n + 1);
            UnionSub usPrev = unionSubs.get(n - 1);
            UnionSub us = unionSubs.get(n);

            List<Nsub> listDel = new ArrayList<>();
            for ( int t = 0; t< usPrev.getNsubs().size() ; t++ ) {

                Nsub nsub=usPrev.getNsubs().get(t);
                boolean b = dragSub(us, usPrev, nsub);
                if(b){
                    usPrev.remove(nsub);
                }
            }
//            for (Nsub nsub : us.getNsubs()) {
//                boolean b =  dragSub(usNext, us, nsub);
//                if(b){
//                    us.remove(nsub);
//                }
//            }

        }


    }

    // fsub пытается притянуть nsub других fsub
    boolean dragSub(UnionSub us, UnionSub usPrev, Nsub nsub) {
        int m1 = us.getFsub().getSub().getMiddle();
        int m2 = usPrev.getFsub().getSub().getMiddle();
        int m3 = nsub.getSub().getMiddle();
        if (m3 < m2) {
            return false;
        }
        //left side
        if (m1 > m2) {
            if (nsub.getAccelLeft() < nsub.getAccelRight()) {
                //добавить в начало списка

                us.addSubNOnFirstPos(nsub);
                return true;
            }
        } else {
            if (nsub.getAccelLeft() > nsub.getAccelRight()) {
                //добавить в конец списка

                us.addSubN(nsub);
                return true;
            }
        }

        return false;
    }


    public String convert(String pathToSub, boolean isNatvSub) throws Exception {
        String pathToReturn = "";
        if (pathToSub.isEmpty()){
            Log.v(TAG, "pathToSub i empty");
            return "";
        }

        int dot = pathToSub.lastIndexOf('.');
        String ext = pathToSub.substring(dot + 1);

        if (ext.equalsIgnoreCase("SRT")) {
            pathToReturn = pathToSub;
        } else if ("STL".equalsIgnoreCase(ext) || "SCC".equalsIgnoreCase(ext) || "XML".equalsIgnoreCase(ext) || "ASS".equalsIgnoreCase(ext)) {

            if (isNatvSub) {
                pathToReturn = pathToNSUB;
            } else {
                pathToReturn = pathToFSUB;
            }
            String[] strings = {pathToSub, ext, "srt", pathToReturn};
            SubtitleConvert.main(strings);


        } else {
            Util.showMassage(ext + " is not supperted format");
        }
        //определить расширение, если нет srt конвертировать в srt.


        return pathToReturn;
    }


    public class UnionSub {


        Fsub fsub;
        List<Nsub> nsubs = new ArrayList<>();

        public UnionSub() {

        }

        public List<Nsub> getNsubs() {
            return nsubs;
        }

        public Fsub getFsub() {
            return fsub;
        }

        public void addSubF(Fsub fsub) {
            this.fsub = fsub;
        }

        public void addSubN(Nsub nsub) {
            nsubs.add(nsub);
        }


        public List<String> getListSubF() {

            List<String> listSubF = new ArrayList<>();
            return fsub.getSub().getLines();

        }

        public List<String> getListSubN() {
            List<String> list = new ArrayList<>();
            for (Nsub nsub : nsubs) {
                list.addAll(nsub.getSub().getLines());
            }
            return list;
        }

        public List<Nsub> getNsubsByPoint(int point, boolean leftSide) {

            List<Nsub> list = new ArrayList<>();
            for (Nsub nsub : nsubs) {
                if (leftSide) {
                    if (nsub.getSub().getMiddle() < point) {
                        list.add(nsub);
                    }
                    ;
                } else {
                    if (nsub.getSub().getMiddle() > point) {
                        list.add(nsub);
                    }
                    ;
                }
            }
            return list;
        }

        public void addSubNOnFirstPos(Nsub nsub) {
            nsubs.add(0,nsub);
        }

        public void remove(Nsub nsub) {
            nsubs.remove(nsub);
        }
    }


    public class Fsub {

        Subtitle sub;



        public void setSub(Subtitle sub) {
            this.sub = sub;
        }


        public Subtitle getSub() {
            return sub;
        }



    }


    public class Nsub {

        Subtitle sub;


        float accelLeft = 0;
        float accelRight = 0;


        public Subtitle getSub() {
            return sub;
        }

        public void setSub(Subtitle sub) {
            this.sub = sub;
        }


        public float getAccelLeft() {
            return accelLeft;
        }

        public void setAccelLeft(float accelLeft) {
            this.accelLeft = accelLeft;
        }

        public float getAccelRight() {
            return accelRight;
        }

        public void setAccelRight(float accelRight) {
            this.accelRight = accelRight;
        }

        public int compareTo(Nsub n1, int middle) {
            int m1 = Math.abs(middle - sub.getMiddle());
            int m2 = Math.abs(middle - n1.getSub().getMiddle());
            return m1 - m2;
        }
    }


}
