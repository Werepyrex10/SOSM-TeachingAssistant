package malvolyo.teachingassistant;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class DisplayCourseQuestionsActivity extends AppCompatActivity {

    private DatabaseReference database;
    private String firebaseUser;

    private RelativeLayout rl;
    private RelativeLayout questions;
    private ArrayList<String> voted;
    private String today;

    private String course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_course_questions);

        initData();
        manageQuestionLayout();
    }

    private void manageQuestionLayout() {
        database.child(course).child(DBConstants.COURSE_QUESTIONS).
                child(today).child(DBConstants.QUESTIONS).
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

                    text.setOnClickListener(new OnClickListenerColor(question,
                            voted, Integer.parseInt(text.getText().toString()),
                            database.child(course).child(DBConstants.COURSE_QUESTIONS).
                                    child(today).child(DBConstants.QUESTIONS).child(question),
                            firebaseUser, course));

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

                    if(voted.contains(question))
                        text.getBackground().setColorFilter
                                (Color.parseColor("#3B5998"), PorterDuff.Mode.MULTIPLY);

                    b.setLayoutParams(lp_button);
                    text.setLayoutParams(lp_text);
                    questions.addView(b);
                    questions.addView(text);
                    prev_id_button = new_id_button;
                    prev_id_text = new_id_text;
                }

                findPreviousVotedAndColorButtons(
                        database.child(course).child(DBConstants.COURSE_QUESTIONS).child(today).
                                child(DBConstants.VOTED).child(firebaseUser));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    private void initData() {
        firebaseUser = getIntent().getStringExtra(Constants.USERNAME);
        course = getIntent().getStringExtra(Constants.COURSE);

        database = FirebaseDatabase.getInstance().getReference();
        today = Utils.getDay();

        questions = new RelativeLayout(this);
        rl =(RelativeLayout) findViewById(R.id.activity_display_course_questions);

        voted = new ArrayList();

    }

    private void findPreviousVotedAndColorButtons(DatabaseReference dr) {
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for(DataSnapshot ds : children) {
                    for(int i = 0 ; i < questions.getChildCount(); i += 2) {
                        voted.add(ds.getKey());
                        Button q = (Button) questions.getChildAt(i);
                        Button t = (Button) questions.getChildAt(i + 1);

                        if(ds.getKey().equals(q.getText())) {
                            t.getBackground().setColorFilter
                                    (Color.parseColor("#3B5998"), PorterDuff.Mode.MULTIPLY);
                        }
                    }
                }

                manageSupportActionBar();
                findViewById(R.id.questionsLoading).setVisibility(View.GONE);
                rl.addView(questions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void manageSupportActionBar() {
        ActionBar ab = getSupportActionBar();

        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowCustomEnabled(true);

        Button b = new Button(DisplayCourseQuestionsActivity.this);
        b.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                ab.getHeight()));
        b.getBackground().setColorFilter
                (Color.parseColor("#3B5998"), PorterDuff.Mode.MULTIPLY);

        b.setText("Add new question");
        b.setTextColor(Color.parseColor("#FFFFFF"));
        b.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisplayCourseQuestionsActivity.this, SubmitQuestionActivity.class);
                intent.putExtra(Constants.USERNAME, firebaseUser);
                intent.putExtra(Constants.COURSE, course);
                startActivity(intent);
            }
        });

        ab.setCustomView(b);
    }

    /**
     * Created by malvo.
     */

    public static class OnClickListenerColor implements View.OnClickListener {
        DatabaseReference dr;
        String question;
        ArrayList<String> voted;
        Integer value;
        String user;
        String course;

        OnClickListenerColor(String question, ArrayList<String> voted,
                             Integer value, DatabaseReference dr,
                             String user, String course) {
            this.user = user;
            this.voted = voted;
            this.question = question;
            this.dr = dr;
            this.value = value;
            this.course = course;
        }

        @Override
        public void onClick(View view) {
            if(!voted.contains(question)) {
                voted.add(question);

                DatabaseReference dr_voted = FirebaseDatabase.getInstance().getReference();
                dr_voted.child(course).child(DBConstants.COURSE_QUESTIONS).child(Utils.getDay()).
                        child(DBConstants.VOTED).child(user).child(question).setValue(0);

                dr.setValue(value + 1);
            }
        }
    }
}
