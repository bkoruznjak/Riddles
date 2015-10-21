package hr.from.bkoruznjak.myfirstandroidapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AboutAppActivity extends AppCompatActivity {

    private TextView aboutTextView;
    private TextView creditsTitleTextView;
    private TextView creditsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        //set the font
        Typeface ubuntuRTypeFace=Typeface.createFromAsset(getAssets(),"fonts/Ubuntu-R.ttf");
        aboutTextView = (TextView) findViewById(R.id.about_content);
        creditsTitleTextView = (TextView) findViewById(R.id.credits_title_content);
        creditsTextView = (TextView) findViewById(R.id.credits_content);
        aboutTextView.setTypeface(ubuntuRTypeFace);
        creditsTitleTextView.setTypeface(ubuntuRTypeFace);
        creditsTextView.setTypeface(ubuntuRTypeFace);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * NavUtils.navigateUpFromSameTask(this); will call onCreate of Main Activity
     * navigating with an Intent this way will make sure we don't end up calling
     * the Main Activity's onCreate
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            Intent intent = NavUtils.getParentActivityIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
