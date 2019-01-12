package malvolyo.teachingassistantprofesor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistoryQuizSelectActivity extends AppCompatActivity {
    String course;
    String user;
    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_quiz_select);

        Intent intent = getIntent();
        course = intent.getStringExtra(Constants.COURSE);
        user = intent.getStringExtra(Constants.USERNAME);
        ll = (LinearLayout) findViewById(R.id.history_layout);

        FirebaseDatabase.getInstance().getReference().child(course).child(DBConstants.QUIZ).
                child(DBConstants.HISTORY_QUIZ).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ll.removeAllViews();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String question = ds.child(DBConstants.QUIZ_QUESTION).
                            getChildren().iterator().next().getKey();

                    Button b = new Button(HistoryQuizSelectActivity.this);
                    b.setText(question);
                    b.setId(Integer.parseInt(ds.getKey()));
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(HistoryQuizSelectActivity.this,
                                    HistoryQuizActivity.class);

                            intent.putExtra(Constants.USERNAME, user);
                            intent.putExtra(Constants.COURSE, course);
                            intent.putExtra(Constants.HISTORY_ID, "" + view.getId());
                            startActivity(intent);
                        }
                    });

                    ll.addView(b);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
