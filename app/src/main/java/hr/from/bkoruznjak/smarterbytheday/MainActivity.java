package hr.from.bkoruznjak.smarterbytheday;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import hr.from.bkoruznjak.smarterbytheday.db.DatabaseHandler;
import hr.from.bkoruznjak.smarterbytheday.db.Riddle;
import hr.from.bkoruznjak.smarterbytheday.util.ResetFavoritesDialog;
import hr.from.bkoruznjak.smarterbytheday.util.ResetViewCountDialog;
import hr.from.bkoruznjak.smarterbytheday.util.ToastHelper;

public class MainActivity extends Activity implements OnClickListener {

    public static final String TAG = "RIDDLES";
    Handler updateBarHandler;
    ProgressDialog barProgressDialog;
    ProgressBar riddleLoadingBar;
    private String WEB_DATA_URI;
    private String WEB_VERSION_URI;
    private Typeface ubuntuLTypeFace;
    private DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        riddleLoadingBar = (ProgressBar) findViewById(R.id.fetch_from_web_progress_bar);
        riddleLoadingBar.setVisibility(View.GONE);
        WEB_DATA_URI = getResources().getString(R.string.web_data_uri);
        WEB_VERSION_URI = getResources().getString(R.string.web_version_uri);
        ubuntuLTypeFace = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-L.ttf");
        dbHandler = new DatabaseHandler(this);
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
        ActivityOptions opts = ActivityOptions.makeScaleUpAnimation(v, 0, 0,
                v.getWidth(), v.getHeight());
        switch (v.getId()) {
            case R.id.main_riddle_activity_button:
                Log.i(TAG, "main riddles pressed...");
                startActivity(new Intent(this, RiddlePreviewActivity.class),
                        opts.toBundle());
                break;
            case R.id.favorite_riddle_activity_button:
                Log.i(TAG, "favorite riddles pressed...");
                startActivity(new Intent(this, FavoritesAppActivity.class),
                        opts.toBundle());
                break;
            case R.id.about_the_app_activity_button:
                Log.i(TAG, "about app pressed...");
                startActivity(new Intent(this, AboutAppActivity.class),
                        opts.toBundle());
                break;
            case R.id.check_updates:
                Log.i(TAG, "check updates pressed...");
                new FetchWebsiteVersion().execute();
                break;
            default:
                break;
        }
    }
    /**
     * @param riddleArrayList
     * @desc method creates a riddle update dialog bar on the UI thread
     */
    public void launchRiddleUpdateBar(final ArrayList<Riddle> riddleArrayList) {
        barProgressDialog = new ProgressDialog(MainActivity.this);
        barProgressDialog.setTitle("Riddle Updater");
        barProgressDialog.setMessage("Update in progress ...");
        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(riddleArrayList.size());
        barProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //handle thread delay regarding capacity
                int delayTimeInMilis = 0;
                if (riddleArrayList.size() < 100) {
                    delayTimeInMilis = 20;
                } else {
                    delayTimeInMilis = 2;
                }
                try {
                    for (int index = 0; index < riddleArrayList.size(); index++) {
                        Log.d(TAG, "updating riddle:" + index);
                        Thread.sleep(delayTimeInMilis);
                        if (dbHandler.recordIdExistsInDb(riddleArrayList.get(index).getId())) {
                            dbHandler.updateRiddle(riddleArrayList.get(index));
                        } else {
                            dbHandler.addRiddle(riddleArrayList.get(index));
                        }

                        updateBarHandler.post(new Runnable() {
                            public void run() {
                                barProgressDialog.incrementProgressBy(1);
                            }
                        });
                        if (barProgressDialog.getProgress() == barProgressDialog.getMax() - 1) {
                            Thread.sleep(2000);
                            barProgressDialog.dismiss();
                            Log.i(TAG, "Update done, bar dissmissed!");
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();
    }

    /**
     * @param webURI
     * @return riddle ArrayList
     * @desc method returns Arraylist with riddles parsed from the web .txt file
     */
    private InputStream fetchRiddleStreamFromWeb(String webURI) {
        InputStream webStream = null;
        try {
            //get the riddles file
            URL riddlesUrl = new URL(webURI);
            HttpURLConnection urlConnection = (HttpURLConnection) riddlesUrl.openConnection();
            webStream = urlConnection.getInputStream();
        } catch (Exception e) {
            Log.e(TAG, "" + e);
        }
        return webStream;
    }

    /**
     * @param webInputStream
     * @return riddle ArrayList
     * @desc method returns Arraylist with riddles parsed from the web .txt file stream
     */
    private ArrayList<Riddle> getRiddleListFromInputStream(InputStream webInputStream) {
        ArrayList<Riddle> returnedRiddleList = new ArrayList<Riddle>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(webInputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.v(TAG, line);
                String[] riddleData = line.split(" --- ");
                String riddleId = riddleData[1].replaceAll("<br/>", "\n");
                String riddleText = riddleData[2].replaceAll("<br/>", "\n");
                String riddleAnwser = riddleData[3].replaceAll("<br/>", "\n");
                Log.d(TAG + " id:", riddleId);
                Log.d(TAG + " text:", riddleText);
                Log.d(TAG + " anwser:", riddleAnwser);
                if (dbHandler.recordIdExistsInDb(riddleId)) {
                    Log.d(TAG, "existing riddle with id:" + riddleId + " changed and added to ArrayList");
                    Riddle oldRiddle = dbHandler.getRiddle(riddleId);
                    returnedRiddleList.add(new Riddle(riddleId, riddleText, riddleAnwser, oldRiddle.getViewCount(), oldRiddle.getFavorite()));
                } else {
                    Log.d(TAG, "found new riddle with id:" + riddleId + " and added to ArrayList");
                    returnedRiddleList.add(new Riddle(riddleId, riddleText, riddleAnwser, 0, 0));
                }
            }
            bufferedReader.close();
        } catch (IOException ioEx) {
            Log.e(TAG, "" + ioEx);
        }
        return returnedRiddleList;
    }

    /**
     * @return is the latest database version boolean value
     * @desc method checks if entry in table version_table matches the one from the website
     * if so true will be returned, if not table entry will be updated to latest version and false
     * will be returned
     */
    private boolean isLatestDatabaseVersion() {
        int riddleDatabaseVersion = dbHandler.getRiddleVersion();
        Log.i(TAG, "Riddle Database version:" + riddleDatabaseVersion);
        try {
            URL versionUrl = new URL(WEB_VERSION_URI);
            HttpURLConnection urlConnection = (HttpURLConnection) versionUrl.openConnection();
            try {
                BufferedReader breader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String versionLine = breader.readLine();
                Log.i(TAG, "" + versionLine);
                if (versionLine != null) {
                    int latestDatabaseVersion = Integer.parseInt(versionLine);
                    if (riddleDatabaseVersion == latestDatabaseVersion) {
                        Log.i(TAG, "riddle database is up to date...");
                        return true;
                    } else {
                        Log.i(TAG, "updating riddle database...");
                        dbHandler.changeRiddleVersion(latestDatabaseVersion);
                        return false;
                    }
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception ex) {
            Log.e(TAG, "" + ex);
        }
        return true;
    }

    private class FetchWebsiteVersion extends AsyncTask<Void, Void, Void> {

        private boolean isLatestDatabase;

        @Override
        protected Void doInBackground(Void... params) {
            isLatestDatabase = isLatestDatabaseVersion();
            Log.i(TAG, "riddle version fetched sucessfully");
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            super.onPostExecute(param);
            if (isLatestDatabase) {
                new ToastHelper(MainActivity.this.getApplicationContext(), getResources().getString(R.string.dialog_update_riddles_up_to_date), 65, ubuntuLTypeFace).show();
            } else {
                new FetchWebsiteData().execute();
            }
        }
    }

    private class FetchWebsiteData extends AsyncTask<Void, Void, Void> {

        private InputStream webInputStream;
        private ArrayList<Riddle> webRiddleArrayList = new ArrayList<Riddle>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            riddleLoadingBar.setVisibility(View.VISIBLE);
            riddleLoadingBar.setIndeterminate(true);

        }

        @Override
        protected Void doInBackground(Void... params) {
            //fetch the riddle data
            webInputStream = fetchRiddleStreamFromWeb(WEB_DATA_URI);
            webRiddleArrayList = getRiddleListFromInputStream(webInputStream);
            Log.i(TAG, "riddle stream fetched sucessfully");
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            super.onPostExecute(param);
            riddleLoadingBar.setVisibility(View.GONE);
            MainActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            launchRiddleUpdateBar(webRiddleArrayList);
        }
    }
}

