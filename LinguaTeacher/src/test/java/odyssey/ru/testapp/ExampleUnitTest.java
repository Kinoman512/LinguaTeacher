package odyssey.ru.testapp;

import android.util.Log;
import android.view.View;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import odyssey.ru.linglibrary.Picker;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    int cardCount = 3;
    static List<String>  cards = new ArrayList();
    Picker<String> picker;
    int deleted = 0;


    int nextCurCard() {

        if(deleted >= cards.size()){
            System.out.println( "List Cards is all deleted");
            return 0;
        }
        String  y = picker.pickRandom();;//nextCurCard();
        int index = cards.indexOf(y);


        int cursor = index;
        while (y == null && cursor == index){
            if( cursor >= cards.size() - 1 ){
                cursor = 0;
            }else{
                cursor++;
            }

            if( cards.get(cursor) != null){
                y =  cards.get(cursor);
            }


        }

        index = cards.indexOf(y);
        return index;
    }


    @Test
    public void testSome(){


       //Integer[] array = new Integer[100];
        try { picker = new Picker<>(cards);

            Map<String, Integer> mapCount = new HashMap();

            for(int x = 0; x < 1004; x++){


                int index = nextCurCard();
                String  y = cards.get(index);


                if( x % 7 == 100006){
                    cards.set( index ,null);
                    deleted++;
                    //picker = new Picker<>(cards);
                    System.out.println( "remove y" + x + " = " + y + " deleted " + deleted);
                }
                System.out.println( "wey" + x + " = " + y );
                Integer t = mapCount.get(y);
                if(t == null){
                    t = 0;
                }
                int c = t + 1;
                mapCount.put(y,c);
                if( picker.nothingToPick()){
                    picker.revert();
                }
            }

            System.out.println( "mapCount " +  mapCount);
        }catch(Exception e){

        }





    }
    @Test
    public void test2() {
        try { picker = new Picker<>(cards);

            Map<String, Integer> mapCount = new HashMap();

            for(int x = 0; x < 1004; x++){


                int index = nextCurCard();
                String  y = cards.get(index);


                if( x % 7 == 100006){
                    cards.set( index ,null);
                    deleted++;
                    //picker = new Picker<>(cards);
                    System.out.println( "remove y" + x + " = " + y + " deleted " + deleted);
                }
                System.out.println( "wey" + x + " = " + y );
                Integer t = mapCount.get(y);
                if(t == null){
                    t = 0;
                }
                int c = t + 1;
                mapCount.put(y,c);
                if( picker.nothingToPick()){
                    picker.revert();
                }
            }

            System.out.println( "mapCount " +  mapCount);
        }catch(Exception e){

        }
    }





    public int testFunct() {

        picker = new Picker<>(cards);
        if (picker == null) {
            picker = new Picker<>(cards);
        }

        if (deleted >= cards.size()) {
            System.out.println("List Cards is all deleted");
            return 0;
        }
        String y = picker.pickRandom();
        ;//nextCurCard();
        int index = cards.indexOf(y);


        int cursor = index;
        while (y == null && cursor == index) {
            if (cursor >= cards.size() - 1) {
                cursor = 0;
            } else {
                cursor++;
            }

            if (cards.get(cursor) != null) {
                y = cards.get(cursor);
            }


        }

        index = cards.indexOf(y);


        if (picker.nothingToPick()) {
            picker.revert();
        }
//        Log.d("IndexCardGroup ", "Index " + index);
        return index;


    }




    @BeforeClass
    public static void setUpClass() {
        System.out.println("Master setup");

        cards.add("1a");
        cards.add("2b");
        cards.add("3c");
        cards.add("4d");
        cards.add("55");
//        cards.add("66");
//        cards.add("77");
//        cards.add("88");
//        cards.add("99");
//        cards.add("111");

    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println("Master tearDown");
    }


}