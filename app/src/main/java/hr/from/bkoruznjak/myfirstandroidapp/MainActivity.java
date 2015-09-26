package hr.from.bkoruznjak.myfirstandroidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import hr.from.bkoruznjak.myfirstandroidapp.db.DatabaseHandler;
import hr.from.bkoruznjak.myfirstandroidapp.db.Riddle;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "push button called";
    public static final String MESSAGE = "hr.from.bkoruznjak.MESSAGE";
    private List<Riddle> riddleList;
    private String riddleText;
    private String riddleAnwser;
    private String riddleId;
    private int riddleViewCount;
    private boolean showAnwser;
    private int riddleNumber = 0;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reading all riddles
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        Log.d(TAG, "Reading all riddles..");
        riddleList = dbHandler.getAllRiddles();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the hamburger_menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
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


    @Override
    public void onBackPressed() {
        System.exit(0);

    }

    public void buttonNextRiddle(View view) {
        TextView riddleTextView = (TextView) findViewById(R.id.riddle_text);
        TextView riddleAnwserTextView = (TextView) findViewById(R.id.riddle_anwser);
        if (riddleList.isEmpty()) {
            riddleTextView.setText("There appears to be no riddles");
        } else {
            //reach end of list condition
            if (riddleNumber == riddleList.size() - 1) {
                riddleNumber = 0;
            } else {
                riddleNumber++;
            }
            //hide Anwser again if shown
            if (showAnwser) {
                showAnwser = !showAnwser;
                riddleAnwserTextView.setText("");
            }
            //increase counter after fetched riddle
            Riddle riddle = riddleList.get(riddleNumber);
            riddleText = riddle.getRiddleText();
            riddleAnwser = riddle.getRiddleAnwser();
            riddleId = riddle.getId();
            riddleViewCount = riddle.getViewCount();
            riddleTextView.setText(riddleText);
        }
    }

    public void buttonPreviousRiddle(View view) {
        TextView riddleTextView = (TextView) findViewById(R.id.riddle_text);
        TextView riddleAnwserTextView = (TextView) findViewById(R.id.riddle_anwser);
        if (riddleList.isEmpty()) {
            riddleTextView.setText("There appears to be no riddles");
        } else {
            //reach end of list condition
            if (riddleNumber == 0) {
                riddleNumber = riddleList.size() - 1;
            } else {
                riddleNumber--;
            }
            //hide Anwser again if shown
            if (showAnwser) {
                showAnwser = !showAnwser;
                riddleAnwserTextView.setText("");
            }
            //decrease counter after fetched riddle
            Riddle riddle = riddleList.get(riddleNumber);
            riddleText = riddle.getRiddleText();
            riddleAnwser = riddle.getRiddleAnwser();
            riddleId = riddle.getId();
            riddleViewCount = riddle.getViewCount();
            riddleTextView.setText(riddleText);
        }
    }

    public void buttonToggleAnwser(View view) {
        showAnwser = !showAnwser;
        TextView riddleAnwserTextView = (TextView) findViewById(R.id.riddle_anwser);
        if (showAnwser) {
            riddleAnwserTextView.setText(riddleAnwser);
        } else {
            riddleAnwserTextView.setText("");
        }
    }


}

