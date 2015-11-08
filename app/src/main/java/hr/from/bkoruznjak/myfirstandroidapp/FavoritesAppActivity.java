package hr.from.bkoruznjak.myfirstandroidapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hr.from.bkoruznjak.myfirstandroidapp.db.DatabaseHandler;
import hr.from.bkoruznjak.myfirstandroidapp.db.Riddle;
import hr.from.bkoruznjak.myfirstandroidapp.db.enums.RiddleParameterEnum;
import hr.from.bkoruznjak.myfirstandroidapp.util.RiddleUpdater;

public class FavoritesAppActivity extends AppCompatActivity {

    public static final String TAG = "MY_R_APP";
    private final int FAVORITE_RIDDLE_SUBSTRING_LENGTH = 40;
    /**
     * The container view which has layout change animations turned on. In this sample, this view
     * is a {@link android.widget.LinearLayout}.
     */
    private ViewGroup mContainerView;
    private DatabaseHandler dbHandler;
    private List<Riddle> favoriteRiddleList;
    private String[] favoriteRiddleTextArray;
    private String[] favoriteRiddleIdArray;
    private Typeface ubuntuRTypeFace;
    private Riddle returnRiddle;
    private Riddle onCreateRiddle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_app);
        ubuntuRTypeFace = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-R.ttf");
        favoriteRiddleList = fetchRiddleList();
        onCreateRiddle = (Riddle) (this.getIntent().getSerializableExtra("onCreateRiddle"));
        //get the list container
        mContainerView = (ViewGroup) findViewById(R.id.container);
        favoriteRiddleTextArray = fetchFavoriteRiddleTexts(favoriteRiddleList);
        favoriteRiddleIdArray = fetchFavoriteRiddleIds(favoriteRiddleList);
        Log.d(TAG, "populating the list...");
        populateTheListLayout(favoriteRiddleTextArray, favoriteRiddleIdArray);
        Log.d(TAG, "popularing finished...");
    }


    /*
    * @desc populates the layout with list items
    *
    * @param String array of riddle id's
    */
    private void populateTheListLayout(String[] favoriteRiddleTextArray, String[] favoriteRiddleIdArray) {
        for (int i = 0; i < favoriteRiddleTextArray.length; i++) {
            addItem(favoriteRiddleTextArray, favoriteRiddleIdArray, i);
        }
    }

    /*
    * @desc initializes the list with only favorite riddles
    */
    private List<Riddle> fetchRiddleList() {
        dbHandler = new DatabaseHandler(this);
        return dbHandler.getAllRiddles(RiddleParameterEnum.FAVORITE);
    }

    /*
    * @desc returns the String Id's of the favorite riddles
    *
    * @param List of riddles with the favorite value of 1
    */
    private String[] fetchFavoriteRiddleTexts(List<Riddle> favoriteRiddleList) {
        //hide the empty list text if there are elements in list
        if (favoriteRiddleList.size() > 0) {
            findViewById(android.R.id.empty).setVisibility(View.GONE);
        }
        favoriteRiddleTextArray = new String[favoriteRiddleList.size()];
        for (int i = 0; i < favoriteRiddleList.size(); i++) {
            String favoriteRiddleDisplayMessage;
            //limit the length to be displayed
            if (favoriteRiddleList.get(i).getRiddleText().length() > FAVORITE_RIDDLE_SUBSTRING_LENGTH) {
                favoriteRiddleDisplayMessage = favoriteRiddleList.get(i).getRiddleText().substring(0, FAVORITE_RIDDLE_SUBSTRING_LENGTH - 1);
            } else {
                favoriteRiddleDisplayMessage = favoriteRiddleTextArray[i] = favoriteRiddleList.get(i).getRiddleText();
            }
            //add periods to make it look more nice
            if (favoriteRiddleDisplayMessage.endsWith(".")) {
                favoriteRiddleDisplayMessage = favoriteRiddleDisplayMessage + "..";
            } else if (favoriteRiddleDisplayMessage.matches(".*[\\w]$")) {
                favoriteRiddleDisplayMessage = favoriteRiddleDisplayMessage + "...";
            }
            favoriteRiddleTextArray[i] = favoriteRiddleDisplayMessage;

        }
        return favoriteRiddleTextArray;
    }

    /*
    * @desc returns the Id's of the favorite riddles
    *
    * @param List of riddles with the favorite value of 1
    */
    private String[] fetchFavoriteRiddleIds(List<Riddle> favoriteRiddleList) {
        //hide the empty list text if there are elements in list
        if (favoriteRiddleList.size() > 0) {
            findViewById(android.R.id.empty).setVisibility(View.GONE);
        }
        favoriteRiddleIdArray = new String[favoriteRiddleList.size()];
        for (int i = 0; i < favoriteRiddleList.size(); i++) {
            Riddle riddle = favoriteRiddleList.get(i);
            favoriteRiddleIdArray[i] = riddle.getId();
        }
        return favoriteRiddleIdArray;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button.
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * @desc go back to the main activity
    */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "returning to main...");
        returnRiddleToMainActivity(onCreateRiddle);
    }


    private void returnRiddleToMainActivity(Riddle returnRiddle) {
        Intent returnToPreviewIntent = new Intent(this, RiddlePreviewActivity.class);
        returnToPreviewIntent.putExtra("returnToMainRiddle", returnRiddle);
        startActivity(returnToPreviewIntent);
        finish();
    }

    /*
    * @desc adds an item to the list layout
    *
    * @param integer value of the list index number
    */
    private void addItem(final String[] favoriteRiddleTextArray, final String[] favoriteRiddleIdArray, final int itemIndex) {
        // Instantiate a new "row" view.
        final ViewGroup newView = (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.list_item, mContainerView, false);

        // Set the text in the new row to a random country.
        ((TextView) newView.findViewById(android.R.id.text1)).setText(
                favoriteRiddleTextArray[itemIndex]);
        ((TextView) newView.findViewById(android.R.id.text1)).setTypeface(ubuntuRTypeFace);

        // Set a click listener for the favorite riddle textview in the row that will go to that riddle.
        newView.findViewById(android.R.id.text1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get details about the riddle and open that particular favorite riddle
                Log.d(TAG, "setting up the main activity with the riddle id:" + favoriteRiddleIdArray[itemIndex] + " : " + favoriteRiddleTextArray[itemIndex]);
                returnRiddle = dbHandler.getRiddle(favoriteRiddleIdArray[itemIndex]);
                Log.d(TAG, "returning to main...");
                returnRiddleToMainActivity(returnRiddle);
            }
        });

        // Set a click listener for the "X" button in the row that will remove the row.
        newView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //remove riddle from favorites
                returnRiddle = dbHandler.getRiddle(favoriteRiddleIdArray[itemIndex]);
                returnRiddle.setFavorite(0);
                Log.d(TAG, "removing riddle: " + returnRiddle.getId() + " from favorites...");
                (new Thread(new RiddleUpdater(FavoritesAppActivity.this, returnRiddle))).start();

                // Remove the row from its parent (the container view).
                // Because mContainerView has android:animateLayoutChanges set to true,
                // this removal is automatically animated.
                mContainerView.removeView(newView);

                // If there are no rows remaining, show the empty view.
                if (mContainerView.getChildCount() == 0) {
                    findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
                }
            }
        });

        // Because mContainerView has android:animateLayoutChanges set to true,
        // adding this view is automatically animated.
        mContainerView.addView(newView, 0);
    }
}