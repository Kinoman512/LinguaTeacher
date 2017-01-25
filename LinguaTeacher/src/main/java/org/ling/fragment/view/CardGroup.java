package org.ling.fragment.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import org.R;
import org.ling.MainActivity;
import org.ling.fragment.FragmentTrainResult;
import org.ling.fragment.anim.FlipAnimation;
import org.ling.model.CommonWord;
import org.ling.model.SimpleWord;
import org.ling.model.Stage;
import org.ling.model.agent.StageAgent;
import org.ling.service.ImageService;
import org.ling.service.PlayAudioCardsService;
import org.ling.utils.IActionHandler;
import org.ling.utils.TrainUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

;import odyssey.ru.linglibrary.Picker;
import odyssey.ru.linglibrary.Util;
import odyssey.ru.linglibrary.ViewUtil;

/**
 * Created by Dmitry on 09.07.2016.
 */
public class CardGroup extends ViewGroup {
    private static final int ATTEMPS = 2;
    int deviceWidth;
    List<View> cards = new ArrayList<>();
    List<View> childsBegins = new ArrayList<>();
    public static CardGroup istance;

    private float mLastMotionX;
    private float mLastMotionY;
    private float mTouchY;
    private float mTouchX;
    private static boolean isPlayAudioCardsServiceRun;
    private boolean blocked = true;

    int curCard = -1;
    View currentView;
    boolean fliped = false;
    private boolean ignoreMistakes;
    private int prevCurCard;
    private int deleted = 0;
    Picker<View> picker;


    public boolean isPlayAudioCardsServiceRun() {
        return isPlayAudioCardsServiceRun;
    }

    public CardGroup(Context context) {
        this(context, null, 0);
        istance = this;
    }

    public List<View> getCards() {
        return cards;
    }

    public View getCurrentView() {
        if (currentView == null) {

            if (cards.size() == 0) return null;
            currentView = cards.get(curCard);
        }
        return currentView;
    }

    public int getCurCard() {
        return curCard;
    }

