package hr.from.bkoruznjak.myfirstandroidapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import hr.from.bkoruznjak.myfirstandroidapp.util.SimpleGestureFilter;
import hr.from.bkoruznjak.myfirstandroidapp.util.SimpleGestureFilter.SimpleGestureListener;

public class MainActivity extends AppCompatActivity implements SimpleGestureListener {

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
    private SimpleGestureFilter detector;
    private Riddle onCreateRiddle;

    /*
     * @desc if there was no instance generates new riddle
     * if instance exists just changes layout on create and
     * keeps the settings
     *
     * @param saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHandler = new DatabaseHandler(this);
        //detect touched area
        detector = new SimpleGestureFilter(this, this);
        //set all views
        riddleTextView = (TextView) findViewById(R.id.riddle_text);
        riddleAnwserTextView = (TextView) findViewById(R.id.riddle_anwser);
        riddleNumberTextView = (TextView) findViewById(R.id.id_riddle_number);
        riddleViewCountTextView = (TextView) findViewById(R.id.id_riddle_view_count);
        riddleFavoriteCheckbox = (CheckBox) findViewById(R.id.id_checkbox_riddle_favorite);
        if (savedInstanceState != null) {
            Log.d(TAG, "FOUND SAVED INSTANCE STATE");
            onCreateRiddle = (Riddle) savedInstanceState.getSerializable("onCreateRiddle");
            riddleNumber = savedInstanceState.getInt("riddleNumber");
            riddleList = dbHandler.getAllRiddles();
            numberOfRiddles = riddleList.size();
            addListenerOnFavoriteCheckbox();
            setTextFields(onCreateRiddle);
            favoriteStatusHandler(onCreateRiddle);
            onCreateRiddle.setViewCount(riddleViewCount);
        } else {
            Log.d(TAG, "NEW INSTANCE STATE");
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
                onCreateRiddle = riddleList.get(riddleNumber);
                setTextFields(onCreateRiddle);

                favoriteStatusHandler(onCreateRiddle);
                onCreateRiddle.setViewCount(riddleViewCount);
                //dbHandler.updateRiddle(riddle);
                (new Thread(new RiddleUpdater(this, onCreateRiddle))).start();
            }
        }
    }

    /*
     * @desc saves the current riddle in the instance state
     *
     * @param saved instance state
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putSerializable("onCreateRiddle", onCreateRiddle);
        savedInstanceState.putInt("riddleNumber", riddleNumber);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /*
     * @desc sets all the activity text fields based on the riddle values
     *
     * @param riddle object
     */
    private void setTextFields(Riddle riddle) {
        riddleText = riddle.getRiddleText();
        riddleAnwser = riddle.getRiddleAnwser();
        riddleId = riddle.getId();
        riddleViewCount = riddle.getViewCount();
        riddleTextView.setText(riddleText);
        riddleNumberTextView.setText(riddle.getId() + " of " + numberOfRiddles);
        riddleViewCount++;
        riddleViewCountTextView.setText("view count:" + riddleViewCount);
    }

    /*
     * @desc updates riddle object upon tap on favorites checkbox
     */
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


    /*
     * @desc kill the application on back pressed
     */
    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    /*
     * @desc handles the next riddle swipe
     */
    public void nextRiddle() {
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
            onCreateRiddle = riddleList.get(riddleNumber);
            setTextFields(onCreateRiddle);

            favoriteStatusHandler(onCreateRiddle);

            onCreateRiddle.setViewCount(riddleViewCount);
            //dbHandler.updateRiddle(riddle);
            (new Thread(new RiddleUpdater(this, onCreateRiddle))).start();
        }
    }

    /*
     * @desc handles the previous riddle swipe
     */
    public void previousRiddle() {
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
            onCreateRiddle = riddleList.get(riddleNumber);
            setTextFields(onCreateRiddle);

            favoriteStatusHandler(onCreateRiddle);

            onCreateRiddle.setViewCount(riddleViewCount);
            //dbHandler.updateRiddle(riddle);
            (new Thread(new RiddleUpdater(this, onCreateRiddle))).start();
        }
    }

    /*
     * @desc shows or hides the anwser to the riddle
     *
     * @param clicked view
     */
    public void buttonToggleAnwser(View view) {
        showAnwser = !showAnwser;
        TextView riddleAnwserTextView = (TextView) findViewById(R.id.riddle_anwser);
        if (showAnwser) {
            riddleAnwserTextView.setText(riddleAnwser);
        } else {
            riddleAnwserTextView.setText("");
        }
    }


    /*
     * @desc checks or unchecks the favorite checkbox based
     * on the riddle status
     *
     * @param riddle object
     */
    private void favoriteStatusHandler(Riddle riddle) {
        if (riddle.getFavorite() == 1 && !riddleFavoriteCheckbox.isChecked()) {
            //checkbox must be checked
            riddleFavoriteCheckbox.setChecked(true);
        } else if (riddle.getFavorite() == 0 && riddleFavoriteCheckbox.isChecked()) {
            //checkbox can't be checked
            riddleFavoriteCheckbox.setChecked(false);
        }
    }


    /*
     * @desc handles the Toast display when favorites checkbox is clicked
     *
     * @param application context
     * @param message to toast display
     */
    public void addToFavoritesToast(Context context, String message) {
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.BOTTOM, 0, 65);
        toast.show();
    }

    /*
     * @desc handles touch events
     *
     * @param screen motion events swipes, touches
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    /*
     * @desc handles swipe events
     *
     * @param direction of a swipe
     */
    @Override
    public void onSwipe(int direction) {
        String str = "";
        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT:
                str = "Swipe Right";
                Log.d(TAG, str);
                previousRiddle();
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                str = "Swipe Left";
                Log.d(TAG, str);
                nextRiddle();
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                str = "Swipe Down";
                Log.d(TAG, str);
                break;
            case SimpleGestureFilter.SWIPE_UP:
                str = "Swipe Up";
                Log.d(TAG, str);
                break;

        }
        //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {
        //Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }
}

