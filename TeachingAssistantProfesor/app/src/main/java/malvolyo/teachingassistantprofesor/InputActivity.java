package malvolyo.teachingassistantprofesor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
    }

    public void onClick(View view) {
        Button b = (Button) view;

        switch(b.getId()) {
            case R.id.cancel:
                setResult(RESULT_CANCELED);
                break;
            case R.id.done:
                Intent intent = new Intent();
                EditText text = (EditText) findViewById(R.id.input);

                if (intent.hasExtra(Constants.INPUT))
                    intent.removeExtra(Constants.INPUT);

                intent.putExtra(Constants.INPUT, text.getText().toString());
                setResult(RESULT_OK, intent);
                break;
        }

        finish();
    }
}
