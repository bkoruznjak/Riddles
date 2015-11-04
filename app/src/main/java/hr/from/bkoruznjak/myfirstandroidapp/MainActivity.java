package hr.from.bkoruznjak.myfirstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    public static final String TAG = "RIDDLES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        Button menuButtonOne = (Button) findViewById(R.id.main_riddle_activity_button);
        menuButtonOne.setOnClickListener(this);
        Button menuButtonTwo = (Button) findViewById(R.id.favorite_riddle_activity_button);
        menuButtonTwo.setOnClickListener(this);
        Button menuButtonThree = (Button) findViewById(R.id.about_the_app_activity_button);
        menuButtonThree.setOnClickListener(this);
        Button menuButtonFour = (Button) findViewById(R.id.update_riddles_activity_button);
        menuButtonFour.setOnClickListener(this);
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
            case R.id.update_riddles_activity_button:
                Log.i(TAG, "update riddles pressed...");
                checkForUpdates();
                break;
            default:
                break;
        }
    }

    private void checkForUpdates() {
        Log.i(TAG, "checking for updates...");
    }
}

