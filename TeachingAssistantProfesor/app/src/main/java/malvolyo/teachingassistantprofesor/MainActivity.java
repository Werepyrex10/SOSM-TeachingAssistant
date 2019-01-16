package malvolyo.teachingassistantprofesor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private final int userLength = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View view) {
        EditText username = (EditText) findViewById(R.id.Username);
        String user = username.getText().toString();

        if(user.length() < userLength) {
            Toast.makeText(this, "Username too short", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, DisplayCoursesActivity.class);
        intent.putExtra(Constants.USERNAME, user);
        startActivity(intent);
    }
}
