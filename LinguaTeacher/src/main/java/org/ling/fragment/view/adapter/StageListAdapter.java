package org.ling.fragment.view.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.R;
import org.ling.MainActivity;
import org.ling.model.Stage;
import org.ling.model.agent.StageAgent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

;

/**
 * Created by Dmitry on 15.07.2016.
 */
public class StageListAdapter extends BaseAdapter {

    Context mContext;
    List<Stage> stages = new ArrayList<>();
    List<Stage> stagesForDelete = new ArrayList<>();
    ListView lv;


    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public StageListAdapter(Context mContext, List<Stage> s, ListView lv) {
        this.mContext = mContext;
        this.lv = lv;
//        Collections.sort(lcw, new Comparator<CommonWord>() {
//            @Override
//            public int compare(CommonWord o1, CommonWord o2) {
//                //The dates are currently sorted in Ascending order
//
//                return (int) (o2.getAdded() - o1.getAdded());
//
//                //To have them in Descending order just switch the DateTimes around
//            }
//        });
        this.stages.addAll(s);


    }

    @Override
    public int getCount() {
        return stages.size();
    }

    @Override
    public Object getItem(int position) {
        return stages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;//stages.get(position).getId();
    }

    public void save() {


        Iterator it = stages.iterator();
        while (it.hasNext()) {
            Stage st = (Stage) it.next();
            if (!stagesForDelete.contains(st)) {
                if (st.getDelta() == 0) {
                    stagesForDelete.add(st);

                } else {
                    StageAgent.update(st);
                }
            } else {
                StageAgent.delete(st.getWeight());
            }
        }
        stages.removeAll(stagesForDelete);
        stagesForDelete.clear();
        Toast.makeText(mContext, MainActivity.activity.getResources().getString(R.string.saved),
                Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();
    }


    @Override
    public View getView(final int pos, View someView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (someView == null) {
            someView = inflater.inflate(R.layout.item_stage, parent, false);
            final Stage stage = stages.get(pos);

            final LinearLayout stage_holder = (LinearLayout) someView.findViewById(R.id.stage_holder);
            if (pos == 0) {
                stage_holder.setVisibility(View.VISIBLE);
            }

            final EditText year = (EditText) someView.findViewById(R.id.yearField);
            final EditText month = (EditText) someView.findViewById(R.id.monthField);
            final EditText day = (EditText) someView.findViewById(R.id.dayField);
            final EditText hour = (EditText) someView.findViewById(R.id.hourField);

            final LinearLayout stage_front = (LinearLayout) someView.findViewById(R.id.stage_front);
            final LinearLayout stage_back = (LinearLayout) someView.findViewById(R.id.stage_back);
            TextView restoreStage = (TextView) someView.findViewById(R.id.restoreStage);

            TextView stage_pos = (TextView) someView.findViewById(R.id.stage_pos);
            com.mikepenz.iconics.view.IconicsImageView btn_stage_add = (com.mikepenz.iconics.view.IconicsImageView) someView.findViewById(R.id.btn_stage_add);

            com.mikepenz.iconics.view.IconicsImageView btn_stage_delete = (com.mikepenz.iconics.view.IconicsImageView) someView.findViewById(R.id.btn_stage_delete);

            if (stagesForDelete.contains(stages.get(pos))) {
                stage_front.setVisibility(View.GONE);
                stage_back.setVisibility(View.VISIBLE);
            } else {
                stage_front.setVisibility(View.VISIBLE);
                stage_back.setVisibility(View.GONE);
            }

            restoreStage.setText(MainActivity.activity.getResources().getString(R.string.restore));

            final long[] y = {0};
            final long[] m = {0};
            final long[] d = {0};
            final long[] h = {0};

            year.addTextChangedListener(new TextWatcher() {


                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Stage st = stages.get(pos);
                    String h2 = year.getText().toString();
                    if (h2.isEmpty()) return;
                    y[0] = Long.valueOf(year.getText().toString()) * DateUtils.YEAR_IN_MILLIS;
                    st.setDelta(y[0] + m[0] + d[0] + h[0]);
                }
            });


            month.addTextChangedListener(new TextWatcher() {


                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Stage st = stages.get(pos);
                    String h2 = month.getText().toString();
                    if (h2.isEmpty()) return;
                    m[0] = Long.valueOf(month.getText().toString()) * DateUtils.DAY_IN_MILLIS * 30;
                    st.setDelta(y[0] + m[0] + d[0] + h[0]);
                }
            });


            day.addTextChangedListener(new TextWatcher() {


                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Stage st = stages.get(pos);
                    String h2 = day.getText().toString();
                    if (h2.isEmpty()) return;
                    d[0] = Long.valueOf(day.getText().toString()) * DateUtils.DAY_IN_MILLIS;
                    st.setDelta(y[0] + m[0] + d[0] + h[0]);
                }
            });

