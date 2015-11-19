package hr.from.bkoruznjak.myfirstandroidapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import hr.from.bkoruznjak.myfirstandroidapp.db.DatabaseHandler;
import hr.from.bkoruznjak.myfirstandroidapp.util.ResetFavoritesDialog;
import hr.from.bkoruznjak.myfirstandroidapp.util.ResetViewCountDialog;

public class MainActivity extends Activity implements OnClickListener {

    public static final String TAG = "RIDDLES";
    Handler updateBarHandler;
    ProgressDialog barProgressDialog;
    private Typeface ubuntuLTypeFace;
    private int riddleDatabaseVersion;
    private DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        ubuntuLTypeFace = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-L.ttf");
        dbHandler = new DatabaseHandler(this);
        riddleDatabaseVersion = dbHandler.getRiddleVersion();
        updateBarHandler = new Handler();
        Button menuButtonOne = (Button) findViewById(R.id.main_riddle_activity_button);
        menuButtonOne.setOnClickListener(this);
        menuButtonOne.setTypeface(ubuntuLTypeFace);
        Button menuButtonTwo = (Button) findViewById(R.id.favorite_riddle_activity_button);
        menuButtonTwo.setOnClickListener(this);
        menuButtonTwo.setTypeface(ubuntuLTypeFace);
        Button menuButtonThree = (Button) findViewById(R.id.about_the_app_activity_button);
        menuButtonThree.setOnClickListener(this);
        menuButtonThree.setTypeface(ubuntuLTypeFace);
        Button menuButtonFour = (Button) findViewById(R.id.check_updates);
        menuButtonFour.setOnClickListener(this);
        menuButtonFour.setTypeface(ubuntuLTypeFace);
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
            case R.id.check_updates:
                Log.i(TAG, "check updates pressed...");
                new FetchWebsiteData().execute();
                launchBarDialog();
                break;
            default:
                break;
        }
    }

    public void launchBarDialog() {
        barProgressDialog = new ProgressDialog(MainActivity.this);
        barProgressDialog.setTitle("Updating riddles ...");
        barProgressDialog.setMessage("Update in progress ...");
        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(20);
        barProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Here you should write your time consuming task...
                    while (barProgressDialog.getProgress() <= barProgressDialog.getMax()) {
                        Thread.sleep(200);
                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                barProgressDialog.incrementProgressBy(2);
                            }
                        });
                        if (barProgressDialog.getProgress() == barProgressDialog.getMax()) {
                            barProgressDialog.dismiss();
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();
    }

    private void checkForUpdates() {
        Log.i(TAG, "Riddle Database version:" + riddleDatabaseVersion);
        try {
            URL versionUrl = new URL("http://borna-koruznjak.from.hr/projects/riddles/riddle_versions.txt");
            HttpURLConnection urlConnection = (HttpURLConnection) versionUrl.openConnection();
            try {
                BufferedReader breader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String versionLine = breader.readLine();
                Log.i(TAG, "" + versionLine);
                if (versionLine != null) {
                    int latestDatabaseVersion = Integer.parseInt(versionLine);
                    if (riddleDatabaseVersion == latestDatabaseVersion) {
                        Log.i(TAG, "you have the latest riddles version");
                    } else {
                        Log.i(TAG, "updating your riddles database");
                        launchBarDialog();
                        dbHandler.changeRiddleVersion(latestDatabaseVersion);
                        riddleDatabaseVersion = dbHandler.getRiddleVersion();
                    }
                }


            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception ex) {
            Log.e(TAG, "" + ex);
        }
    }

    private void updateDatabaseVersionAndContent(int version) {
        SQLiteDatabase db = new DatabaseHandler(this).getWritableDatabase();
        //update riddles first
        db.setVersion(version);
        Log.i(TAG, "database updated sucessfully");
    }

    private class FetchWebsiteData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            checkForUpdates();
            return null;
        }
    }


}

