package malvolyo.teachingassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SubmitQuestionActivity extends AppCompatActivity {
    private final int questionLength = 5;

    private DatabaseReference database;
    private String firebaseUser;
    private String course;
    private String today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_question);

        initData();
        manageAddQuestion();
    }

    private void initData() {
        firebaseUser = getIntent().getStringExtra(Constants.USERNAME);
        course = getIntent().getStringExtra(Constants.COURSE);
        today = Utils.getDay();
        database = FirebaseDatabase.getInstance().getReference();
    }

    private void manageAddQuestion() {

        Button sendQ = (Button) findViewById(R.id.sendQuestion);
        Button discardQ = (Button) findViewById(R.id.discardQuestion);
        final EditText questionText = (EditText) findViewById(R.id.quizLayout);


        sendQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(questionText.getText().length() < questionLength) {
                    Toast.makeText(SubmitQuestionActivity.this, "Text too short", Toast.LENGTH_SHORT).show();
                    return;
                }

                String qt = questionText.getText().toString();

                database.child(course).child(DBConstants.COURSE_QUESTIONS).child(today).
                        child(DBConstants.VOTED).child(firebaseUser).child(qt).setValue(0);
                database.child(course).child(DBConstants.COURSE_QUESTIONS).child(today).
                         child(DBConstants.AUTHOR).child(firebaseUser).child(qt).setValue(0);
                database.child(course).child(DBConstants.COURSE_QUESTIONS).child(today).
                        child(DBConstants.QUESTIONS).child(qt).setValue(0);
                finish();
            }
        });

        discardQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
