package malvolyo.teachingassistantprofesor;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryQuizActivity extends AppCompatActivity {
    String history_id;
    String user;
    String course;
    private PieChart pc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_quiz);

        Intent intent = getIntent();
        history_id = intent.getStringExtra(Constants.HISTORY_ID);
        user = intent.getStringExtra(Constants.USERNAME);
        course = intent.getStringExtra(Constants.COURSE);

        pc = (PieChart)findViewById(R.id.history_quizPieChart);

        pc.setUsePercentValues(true);

        pc.setDrawHoleEnabled(true);
        pc.setHoleRadius(10);
        pc.setTransparentCircleRadius(10);

        Description desc = new Description();
        desc.setText("Quiz Statistics");
        desc.setTextColor(Color.BLACK);
        desc.setTextSize(20);
        pc.setDescription(desc);

        FirebaseDatabase.getInstance().getReference()
                .child(course).child(DBConstants.QUIZ).child(DBConstants.HISTORY_QUIZ)
                .child(history_id).child(DBConstants.ANSWERED).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int counter = 0;
                pc.setVisibility(View.GONE);
                findViewById(R.id.historyQuizLoading).setVisibility(View.VISIBLE);

                if(dataSnapshot.getChildrenCount() == 0) {
                    findViewById(R.id.historyQuizLoading).setVisibility(View.GONE);
                    return;
                }

                findViewById(R.id.historyQuizLoading).setVisibility(View.VISIBLE);

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                HashMap<String, Integer> idToPos = new HashMap<String, Integer>();

                for(DataSnapshot ds : children) {
                    String val = "" + ds.getValue();
                    if (!idToPos.containsKey(val)) {
                        idToPos.put(val, counter);
                        counter++;
                    }
                }

                ArrayList<PieEntry> yEntry = new ArrayList<PieEntry>();

                Float[] yData = new Float[counter];
                String[] yAns = new String[counter];

                for (int i = 0; i < yData.length; i++)
                    yData[i] = 0f;

                children = dataSnapshot.getChildren();

                counter = 0;

                for(DataSnapshot ds : children) {
                    ++counter;
                    String val = "" + ds.getValue();
                    yData[idToPos.get(val)]++;
                    yAns[idToPos.get(val)] = "" + ds.getChildren().iterator().next().getValue();
                }

                for (int i = 0; i < yData.length; i++) {
                    float wr = yData[i] / counter * 100;

                    yEntry.add(new PieEntry(wr, yAns[i]));
                }

                PieDataSet pds = new PieDataSet(yEntry, "");
                pds.setSliceSpace(3);
                pds.setSelectionShift(5);

                ArrayList<Integer> colors = new ArrayList<Integer>();

                colors.add(Color.CYAN);
                colors.add(Color.RED);
                colors.add(Color.BLUE);
                colors.add(Color.GREEN);
                colors.add(Color.LTGRAY);
                colors.add(Color.MAGENTA);
                colors.add(Color.GRAY);
                colors.subList(yData.length, colors.size()).clear();
                pds.setColors(colors);

                PieData pd = new PieData(pds);

                pd.setValueFormatter(new PercentFormatter());
                pd.setValueTextSize(30);

                pc.setData(pd);

                pc.setCenterText("" + counter);

                pc.invalidate();

                findViewById(R.id.historyQuizLoading).setVisibility(View.GONE);
                pc.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
