package hr.from.bkoruznjak.myfirstandroidapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

import hr.from.bkoruznjak.myfirstandroidapp.db.DatabaseHandler;
import hr.from.bkoruznjak.myfirstandroidapp.db.Riddle;
import hr.from.bkoruznjak.myfirstandroidapp.db.enums.RiddleParameterEnum;
import hr.from.bkoruznjak.myfirstandroidapp.util.RiddleUpdater;
import hr.from.bkoruznjak.myfirstandroidapp.util.SimpleGestureFilter;
import hr.from.bkoruznjak.myfirstandroidapp.util.SimpleGestureFilter.SimpleGestureListener;

public class MainActivityBac extends AppCompatActivity implements SimpleGestureListener {

    View.OnClickListener addFavoriteOnClickListener;
    View.OnClickListener removeFavoriteOnClickListener;
    public static final String TAG = "MY_R_APP";
    public static final String MESSAGE = "hr.from.bkoruznjak.MESSAGE";
    private List<Riddle> riddleList;
    private String riddleAnwser;
    private int riddleViewCount;
    private int numberOfRiddles;
    private boolean showAnwser;
    private int riddleNumber = 0;
    private String riddleId;
    private DatabaseHandler dbHandler;
    private TextView riddleTextView;
    private TextView riddleAnwserTextView;
    private TextView riddleNumberTextView;
    private TextView riddleViewCountTextView;
    private CheckBox riddleFavoriteCheckbox;
    private SimpleGestureFilter detector;
    private Riddle onCreateRiddle;
    private Snackbar favoriteSnackbar;
    private Riddle returnedRiddle;

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
        setContentView(R.layout.activity_main_bac);
        doWork(savedInstanceState);
    }

    /*
     * @desc initialises the main activity
     */
    private void doWork(Bundle savedInstanceState) {
        dbHandler = new DatabaseHandler(this);
        //detect touched area
        detector = new SimpleGestureFilter(this, this);
        //set all views
        Typeface ubuntuRTypeFace = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-R.ttf");
        Typeface ubuntuBTypeFace = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-B.ttf");
        Typeface ubuntuLTypeFace = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-L.ttf");
        //get views
        riddleTextView = (TextView) findViewById(R.id.riddle_text);
        riddleAnwserTextView = (TextView) findViewById(R.id.riddle_anwser);
        riddleNumberTextView = (TextView) findViewById(R.id.id_riddle_number);
        riddleViewCountTextView = (TextView) findViewById(R.id.id_riddle_view_count);
        riddleFavoriteCheckbox = (CheckBox) findViewById(R.id.id_checkbox_riddle_favorite);
        //set fonts
        riddleTextView.setTypeface(ubuntuRTypeFace);
        riddleAnwserTextView.setTypeface(ubuntuLTypeFace);
        riddleNumberTextView.setTypeface(ubuntuLTypeFace);
        riddleViewCountTextView.setTypeface(ubuntuLTypeFace);
        riddleFavoriteCheckbox.setTypeface(ubuntuLTypeFace);
        addListenerOnFavoriteCheckbox();
        addListenerOnFavoriteFab();

        //check for callback riddles from other intents
        returnedRiddle = (Riddle)(this.getIntent().getSerializableExtra("returnToMainRiddle"));
        if (savedInstanceState != null) {
            Log.d(TAG, "FOUND SAVED INSTANCE STATE");
            onCreateRiddle = (Riddle) savedInstanceState.getSerializable("onCreateRiddle");
            riddleNumber = savedInstanceState.getInt("riddleNumber");
            riddleList = dbHandler.getAllRiddles(RiddleParameterEnum.DEFAULT);
            numberOfRiddles = riddleList.size();
            setTextFields(onCreateRiddle);
            favoriteStatusHandler(onCreateRiddle);
            onCreateRiddle.setViewCount(riddleViewCount);
        } else {
            Log.d(TAG, "NEW INSTANCE STATE");
            riddleList = dbHandler.getAllRiddles(RiddleParameterEnum.DEFAULT);
            numberOfRiddles = riddleList.size();
            if (riddleList.isEmpty()) {
                riddleTextView.setText(getResources().getString(R.string.no_riddles));
            } else if(returnedRiddle != null) {
                Log.d(TAG, "got the return riddle sucessfully");
                //hide Anwser again if shown
                if (showAnwser) {
                    showAnwser = !showAnwser;
                    riddleAnwserTextView.setText("");
                }
                //increase counter after fetched riddle
                onCreateRiddle = returnedRiddle;
                riddleId = onCreateRiddle.getId();
                setTextFields(onCreateRiddle);

                favoriteStatusHandler(onCreateRiddle);
                onCreateRiddle.setViewCount(riddleViewCount);
                (new Thread(new RiddleUpdater(this, onCreateRiddle))).start();
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
                (new Thread(new RiddleUpdater(this, onCreateRiddle))).start();
            }
        }
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
                    (new Thread(new RiddleUpdater(MainActivityBac.this, riddle))).start();
                    addToFavoritesToast(MainActivityBac.this, "Riddle added to favorites");
                } else {
                    Riddle riddle = riddleList.get(riddleNumber);
                    riddle.setFavorite(0);
                    (new Thread(new RiddleUpdater(MainActivityBac.this, riddle))).start();
                    addToFavoritesToast(MainActivityBac.this, "Riddle removed from favorites");
                }
            }
        });
    }

    /*
     * @desc adds listener to floating action button
     */
    private void addListenerOnFavoriteFab() {
        FloatingActionButton favoriteFab = (FloatingActionButton) findViewById(R.id.favorite_fab);
        final CheckBox riddleFavoriteCheckbox = (CheckBox) findViewById(R.id.id_checkbox_riddle_favorite);
        favoriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (riddleFavoriteCheckbox.isChecked()) {
                    Log.d(TAG, "if snackbar shown:" + favoriteSnackbar.isShown());
                    if (favoriteSnackbar.isShown()) {
                        favoriteSnackbar.dismiss();
                    } else {
                        favoriteSnackbar.show();
                    }
                } else {
                    Log.d(TAG, "else snackbar shown:" + favoriteSnackbar.isShown());
                    if (favoriteSnackbar.isShown()) {
                        favoriteSnackbar.dismiss();
                    } else {
                        favoriteSnackbar.show();
                    }
                }
            }
        });

        addFavoriteOnClickListener = new View.OnClickListener() {
            final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                    .coordinatorLayout);

            @Override
            public void onClick(View v) {
                riddleFavoriteCheckbox.setChecked(true);
                Riddle riddle = riddleList.get(riddleNumber);
                riddle.setFavorite(1);
                Log.d(TAG, "changing the snackbar to red");
                favoriteSnackbar = Snackbar
                        .make(coordinatorLayout, "This is your favorite riddle", Snackbar.LENGTH_LONG)
                        .setAction("Remove", removeFavoriteOnClickListener);
                favoriteSnackbar.setActionTextColor(Color.RED);
                View snackbarView = favoriteSnackbar.getView();
                snackbarView.setBackgroundColor(Color.DKGRAY);
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.BLUE);
                (new Thread(new RiddleUpdater(MainActivityBac.this, riddle))).start();
            }
        };

        removeFavoriteOnClickListener = new View.OnClickListener() {
            final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                    .coordinatorLayout);

            @Override
            public void onClick(View v) {
                riddleFavoriteCheckbox.setChecked(false);
                Riddle riddle = riddleList.get(riddleNumber);
                riddle.setFavorite(0);
                Log.d(TAG, "changing the snackbar to green");
                favoriteSnackbar = Snackbar
                        .make(coordinatorLayout, "You like this riddle?", Snackbar.LENGTH_LONG)
                        .setAction("Favorite", addFavoriteOnClickListener);
                favoriteSnackbar.setActionTextColor(Color.GREEN);
                View snackbarView = favoriteSnackbar.getView();
                snackbarView.setBackgroundColor(Color.DKGRAY);
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.BLUE);
                (new Thread(new RiddleUpdater(MainActivityBac.this, riddle))).start();
            }
        };
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
        String riddleText;
        riddleText = riddle.getRiddleText();
        riddleAnwser = riddle.getRiddleAnwser();
        riddleViewCount = riddle.getViewCount();
        riddleTextView.setText(riddleText);
        riddleNumberTextView.setText(riddle.getId());
        //riddleNumberTextView.setText(riddleNumber + 1 + " of " + numberOfRiddles);
        riddleViewCount++;
        riddleViewCountTextView.setText(getResources().getString(R.string.view_count) + riddleViewCount);

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        //initialize the snackbar view
        if (riddle.getFavorite() == 1) {
            Log.d(TAG, "riddle is favorite");
            favoriteSnackbar = Snackbar
                    .make(coordinatorLayout, "This is your favorite riddle", Snackbar.LENGTH_LONG)
                    .setAction("Remove", removeFavoriteOnClickListener);
            favoriteSnackbar.setActionTextColor(Color.RED);
            View snackbarView = favoriteSnackbar.getView();
            snackbarView.setBackgroundColor(Color.DKGRAY);
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.BLUE);
        } else if (riddle.getFavorite() == 0) {
            Log.d(TAG, "riddle isn't favorite");
            favoriteSnackbar = Snackbar
                    .make(coordinatorLayout, "You like this riddle?", Snackbar.LENGTH_LONG)
                    .setAction("Favorite", addFavoriteOnClickListener);
            favoriteSnackbar.setActionTextColor(Color.GREEN);
            View snackbarView = favoriteSnackbar.getView();
            snackbarView.setBackgroundColor(Color.DKGRAY);
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.BLUE);
        }
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
        Riddle currentRiddle = onCreateRiddle;
        switch (id) {
            case R.id.action_about:
                Log.d(TAG, "about");
                Intent aboutAppIntent = new Intent(this, AboutAppActivity.class);
                aboutAppIntent.putExtra("onCreateRiddle", currentRiddle);
                startActivity(aboutAppIntent);
                return true;
            case R.id.action_favorites:
                Log.d(TAG, "favs");
                Intent favoritesIntent = new Intent(this, FavoritesAppActivity.class);
                favoritesIntent.putExtra("onCreateRiddle", currentRiddle);
                startActivity(favoritesIntent);
                finish();
                return true;
            case R.id.action_challenge:
                Log.d(TAG, "challenge");
                return true;
            case R.id.action_contribute:
                Log.d(TAG, "cotnributes");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*
     * @desc kill the application on back pressed
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "Exiting app...");
        System.exit(0);
    }

    /*
     * @desc handles the next riddle swipe
     */
    public void nextRiddle() {
        if (riddleList.isEmpty()) {
            riddleTextView.setText(getResources().getString(R.string.no_riddles));
        } else {
            Random randomNumber = new Random();
            riddleNumber = randomNumber.nextInt(riddleList.size() - 1);
            //hide Anwser again if shown
            if (showAnwser) {
                showAnwser = !showAnwser;
                riddleAnwserTextView.setText("");
            }

            //increase counter after fetched riddle
            Log.d(TAG, "riddle number:" + riddleNumber);
            Log.d(TAG, "riddle id:" + onCreateRiddle.getId());
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
            riddleTextView.setText(getResources().getString(R.string.no_riddles));
        } else {
            Random randomNumber = new Random();
            riddleNumber = randomNumber.nextInt(riddleList.size() - 1);
            //hide Anwser again if shown
            if (showAnwser) {
                showAnwser = !showAnwser;
                riddleAnwserTextView.setText("");
            }
            //decrease counter after fetched riddle
            Log.d(TAG, "riddle number:" + riddleNumber);
            Log.d(TAG, "riddle id:" + onCreateRiddle.getId());
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
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
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
        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT:
                Log.d(TAG, "Swipe Right");
                previousRiddle();
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                Log.d(TAG, "Swipe Left");
                nextRiddle();
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                Log.d(TAG, "Swipe Down");
                break;
            case SimpleGestureFilter.SWIPE_UP:
                Log.d(TAG, "Swipe Up");
                break;

        }
    }

    @Override
    public void onDoubleTap() {
        //Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }

    public void onRestart(Bundle savedInstanceState) {
        Log.d(TAG, "onrestart called");
    }

    public void onStart(Bundle savedInstanceState) {
        Log.d(TAG, "onstart called");
    }

    public void onResume(Bundle savedInstanceState) {
        Log.d(TAG, "onresume called");
    }

    public void onPause(Bundle savedInstanceState) {
        Log.d(TAG, "onpause called");
    }

    public void onStop(Bundle savedInstanceState) {
        Log.d(TAG, "onstop called");
    }

    public void onDestroy(Bundle savedInstanceState) {
        Log.d(TAG, "ondestroy called");
    }
}

