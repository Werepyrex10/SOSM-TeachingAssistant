package malvolyo.teachingassistant;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final int userLength = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d("Entry point of app", "Hello World. This is my logging message");
    }

    /*Called on pressing the login button*/
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
