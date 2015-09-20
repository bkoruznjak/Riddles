package hr.from.bkoruznjak.myfirstandroidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "push button called";
    public static final String MESSAGE = "hr.from.bkoruznjak.MESSAGE";
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonMethodOne(View view) {
        counter++;
        Log.v(TAG, "count:" + counter);
        Intent intentChangeView = new Intent(this, DisplayMessageActivity.class);
        EditText editTextOfMainActivity = (EditText) findViewById(R.id.welcome_text);
        String someTextToSend = editTextOfMainActivity.getText().toString();
        intentChangeView.putExtra(MESSAGE, someTextToSend);
        startActivity(intentChangeView);
    }

    @Override
    public void onBackPressed() {
        System.exit(0);

    }


}

