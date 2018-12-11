package malvolyo.teachingassistant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuizActivity extends AppCompatActivity {
    private String firebaseUser;
    private String course;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Intent intent = getIntent();
        course = intent.getStringExtra(Constants.COURSE);
        firebaseUser = intent.getStringExtra(Constants.USERNAME);

        database = FirebaseDatabase.getInstance().getReference();

        database.child(course).child(DBConstants.QUIZ).child(DBConstants.ACTIVE_QUIZ).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                findViewById(R.id.noQuiz).setVisibility(View.GONE);
                findViewById(R.id.quizLayout).setVisibility(View.GONE);
                findViewById(R.id.quizLoading).setVisibility(View.VISIBLE);
                String s;

                if (dataSnapshot.child(DBConstants.QUIZ_QUESTION).getChildrenCount() == 0)
                    s = "0";
                else
                    s = dataSnapshot.child(DBConstants.QUIZ_QUESTION).getChildren().
                            iterator().next().getKey();
                boolean answered = false;

                Iterable<DataSnapshot> users = dataSnapshot.child(DBConstants.ANSWERED).getChildren();

                for (DataSnapshot ds : users) {
                    if (firebaseUser.equals("" + ds.getKey())) {
                        answered = true;
                        break;
                    }
                }

                if (s.equals("0") || answered) {
                    findViewById(R.id.quizLoading).setVisibility(View.GONE);
                    findViewById(R.id.quizLayout).setVisibility(View.GONE);
                    findViewById(R.id.noQuiz).setVisibility(View.VISIBLE);
                }
                else {
                    EditText question = (EditText) findViewById(R.id.quizText);
                    question.setText(s);

                    Iterable<DataSnapshot> children = dataSnapshot.child(DBConstants.QUIZ_QUESTION).
                            child(s).getChildren();
                    RadioGroup rg = (RadioGroup) findViewById(R.id.quizAnswers);
                    rg.removeAllViews();

                    for (DataSnapshot ds : children) {
                        String ans = ds.getKey().toString();
                        RadioButton rb = new RadioButton(QuizActivity.this);
                        rb.setText(ans);
                        rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                        String correct = "" + ds.getValue();
                        rb.setHint(correct);
                        rg.addView(rb);
                    }
                    findViewById(R.id.quizLoading).setVisibility(View.GONE);
                    findViewById(R.id.noQuiz).setVisibility(View.GONE);
                    findViewById(R.id.quizLayout).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onClick(View view) {
        RadioGroup rg = (RadioGroup) findViewById(R.id.quizAnswers);

        if (rg.getCheckedRadioButtonId() == -1) {
            Toast.makeText(QuizActivity.this, "Select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
        String id = rb.getHint().toString();

        findViewById(R.id.quizLayout).setVisibility(View.GONE);

        if (id.equals(Constants.CORRECT_ANS))
            Toast.makeText(QuizActivity.this, "Correct answer " + firebaseUser, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(QuizActivity.this, "Wrong answer " + firebaseUser, Toast.LENGTH_SHORT).show();

        database.child(course).child(DBConstants.QUIZ).child(DBConstants.ACTIVE_QUIZ).
                child(DBConstants.ANSWERED).child(firebaseUser).child(id).setValue(rb.getText().toString());
    }
}
