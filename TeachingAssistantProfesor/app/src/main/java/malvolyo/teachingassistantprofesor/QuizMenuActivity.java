package malvolyo.teachingassistantprofesor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class QuizMenuActivity extends AppCompatActivity {
    private String user;
    private String course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_menu);

        Intent intent = getIntent();
        user = intent.getStringExtra(Constants.USERNAME);
        course = intent.getStringExtra(Constants.COURSE);
    }

    public void onClick(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.activeQuiz:
                intent = new Intent(this, ActiveQuizActivity.class);
                intent.putExtra(Constants.COURSE, course);
                intent.putExtra(Constants.USERNAME, user);
                startActivity(intent);
                break;
            case R.id.addQuiz:
                intent = new Intent(this, AddQuizActivity.class);
                intent.putExtra(Constants.COURSE, course);
                intent.putExtra(Constants.USERNAME, user);
                startActivity(intent);
                break;
            case R.id.historyQuiz:
                intent = new Intent(this, HistoryQuizSelectActivity.class);
                intent.putExtra(Constants.COURSE, course);
                intent.putExtra(Constants.USERNAME, user);
                startActivity(intent);
                break;
        }
    }
}
