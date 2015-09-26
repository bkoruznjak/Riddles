package hr.from.bkoruznjak.myfirstandroidapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Random;

import hr.from.bkoruznjak.myfirstandroidapp.db.DatabaseHandler;
import hr.from.bkoruznjak.myfirstandroidapp.db.Riddle;
import hr.from.bkoruznjak.myfirstandroidapp.util.RiddleUpdater;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACT";
    public static final String MESSAGE = "hr.from.bkoruznjak.MESSAGE";
    private List<Riddle> riddleList;
    private String riddleText;
    private String riddleAnwser;
    private String riddleId;
    private int riddleViewCount;
    private int numberOfRiddles;
    private boolean showAnwser;
    private int riddleNumber = 0;
    private int counter = 0;
    private DatabaseHandler dbHandler;
    private TextView riddleTextView;
    private TextView riddleAnwserTextView;
    private TextView riddleNumberTextView;
    private TextView riddleViewCountTextView;
    private CheckBox riddleFavoriteCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHandler = new DatabaseHandler(this);
        riddleTextView = (TextView) findViewById(R.id.riddle_text);
        riddleAnwserTextView = (TextView) findViewById(R.id.riddle_anwser);
        riddleNumberTextView = (TextView) findViewById(R.id.id_riddle_number);
        riddleViewCountTextView = (TextView) findViewById(R.id.id_riddle_view_count);
        riddleFavoriteCheckbox = (CheckBox) findViewById(R.id.id_checkbox_riddle_favorite);

        riddleList = dbHandler.getAllRiddles();
        numberOfRiddles = riddleList.size();
        addListenerOnFavoriteCheckbox();

        if (riddleList.isEmpty()) {
            riddleTextView.setText("There appears to be no riddles");
        } else {
            //reach end of list condition
            Random randomNumber = new Random();
            riddleNumber = randomNumber.nextInt(riddleList.size() - 1);
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
            riddleNumberTextView.setText(riddle.getId() + " of " + numberOfRiddles);
            riddleViewCount++;
            riddleViewCountTextView.setText("view count:" + riddleViewCount);

            if (riddle.getFavorite() == 1 && !riddleFavoriteCheckbox.isChecked()) {
                //checkbox must be checked
                riddleFavoriteCheckbox.setChecked(true);
            } else if (riddle.getFavorite() == 0 && riddleFavoriteCheckbox.isChecked()) {
                //checkbox can't be checked
                riddleFavoriteCheckbox.setChecked(false);
            }
            riddle.setViewCount(riddleViewCount);
            //dbHandler.updateRiddle(riddle);
            (new Thread(new RiddleUpdater(this, riddle))).start();

        }

    }

    private void addListenerOnFavoriteCheckbox() {
        CheckBox riddleFavoriteCheckbox = (CheckBox) findViewById(R.id.id_checkbox_riddle_favorite);
        riddleFavoriteCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    Riddle riddle = riddleList.get(riddleNumber);
                    riddle.setFavorite(1);
                    //dbHandler.updateRiddle(riddle);
                    (new Thread(new RiddleUpdater(MainActivity.this, riddle))).start();
                    addToFavoritesToast(MainActivity.this, "Riddle added to favorites");
                } else {
                    Riddle riddle = riddleList.get(riddleNumber);
                    riddle.setFavorite(0);
                    //dbHandler.updateRiddle(riddle);
                    (new Thread(new RiddleUpdater(MainActivity.this, riddle))).start();
                    addToFavoritesToast(MainActivity.this, "Riddle removed from favorites");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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
            riddleNumberTextView.setText(riddle.getId() + " of " + numberOfRiddles);
            riddleViewCount++;
            riddleViewCountTextView.setText("view count:" + riddleViewCount);

            if (riddle.getFavorite() == 1 && !riddleFavoriteCheckbox.isChecked()) {
                //checkbox must be checked
                riddleFavoriteCheckbox.setChecked(true);
            } else if (riddle.getFavorite() == 0 && riddleFavoriteCheckbox.isChecked()) {
                //checkbox can't be checked
                riddleFavoriteCheckbox.setChecked(false);
            }

            riddle.setViewCount(riddleViewCount);
            //dbHandler.updateRiddle(riddle);
            (new Thread(new RiddleUpdater(this, riddle))).start();
        }
    }

    public void buttonPreviousRiddle(View view) {
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
            riddleNumberTextView.setText(riddle.getId() + " of " + numberOfRiddles);
            riddleViewCount++;
            riddleViewCountTextView.setText("view count:" + riddleViewCount);

            if (riddle.getFavorite() == 1 && !riddleFavoriteCheckbox.isChecked()) {
                //checkbox must be checked
                riddleFavoriteCheckbox.setChecked(true);
            } else if (riddle.getFavorite() == 0 && riddleFavoriteCheckbox.isChecked()) {
                //checkbox can't be checked
                riddleFavoriteCheckbox.setChecked(false);
            }

            riddle.setViewCount(riddleViewCount);
            //dbHandler.updateRiddle(riddle);
            (new Thread(new RiddleUpdater(this, riddle))).start();
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

    public void addToFavoritesToast(Context context, String message) {
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }
}

