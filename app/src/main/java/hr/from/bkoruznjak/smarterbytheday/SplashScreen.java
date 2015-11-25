package hr.from.bkoruznjak.smarterbytheday;

/**
 * Created by borna on 19.09.15..
 */

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import hr.from.bkoruznjak.smarterbytheday.db.DatabaseHandler;
import hr.from.bkoruznjak.smarterbytheday.db.Riddle;

public class SplashScreen extends Activity {

    public static final String TAG = "RIDDLES";
    private static final int PROGRESS = 0x1;
    // Splash screen timer
    private int riddleLoadingBarStatus = 0;
    private Handler mHandler = new Handler();

    public static int countLines(InputStream streamName) throws IOException {
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = streamName.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            streamName.close();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new PrefetchData().execute();
    }

    private class PrefetchData extends AsyncTask<Integer, Integer, String> {
        ProgressBar riddleLoadingBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            riddleLoadingBar = (ProgressBar) findViewById(R.id.riddle_progress_bar);
            try {
                Resources res = getResources();
                int numberOfLines = countLines(res.openRawResource(R.raw.riddles));
                Log.i(TAG, "number of lines:" + numberOfLines);
                riddleLoadingBar.setMax(numberOfLines);
            } catch (IOException ioEx) {
                Log.e(TAG, "" + ioEx);
            }
        }

        @Override
        protected String doInBackground(Integer... arg0) {

            //init the database
            DatabaseHandler dbHandler = new DatabaseHandler(SplashScreen.this);
            Log.d("dbHandler: ", "createdDbHandler ..");

            try {
                //get resources file
                Resources res = getResources();
                InputStream riddleInputStream = res.openRawResource(R.raw.riddles);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(riddleInputStream));
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
                        Log.d(TAG, "row already exists");
                    } else {
                        dbHandler.addRiddle(new Riddle(riddleId, riddleText, riddleAnwser, 0, 0));
                        Log.d(TAG, "row inserted successfully");
                    }
                    //increase loading bar
                    riddleLoadingBarStatus++;
                    publishProgress(riddleLoadingBarStatus);
                }
            } catch (Exception e) {
                Log.e(TAG, "" + e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            riddleLoadingBar.setVisibility(View.GONE);
            Log.v(TAG, "setting up the intent");
            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            Log.v(TAG, "starting activity");
            startActivity(i);
            Log.v(TAG, "calling finish");
            finish();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            riddleLoadingBar.setProgress(riddleLoadingBarStatus);
        }
    }

}