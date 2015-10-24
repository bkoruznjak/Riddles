package hr.from.bkoruznjak.myfirstandroidapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hr.from.bkoruznjak.myfirstandroidapp.db.DatabaseHandler;
import hr.from.bkoruznjak.myfirstandroidapp.db.Riddle;
import hr.from.bkoruznjak.myfirstandroidapp.db.enums.RiddleParameterEnum;

public class FavoritesAppActivity extends AppCompatActivity {

    /**
     * The container view which has layout change animations turned on. In this sample, this view
     * is a {@link android.widget.LinearLayout}.
     */
    private ViewGroup mContainerView;
    public static final String TAG = "FAV_ACT";
    private DatabaseHandler dbHandler;
    private List<Riddle> favoriteRiddleList;
    private String[] favoriteRiddleTextArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_app);
        Typeface ubuntuRTypeFace = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-R.ttf");
        favoriteRiddleList = fetchRiddleList();

        //get the list container
        mContainerView = (ViewGroup) findViewById(R.id.container);
        favoriteRiddleTextArray = fetchFavoriteRiddleIds(favoriteRiddleList);
        Log.d(TAG, "populating the list...");
        populateTheListLayout(favoriteRiddleTextArray);
        Log.d(TAG, "popularing finished...");
    }

    /*
* @desc populates the layout with list items
*
* @param String array of riddle id's
*/
    private void populateTheListLayout(String[] favoriteRiddleIdArray) {
        for (int i = 0; i < favoriteRiddleIdArray.length; i++) {
            addItem(favoriteRiddleIdArray, i);
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
    private String[] fetchFavoriteRiddleIds(List<Riddle> favoriteRiddleList) {
        //hide the empty list text if there are elements in list
        if (favoriteRiddleList.size() > 0) {
            findViewById(android.R.id.empty).setVisibility(View.GONE);
        }
        favoriteRiddleTextArray = new String[favoriteRiddleList.size()];
        for (int i = 0; i < favoriteRiddleList.size(); i++) {
            if (((Riddle) favoriteRiddleList.get(i)).getRiddleText().length() > 55) {
                favoriteRiddleTextArray[i] = ((Riddle) favoriteRiddleList.get(i)).getRiddleText().substring(0,54);
            } else {
                favoriteRiddleTextArray[i] = ((Riddle) favoriteRiddleList.get(i)).getRiddleText();
            }

        }
        return favoriteRiddleTextArray;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*
 * @desc adds an item to the list layout
 *
 * @param integer value of the list index number
 */
    private void addItem(String[] favoriteRiddleIdArray, int itemIndex) {
        // Instantiate a new "row" view.
        final ViewGroup newView = (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.list_item, mContainerView, false);

        // Set the text in the new row to a random country.
        ((TextView) newView.findViewById(android.R.id.text1)).setText(
                favoriteRiddleIdArray[itemIndex]);

        // Set a click listener for the "X" button in the row that will remove the row.
        newView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove the row from its parent (the container view).
                // Because mContainerView has android:animateLayoutChanges set to true,
                // this removal is automatically animated.
                mContainerView.removeView(newView);

                // If there are no rows remaining, show the empty view.
                Log.d(TAG, "mContainerView child count:" + mContainerView.getChildCount());
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