            hour.addTextChangedListener(new TextWatcher() {


                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        Stage st = stages.get(pos);
                        String h2 = hour.getText().toString();
                        if (h2.isEmpty()) return;
                        h[0] = Long.valueOf(hour.getText().toString()) * DateUtils.HOUR_IN_MILLIS;
                        st.setDelta(y[0] + m[0] + d[0] + h[0]);
                    } catch (Exception e) {
                    }

                }
            });


            btn_stage_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Stage newStage = new Stage();


                    long prevWeight = 0;
                    long nextWeight = 0;
                    if (pos == 0) {
                        prevWeight = stages.get(pos).getWeight();
                    } else {
                        prevWeight = stages.get(pos - 1).getWeight();
                    }

                    if (pos <= stages.size()) {
                        nextWeight = stages.get(pos).getWeight() + 2;
                    } else {
                        nextWeight = stages.get(pos + 1).getWeight();
                    }
                    newStage.setDelta(0);

                    long w = (nextWeight + prevWeight) / 2;
                    newStage.setWeight(w);

                    List<Stage> items = new ArrayList<Stage>();
                    items.addAll(stages);
                    items.add(pos + 1, newStage);


                    stages.clear();
                    stages.addAll(items);

                    StageListAdapter.this.notifyDataSetChanged();
//                    MainActivity.setListViewHeightBasedOnChildren(lv, 5);
                    scrollMyListView(lv, pos + 1);
                }
            });

            btn_stage_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (stagesForDelete.size() + 1 == stages.size()) {
                        Toast.makeText(mContext, MainActivity.activity.getResources().getString(R.string.cannot_delete_last_step), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    stage_front.setVisibility(View.GONE);
                    stage_back.setVisibility(View.VISIBLE);

                    stagesForDelete.add(stages.get(pos));
                    StageListAdapter.this.notifyDataSetChanged();
//                    MainActivity.setListViewHeightBasedOnChildren(lv, 30);
                }
            });

            restoreStage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stage_front.setVisibility(View.VISIBLE);
                    stage_back.setVisibility(View.GONE);

                    stagesForDelete.remove(stages.get(pos));
                    StageListAdapter.this.notifyDataSetChanged();

                }
            });


            long monthRest = stage.getDelta() % (DateUtils.YEAR_IN_MILLIS);
            long dayRest = monthRest % (DateUtils.DAY_IN_MILLIS * 30);
            long hoursRest = dayRest % DateUtils.DAY_IN_MILLIS;

            int years = (int) (stage.getDelta() / DateUtils.YEAR_IN_MILLIS);
            int months = (int) (monthRest / (DateUtils.DAY_IN_MILLIS * 30));
            int days = (int) (dayRest / DateUtils.DAY_IN_MILLIS);
            int hours = (int) (hoursRest / DateUtils.HOUR_IN_MILLIS);

            year.setText(String.valueOf(years));
            month.setText(String.valueOf(months));
            day.setText(String.valueOf(days));
            hour.setText(String.valueOf(hours));
            stage_pos.setText(String.valueOf((pos + 1)));

        }


        return someView;


    }

    public int getViewTypeCount() {
        return 100;
    }

    @Override
    public int getItemViewType(int position) {
        if (   position < getViewTypeCount() - 1) {
            return position  ;
        } else {
            return getViewTypeCount() - 1;
        }
//        return  0;
    }


    private void scrollMyListView(final ListView listView, final int pos) {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
//                MainActivity.setListViewHeightBasedOnChildren(listView, 30);
                listView.setSelection(pos);
            }
        });
    }
}
