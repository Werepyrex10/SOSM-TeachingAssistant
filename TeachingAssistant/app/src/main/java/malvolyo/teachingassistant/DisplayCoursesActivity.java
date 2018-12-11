package malvolyo.teachingassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DisplayCoursesActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private RelativeLayout rl;
    private String user;
    private RelativeLayout buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_courses);

        user = getIntent().getStringExtra(Constants.USERNAME);
        rl = (RelativeLayout) findViewById(R.id.activity_display_courses);

        database = FirebaseDatabase.getInstance();
        DatabaseReference courses = database.getReference();

        buttons = new RelativeLayout(this);

        courses.child(DBConstants.COURSES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get all of the children of Courses
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                int prev_id = -1;

                rl.removeView(buttons);
                findViewById(R.id.coursesLoading).setVisibility(View.VISIBLE);

                buttons = new RelativeLayout(DisplayCoursesActivity.this);

                for(DataSnapshot ds : children) {
                    String course = ds.getKey();
                    int new_id = View.generateViewId();

                    Button b = new Button(DisplayCoursesActivity.this);
                    b.setText(course);
                    b.setId(new_id);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(Utils.isNetworkAvailable(DisplayCoursesActivity.this)) {
                                Intent intent = new Intent(DisplayCoursesActivity.this, MenuActivity.class);
                                intent.putExtra(Constants.USERNAME, user);
                                Button b = (Button) findViewById(view.getId());
                                intent.putExtra(Constants.COURSE, b.getText().toString());
                                startActivity(intent);
                            }
                            else {
                                Utils.networkUnavailable(DisplayCoursesActivity.this);
                            }
                        }
                    });

                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);

                    if(prev_id != -1){
                        Button prev_b = (Button) buttons.findViewById(prev_id);
                        lp.addRule(RelativeLayout.BELOW, prev_b.getId());
                        lp.addRule(RelativeLayout.ALIGN_END, prev_b.getId());
                    }
                    else {
                        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    }

                    b.setLayoutParams(lp);
                    buttons.addView(b);
                    prev_id = new_id;
                }
                findViewById(R.id.coursesLoading).setVisibility(View.GONE);
                rl.addView(buttons);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                rl.removeView(buttons);
            }
        });
    }
}
