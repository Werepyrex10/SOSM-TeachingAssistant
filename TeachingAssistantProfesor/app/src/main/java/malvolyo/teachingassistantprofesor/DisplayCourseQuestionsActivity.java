package malvolyo.teachingassistantprofesor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DisplayCourseQuestionsActivity extends AppCompatActivity {
    private DatabaseReference database;
    private String today;
    private RelativeLayout questions;
    private RelativeLayout rl;
    private String course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_course_questions);

        Intent intent = getIntent();
        course = intent.getStringExtra(Constants.COURSE);

        questions = new RelativeLayout(this);
        rl = (RelativeLayout) findViewById(R.id.activity_display_course_questions);

        database = FirebaseDatabase.getInstance().getReference();
        setDay();

        database.child(course).child(DBConstants.COURSE_QUESTIONS).child(today).
                child(DBConstants.QUESTIONS).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                        int prev_id_button = -1;
                        int prev_id_text = -1;

                        rl.removeView(questions);

                        findViewById(R.id.questionsLoading).setVisibility(View.VISIBLE);

                        questions = new RelativeLayout(DisplayCourseQuestionsActivity.this);

                        for(DataSnapshot ds : children) {
                            String question = ds.getKey();
                            String votes = "" + ds.getValue(Integer.class);

                            int new_id_button = View.generateViewId();
                            int new_id_text = View.generateViewId();

                            final Button b = new Button(DisplayCourseQuestionsActivity.this);
                            b.setText(question);
                            b.setId(new_id_button);

                            final Button text = new Button(DisplayCourseQuestionsActivity.this);
                            text.setText(votes);
                            text.setId(new_id_text);

                            RelativeLayout.LayoutParams lp_button = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            lp_button.addRule(RelativeLayout.START_OF, text.getId());
                            lp_button.addRule(RelativeLayout.ALIGN_PARENT_START);

                            RelativeLayout.LayoutParams lp_text = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            lp_text.addRule(RelativeLayout.ALIGN_PARENT_END);
                            lp_text.addRule(RelativeLayout.ALIGN_BOTTOM, b.getId());
                            lp_text.addRule(RelativeLayout.ALIGN_TOP, b.getId());

                            if(prev_id_button != -1){
                                Button prev_b = (Button) questions.findViewById(prev_id_button);
                                lp_button.addRule(RelativeLayout.BELOW, prev_b.getId());

                                Button prev_text = (Button) questions.findViewById(prev_id_text);
                                lp_text.addRule(RelativeLayout.BELOW, prev_text.getId());
                            }
                            else {
                                lp_button.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                                lp_text.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                            }

                            b.setLayoutParams(lp_button);
                            text.setLayoutParams(lp_text);
                            questions.addView(b);
                            questions.addView(text);
                            prev_id_button = new_id_button;
                            prev_id_text = new_id_text;
                        }

                        database.child(course).child(DBConstants.COURSE_QUESTIONS).child(today).
                                child(DBConstants.AUTHOR).addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Iterable<DataSnapshot> authors = dataSnapshot.getChildren();

                                        for(DataSnapshot ds : authors) {
                                            String author = ds.getKey();

                                            for(DataSnapshot dsq : ds.getChildren()) {
                                                String question = dsq.getKey();
                                                for (int i = 0; i < questions.getChildCount(); i += 2) {
                                                    Button b = (Button) questions.getChildAt(i);
                                                    if (b.getText().toString().equals(question)) {
                                                        b.setHint(author);
                                                        b.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                Button b = (Button) view;
                                                                Toast.makeText(DisplayCourseQuestionsActivity.this
                                                                        , "Author is " + b.getHint(),
                                                                        Toast.LENGTH_SHORT).show();

                                                            }
                                                        });
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        findViewById(R.id.questionsLoading).setVisibility(View.GONE);
                                        rl.addView(questions);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                }
                        );
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setDay() {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        today = df.format(new Date());
    }
}