    public CardGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        istance = this;
    }

    public CardGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        istance = this;
    }

    private void flipCard(View v, boolean direct, boolean skip) {
        View cardFace = v.findViewById(R.id.card_viewfont);
        View cardBack = v.findViewById(R.id.card_viewback);

        FlipAnimation flipAnimation = new FlipAnimation(cardFace, cardBack);
        flipAnimation.setDirect(direct);
        flipAnimation.skipAnim(skip);

        if (cardFace.getVisibility() == View.GONE) {
            flipAnimation.reverse();
        }
        v.startAnimation(flipAnimation);
    }


    private void init(Context context) {
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point deviceDisplay = new Point();
        display.getSize(deviceDisplay);
        deviceWidth = deviceDisplay.x;





    }

    @Override
    public void addView(View child) {

        cards.add(child);
        childsBegins.add(child);
        curCard++;
        Card card = (Card) child;
        CommonWord cw = card.getTu().getCommonWord();


        final TextView trscTxt = (TextView) child.findViewById(R.id.trscTxt);
        final TextView txtValue = (TextView) child.findViewById(R.id.txtValue);
        final TextView card_word = (TextView) child.findViewById(R.id.card_word);
        final TextView card_trans = (TextView) child.findViewById(R.id.card_trans);
        final TextView card_word_trans = (TextView) child.findViewById(R.id.card_word_trans);
//        final TextView noteView = (TextView) child.findViewById(R.id.noteView);

        final ImageView imgWord = (ImageView) child.findViewById(R.id.imgWord);
        final PopupMenuBotton btn_popup_sound = (PopupMenuBotton) child.findViewById(R.id.btn_popup_sound);
        final PopupMenuBotton btn_popup_sound2 = (PopupMenuBotton) child.findViewById(R.id.btn_popup_sound_back);

        final PlaySoundBotton btn_play_back = (PlaySoundBotton) child.findViewById(R.id.btn_play_back);
        final PlaySoundBotton btn_play_front = (PlaySoundBotton) child.findViewById(R.id.btn_play_front);
        final PlaySoundBotton btn_play_back2 = (PlaySoundBotton) child.findViewById(R.id.btn_play_back2);
        final PlaySoundBotton btn_play_back_from_front = (PlaySoundBotton) child.findViewById(R.id.btn_play_back_from_front);

        final TextView statusStage = (TextView) child.findViewById(R.id.statusStage);
        final TextView statusStage2 = (TextView) child.findViewById(R.id.statusStage2);

        ViewUtil.setTextHidded(card_word_trans);

//        noteView.setText("");

        List<Stage> list = StageAgent.getAll();
        Stage st = card.getTu().getCommonWord().getInsStage();
        int pos = list.indexOf(st);
        if (pos < -1) {
            pos = 0;
        } else {
            pos += 1;
        }

        statusStage.setText( Util.getStringById(R.string.stage) + " " + pos + "/" + list.size());
        statusStage2.setText(Util.getStringById(R.string.stage) + " " + pos + "/" + list.size());

        final CardView card_viewfont = (CardView) child.findViewById(R.id.card_viewfont);
        final CardView card_viewback = (CardView) child.findViewById(R.id.card_viewback);

//
//        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
//        if(displaymetrics.widthPixels < 720){
//            int width = 400;
//            int height = 250;
//            card_viewfont.setLayoutParams(new LinearLayout.LayoutParams(width, height));
//            card_viewback.setLayoutParams(new LinearLayout.LayoutParams(width, height));
//        }

        btn_popup_sound.setSimpleWord(cw.getInsSource());
        btn_popup_sound2.setSimpleWord(cw.getInsDest());

        btn_popup_sound2.setPsb(btn_play_back);
        btn_popup_sound2.addPsb(btn_play_back_from_front);

        btn_popup_sound.setPsb(btn_play_front);
        btn_popup_sound2.addPsb(btn_play_back2);

        ImageService.getImage(cw.getImage(), new IActionHandler() {
            @Override
            public void onSuccessAction(final Object rs) {
                imgWord.setImageBitmap((Bitmap) rs);
            }

            @Override
            public void onFailAction(String s, Throwable throwable) {
                Log.d("CardGroup", "123");
            }
        });

        ViewUtil.setTextHidded(trscTxt);

        txtValue.setTextColor(Color.LTGRAY);
        txtValue.setBackgroundColor(Color.LTGRAY);
        txtValue.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setBackgroundColor(Color.WHITE);
                return true;
            }
        });


        SimpleWord sw1 = cw.getInsSource();
        SimpleWord sw2 = cw.getInsDest();

        card_word_trans.setText(sw2.getWord());
        card_word.setText(sw1.getWord());
        card_trans.setText(sw2.getWord());
        trscTxt.setText(sw1.getTrscr());
        txtValue.setText(sw1.getWord());

        btn_play_front.setSound(cw.getInsSource().getCurrentPronounce(), cw.getInsSource().getLang());
        btn_play_back_from_front.setSound(cw.getInsSource().getCurrentPronounce(), cw.getInsSource().getLang());

        btn_play_back.setSound(cw.getInsDest().getCurrentPronounce(), cw.getInsDest().getLang());
        btn_play_back2.setSound(cw.getInsDest().getCurrentPronounce(), cw.getInsDest().getLang());


        btn_play_front.setOnTouchListener(blockedTouchListner);
        btn_play_back_from_front.setOnTouchListener(blockedTouchListner);
        btn_play_back.setOnTouchListener(blockedTouchListner);
        btn_play_back2.setOnTouchListener(blockedTouchListner);

        super.addView(child);
    }


    OnTouchListener blockedTouchListner = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if ( isPlayAudioCardsServiceRun ) {
                return true;
            }
            return false;
        }
    };

    void onChangeChackStatus(Card child) {
        CheckImage check1 = (CheckImage) child.findViewById(R.id.check1);
        CheckImage check2 = (CheckImage) child.findViewById(R.id.check2);
        CheckImage check3 = (CheckImage) child.findViewById(R.id.check3);

        CheckImage check4 = (CheckImage) child.findViewById(R.id.check4);
        CheckImage check5 = (CheckImage) child.findViewById(R.id.check5);
        CheckImage check6 = (CheckImage) child.findViewById(R.id.check6);

        int x = child.getTu().getMap().size();
        switch (x) {
            case 1:
                check1.setOk();
                check4.setOk();
                break;
            case 2:
                check2.setOk();
                check5.setOk();
                break;
            case 3:
                check3.setOk();
                check6.setOk();
                break;
        }
    }

    void onErrorChange(Card child) {
        TextView errorText = (TextView) child.findViewById(R.id.errorText);
        TextView errorText2 = (TextView) child.findViewById(R.id.errorText2);

        errorText.setText(Util.getStringById(R.string.error) + " "+ child.getTu().getMistakes());
        errorText2.setText(Util.getStringById(R.string.error) + " " + child.getTu().getMistakes());
        errorText.setVisibility(VISIBLE);
        errorText2.setVisibility(VISIBLE);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        int curWidth, curHeight, curLeft, curTop, maxHeight;

        //get the available size of child view
        final int childLeft = this.getPaddingLeft();
        final int childTop = this.getPaddingTop();
        final int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int childBottom = this.getMeasuredHeight() - this.getPaddingBottom();
        final int childWidth = childRight - childLeft;
        final int childHeight = childBottom - childTop;

        maxHeight = 0;
        curLeft = childLeft;
        curTop = childTop;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            child.setOnTouchListener(blockedTouchListner);


            if (child.getVisibility() == GONE)
                return;

            //Get the maximum size of the child
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));
            curWidth = child.getMeasuredWidth();
            curHeight = child.getMeasuredHeight();
            //wrap is reach to the end
            if (curLeft + curWidth >= childRight) {
                curLeft = childLeft;
                curTop += maxHeight;
                maxHeight = 0;
            }
            //do the layout
            curLeft = getMeasuredWidth() / 2 - child.getMeasuredWidth() / 2;
            curTop = getMeasuredHeight() / 2 - child.getMeasuredHeight() / 2;
            child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight);
            //store the max height
            if (maxHeight < curHeight)
                maxHeight = curHeight;
            curLeft += curWidth;
        }
    }


    void onFinish() {
        FragmentTrainResult newFragment = new FragmentTrainResult();
        ArrayList<TrainUnit> ltu = new ArrayList<>();
        for (View e : childsBegins) {
            Card card = (Card) e;
            TrainUnit tu = card.getTu();
            ltu.add(tu);
        }
        Bundle args = new Bundle();
        args.putSerializable("Trains", ltu);
        args.putBoolean("save", true);
        newFragment.setArguments(args);

        MainActivity.switchToNextFragment(newFragment);
//        MainActivity/.setFragment(newFragment,false);
    }

    public void onUp(View v, boolean needErrorChange) {
        Card card = (Card) v;
        TrainUnit tu = card.getTu();
        tu.incMistakes();

        moveUP(v, fliped);
        prevCurCard = nextCurCard();
        curCard = prevCurCard;
        try{
            currentView = cards.get(curCard);


        } catch(Exception e){
            Log.d("Error", e.getMessage());
        }
        View v2 = cards.get(curCard);
        View v3 = cards.get(prevCurCard);
        fliped = false;
        Log.d("IndexCardGroup ", "Index " + IndexCardGroup + " " + v2.hashCode() + " " +   v3.hashCode() );

        waitToBringToFront(v2, v3);
        if (needErrorChange && !ignoreMistakes)
            onErrorChange(card);
    }


    void onDown(View v) {
        Card card = (Card) v;
        TrainUnit tu = card.getTu();
        if (!ignoreMistakes)
            tu.addTime(122);
        if (tu.getAttempts() <= ATTEMPS) {
            moveDOWN(v, fliped);

            prevCurCard = nextCurCard();
            curCard = prevCurCard;

            currentView = cards.get(curCard);
            View v2 = cards.get(curCard);
            View v3 = cards.get(prevCurCard);
            fliped = false;
            waitToBringToFront(v2, v3);
            if (!ignoreMistakes)
                onChangeChackStatus(card);
        } else {
            moveOUT(v, fliped);

            deleteCard(curCard);
            waitToDelete(v);
            //cards.remove(v);

            prevCurCard = nextCurCard();
            curCard = prevCurCard;
            if (cards.isEmpty()) {
                onFinish();
                return;
            }

            currentView = cards.get(curCard);
            View v2 = cards.get(curCard);
            View v3 = cards.get(prevCurCard);
            waitToBringToFront(v2, v3);



            fliped = false;

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        View v = getCurrentView();
        if (v == null  ) return true;
        if (cards.size() == 0) {
            return true;
        }


        final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                break;

            case MotionEvent.ACTION_UP:
                float deltaX = mTouchX - mLastMotionX;
                float deltaY = mTouchY - mLastMotionY;

                double corn = Math.abs(Math.atan(deltaY / deltaX));
                final TextView tv = (TextView) v.findViewById(R.id.trscTxt);
                tv.setTextColor(Color.LTGRAY);
                tv.setBackgroundColor(Color.LTGRAY);

                final TextView tv2 = (TextView) v.findViewById(R.id.txtValue);
                tv2.setTextColor(Color.LTGRAY);
                tv2.setBackgroundColor(Color.LTGRAY);

                if (corn < 0.3) {
                    if (deltaX > 50) {
                        flipCard(v, false, false);
                        Log.d("CardView", " вправо");
                        fliped = !fliped;
                    }
                    if (deltaX < -50) {
                        Log.d("CardView", " влево");
                        fliped = !fliped;
                        flipCard(v, true, false);
                    }
                }

                if (corn > 0.8) {
                    if (deltaY > 50) {
//                        moveDOWN(v);

                        Log.d("CardView", " вниз");
                        onDown(v);
                    }
                    if (deltaY < -50) {
                        Log.d("CardView", " вверх");
                        onUp(v, true);
                    }
                }
                Log.d("CardView", "double" + corn);
                String act = "no act!";
                if (corn < 0.3 || corn > 0.7) {
                    act = "act!!!!!!!!!!!";
                }
                Log.d("CardView", "dx" + deltaX);
                Log.d("CardView", "dy" + deltaY);
                break;

            case MotionEvent.ACTION_MOVE:
                mTouchX = x;
                mTouchY = y;
                break;
        }
        return true;
    }


    void deleteCard(int index){
            cards.remove( index );
           // picker = new Picker<>(cards);
            deleted++;
    }


    int IndexCardGroup = 0;
    View  prevView;

    int nextCurCard() {

        if(picker  == null){
            View[] array = cards.toArray(new View[cards.size()]);
            picker = new Picker<>(array);
        }

        if(deleted >= cards.size()){
         //  Log.d("List Cards is all deleted");
            return 0;
        }
        View   y = picker.pickRandom();;//nextCurCard();
        int index = cards.indexOf(y);


        int cursor = index;
        while (!cards.contains(y) &&  cursor == index ){
            if( cursor >= cards.size() - 1 ){
                cursor = 0;
            }else{
                cursor++;
            }

            if( !cards.contains(y)){
                y =  cards.get(cursor);
            }


        }

        while ( y.equals(prevView) && cards.size() != 1){
            if( cursor >= cards.size() - 1 ){
                cursor = 0;
            }else{
                cursor++;
            }

            if( y.equals(prevView) ){
                y =  cards.get(cursor);
            }


        }

        index = cards.indexOf(y);
        prevView = y;


        if( picker.nothingToPick()){
            picker.revert();
        }
        IndexCardGroup++;
        Log.d("IndexCardGroup ", "Index " + IndexCardGroup + " " + index);
        return index;
    }


    void waitToDelete(final View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(250);
                    MainActivity.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (v == null || v.getParent() == null) return;
                            ((ViewGroup) v.getParent()).removeView(v);
                        }
                    });


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void waitToBringToFront(final View v, final View v2) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    MainActivity.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            v.bringToFront();
                            v2.bringToFront();
                        }
                    });


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void waitToFlip(final View v, final boolean fliped) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(600);
                    MainActivity.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (fliped)
                                flipCard(v, true, true);
                        }
                    });


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void moveOUT(View view, final boolean fliped) {
        Animation anim = AnimationUtils.loadAnimation(MainActivity.activity, R.anim.out);
        view.startAnimation(anim);
        //waitToFlip(view,fliped);

    }


    private void moveDOWN(View view, final boolean fliped) {
        Animation anim = AnimationUtils.loadAnimation(MainActivity.activity, R.anim.downup);
        view.startAnimation(anim);
        waitToFlip(view, fliped);
    }

    private void moveUP(View view, final boolean fliped) {
        Animation anim = AnimationUtils.loadAnimation(MainActivity.activity, R.anim.updown);
        view.startAnimation(anim);
        waitToFlip(view, fliped);

        //test();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        // Measurement will ultimately be computing these values.
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;
        int mLeftWidth = 0;
        int rowCount = 0;

        // Iterate through all children, measuring them and computing our dimensions
        // from their size.
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                continue;

            // Measure the child.
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            maxWidth += Math.max(maxWidth, child.getMeasuredWidth());
            mLeftWidth += child.getMeasuredWidth();

            if ((mLeftWidth / deviceWidth) > rowCount) {
                maxHeight += child.getMeasuredHeight();
                rowCount++;
            } else {
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        // Report our final dimensions.
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }


    boolean testBl = true;

    public void test(){
        Map<View, Integer> mapCount = new HashMap();

        for(int x = 0; x < 800; x++){


            int index = nextCurCard();
            View  y = cards.get(index);


            if( x % 7 == 100006){
                cards.set( index ,null);
                deleted++;
                //picker = new Picker<>(cards);
                System.out.println( "remove y" + x + " = " + y.hashCode() + " deleted " + deleted);
            }
            System.out.println( "wey" + x + " = " + y );
            Integer t = mapCount.get(y);
            if(t == null){
                t = 0;
            }
            int c = t + 1;
            if(t > 200){
                int x2= 1;
                x2 += 32;
            }

            mapCount.put(y,c);

        }

        System.out.println( "mapCount " +  mapCount);
        if(testBl){
            testBl = false;
            test();
        }

    }


    public void changeFaceCards(boolean isChecked) {


        for (View v : cards) {
            flipCard(v, isChecked, true);
        }
    }

    public void goneImages(boolean isChecked) {
        for (View v : cards) {
            View imgView = v.findViewById(R.id.imgWord);
            if (isChecked) {
                imgView.setVisibility(INVISIBLE);
            } else {
                imgView.setVisibility(VISIBLE);
            }
        }
    }

    public void playAudioAtCircle(boolean isChecked) {

        isPlayAudioCardsServiceRun = isChecked;
        PlayAudioCardsService.stopAll();
        if (isChecked) {
//            Bundle args = new Bundle();
//            args.putSerializable("CardGroup", this);
            Intent serviceIntent = new Intent(getContext(), PlayAudioCardsService.class);
//            serviceIntent.putExtras(args);
            getContext().startService(serviceIntent);
        } else {
            Intent serviceIntent = new Intent(getContext(), PlayAudioCardsService.class);
            getContext().stopService(serviceIntent);
        }
    }

    public void ignoreMistakes(boolean isChecked) {
        ignoreMistakes = isChecked;
    }
}