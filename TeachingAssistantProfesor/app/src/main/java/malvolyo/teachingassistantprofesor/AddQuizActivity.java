package malvolyo.teachingassistantprofesor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddQuizActivity extends AppCompatActivity {
    String course;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz);
        Intent intent = getIntent();
        course = intent.getStringExtra(Constants.COURSE);
        user = intent.getStringExtra(Constants.USERNAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == Constants.INPUT_CODE) {
            if (resultCode == RESULT_OK) {
                String ans = intent.getStringExtra(Constants.INPUT);
                RadioGroup rg = (RadioGroup) findViewById(R.id.quizAnswers);
                RadioButton rb = new RadioButton(AddQuizActivity.this);
                rb.setText(ans);
                rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                rb.setId(rg.getChildCount());
                rb.setHint(rb.getId() + "");
                rg.addView(rb);
            }
        }
    }

    public void onClickAnswer(View view) {
        Button b = (Button) view;

        switch(b.getId()) {
            case R.id.addAnswer:
                Intent intent = new Intent(this, InputActivity.class);
                startActivityForResult(intent, Constants.INPUT_CODE);
                break;
            case R.id.removeAnswer:
                RadioGroup rg = (RadioGroup) findViewById(R.id.quizAnswers);
                if (rg.getChildCount() > 0) {
                    rg.removeView(findViewById(rg.getChildCount() - 1));
                }
                else {
                    Toast.makeText(this, "You have no answer to remove", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void onClickQuiz(View view) {
        Button b = (Button) view;

        switch(b.getId()) {
            case R.id.discardQuiz:
                finish();
                break;
            case R.id.submitQuiz:
                boolean found = false;
                RadioGroup rg = (RadioGroup) findViewById(R.id.quizAnswers);

                EditText question = (EditText) findViewById(R.id.quizQuestion);

                if (question.getText().toString().length() == 0) {
                    Toast.makeText(AddQuizActivity.this, "Add quiz question",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(rg.getChildCount() < 2) {
                    Toast.makeText(AddQuizActivity.this, "Please make a valid quiz",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                for(int i = 0 ; i < rg.getChildCount(); i++) {
                    RadioButton rb = (RadioButton) rg.getChildAt(i);

                    if(rb.isChecked()) {
                        rb.setHint(Constants.CORRECT_ANS);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(AddQuizActivity.this, "Please select the correct answer",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                findViewById(R.id.quiz_layout).setVisibility(View.GONE);
                findViewById(R.id.answer_layout).setVisibility(View.GONE);
                findViewById(R.id.discardQuiz).setVisibility(View.GONE);
                findViewById(R.id.submitQuiz).setVisibility(View.GONE);
                findViewById(R.id.loadingSubmit).setVisibility(View.VISIBLE);

                FirebaseDatabase.getInstance().getReference().child(course).
                    child(DBConstants.QUIZ).child(DBConstants.ACTIVE_QUIZ).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() == 0)
                                addNewActiveQuiz();
                            else
                                copyDatabaseValue(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                break;
        }
    }

    private void copyDatabaseValue(final DataSnapshot activeQuizSnapshot) {
        FirebaseDatabase.getInstance().getReference().child(course).child(DBConstants.QUIZ).
            child(DBConstants.HISTORY_QUIZ).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String path = "" + dataSnapshot.getChildrenCount();

                        FirebaseDatabase.getInstance().getReference().child(course).
                                child(DBConstants.QUIZ).child(DBConstants.HISTORY_QUIZ).
                                child(path).setValue(activeQuizSnapshot.getValue(),
                                        new DatabaseReference.CompletionListener()
                                        {
                                            @Override
                                            public void onComplete(DatabaseError databaseError,
                                                   DatabaseReference databaseReference){
                                                removeActiveQuiz();
                                            }
                                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void removeActiveQuiz() {
        FirebaseDatabase.getInstance().getReference().child(course).child(DBConstants.QUIZ).
            child(DBConstants.ACTIVE_QUIZ).removeValue(
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError,
                           DatabaseReference databaseReference) {
                        addNewActiveQuiz();
                    }
                }
        );
    }

    private void addNewActiveQuiz() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().
                child(course).child(DBConstants.QUIZ).child(DBConstants.ACTIVE_QUIZ);
        String answer;

        EditText quizQuestion = (EditText)findViewById(R.id.quizQuestion);
        String question = quizQuestion.getText().toString();

        database.child(DBConstants.AUTHOR).setValue(user);
        database = database.child(DBConstants.QUIZ_QUESTION).child(question);

        RadioGroup rg = (RadioGroup) findViewById(R.id.quizAnswers);

        for(int i = 0; i < rg.getChildCount() - 1; i++) {
            RadioButton rb = (RadioButton)rg.getChildAt(i);
            answer = rb.getText().toString();
            database.child(answer).setValue(rb.getHint().toString());
        }
        RadioButton rb = (RadioButton)rg.getChildAt(rg.getChildCount() - 1);

        answer = rb.getText().toString();

        database.child(answer).setValue(rb.getHint().toString(),
            new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError,
                       DatabaseReference databaseReference) {
                    Toast.makeText(AddQuizActivity.this, "Submitted new quiz", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
            }
        );
    }
}
