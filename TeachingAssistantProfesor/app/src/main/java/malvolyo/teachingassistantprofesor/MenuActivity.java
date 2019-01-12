package malvolyo.teachingassistantprofesor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends AppCompatActivity {

    private String user;
    private String course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        user = intent.getStringExtra(Constants.USERNAME);
        course = intent.getStringExtra(Constants.COURSE);

        getSupportActionBar().setTitle("Hello " + user);
    }

    public void questions(View view) {
        if(Utils.isNetworkAvailable(this)) {
            Intent intent = new Intent(this, DisplayCourseQuestionsActivity.class);
            intent.putExtra(Constants.USERNAME, user);
            intent.putExtra(Constants.COURSE, course);
            startActivity(intent);
        }
        else {
            Utils.networkUnavailable(this);
        }
    }

    public void quiz(View view) {
        if(Utils.isNetworkAvailable(this)) {
            Intent intent = new Intent(this, QuizMenuActivity.class);
            intent.putExtra(Constants.USERNAME, user);
            intent.putExtra(Constants.COURSE, course);
            startActivity(intent);
        }
    }
}
