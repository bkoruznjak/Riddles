package hr.from.bkoruznjak.myfirstandroidapp;

/**
 * Created by borna on 19.09.15..
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import hr.from.bkoruznjak.myfirstandroidapp.db.DatabaseHandler;
import hr.from.bkoruznjak.myfirstandroidapp.db.Riddle;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    public static final String TAG = "MY_R_APP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                Log.v(TAG, "run method start");

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
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                    Log.e(TAG, "" + e);
                }

                Log.v(TAG, "setting up the intent");

                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                Log.v(TAG, "starting activity");
                startActivity(i);
                Log.v(TAG, "calling finish");
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}