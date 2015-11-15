package hr.from.bkoruznjak.myfirstandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import hr.from.bkoruznjak.myfirstandroidapp.util.ResetFavoritesDialog;
import hr.from.bkoruznjak.myfirstandroidapp.util.ResetViewCountDialog;

public class MainActivity extends Activity implements OnClickListener {

    public static final String TAG = "RIDDLES";

    private Typeface ubuntuLTypeFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        ubuntuLTypeFace = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-L.ttf");

        Button menuButtonOne = (Button) findViewById(R.id.main_riddle_activity_button);
        menuButtonOne.setOnClickListener(this);
        menuButtonOne.setTypeface(ubuntuLTypeFace);
        Button menuButtonTwo = (Button) findViewById(R.id.favorite_riddle_activity_button);
        menuButtonTwo.setOnClickListener(this);
        menuButtonTwo.setTypeface(ubuntuLTypeFace);
        Button menuButtonThree = (Button) findViewById(R.id.about_the_app_activity_button);
        menuButtonThree.setOnClickListener(this);
        menuButtonThree.setTypeface(ubuntuLTypeFace);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_reset_viewcount:
                Log.i(TAG, "reseting view count...");
                ResetViewCountDialog resetViewCountDialog = new ResetViewCountDialog();
                resetViewCountDialog.show(getFragmentManager(), "resetViewCountDialog");

                return true;
            case R.id.action_reset_favorites:
                Log.i(TAG, "reseting favorites...");
                ResetFavoritesDialog resetFavoritesDialog = new ResetFavoritesDialog();
                resetFavoritesDialog.show(getFragmentManager(), "resetFavoritesDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "exiting...");
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_riddle_activity_button:
                Log.i(TAG, "main riddles pressed...");
                Intent riddlePreviewIntent = new Intent(this, RiddlePreviewActivity.class);
                startActivity(riddlePreviewIntent);
                break;
            case R.id.favorite_riddle_activity_button:
                Log.i(TAG, "favorite riddles pressed...");
                Intent favoritePreviewIntent = new Intent(this, FavoritesAppActivity.class);
                startActivity(favoritePreviewIntent);
                break;
            case R.id.about_the_app_activity_button:
                Log.i(TAG, "about app pressed...");
                Intent aboutPreviewIntent = new Intent(this, AboutAppActivity.class);
                startActivity(aboutPreviewIntent);
                break;
            default:
                break;
        }
    }

    private void checkForUpdates() {
        Log.i(TAG, "checking for updates...");
    }
}

