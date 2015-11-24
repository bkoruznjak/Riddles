package hr.from.bkoruznjak.smarterbytheday;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;

import hr.from.bkoruznjak.smarterbytheday.db.DatabaseHandler;

public class AboutAppActivity extends Activity {

    public static final String TAG = "RIDDLES";

    private TextView aboutTextView;
    private TextView creditsTitleTextView;
    private TextView creditsTextView;
    private TextView appVersion;
    private TextView appVersionData;
    private TextView riddleVersion;
    private TextView riddleVersionData;
    private TextView riddleCount;
    private TextView riddleCountData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        //set the font
        Typeface ubuntuRTypeFace = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-R.ttf");
        aboutTextView = (TextView) findViewById(R.id.about_content);
        creditsTitleTextView = (TextView) findViewById(R.id.credits_title_content);
        creditsTextView = (TextView) findViewById(R.id.credits_content);
        appVersion = (TextView) findViewById(R.id.app_version_text);
        appVersionData = (TextView) findViewById(R.id.app_version_content_text);
        riddleVersion = (TextView) findViewById(R.id.riddle_version_text);
        riddleVersionData = (TextView) findViewById(R.id.riddle_version_content_text);
        riddleCount = (TextView) findViewById(R.id.riddle_count_text);
        riddleCountData = (TextView) findViewById(R.id.riddle_count_content_text);
        aboutTextView.setTypeface(ubuntuRTypeFace);
        creditsTitleTextView.setTypeface(ubuntuRTypeFace);
        creditsTextView.setTypeface(ubuntuRTypeFace);
        appVersion.setTypeface(ubuntuRTypeFace);
        appVersionData.setTypeface(ubuntuRTypeFace);
        riddleVersion.setTypeface(ubuntuRTypeFace);
        riddleVersionData.setTypeface(ubuntuRTypeFace);
        riddleCount.setTypeface(ubuntuRTypeFace);
        riddleCountData.setTypeface(ubuntuRTypeFace);
        //set the app version data
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            appVersionData.setText(version);
        } catch (PackageManager.NameNotFoundException nnfEx) {
            //Log.e(TAG, "" + nnfEx);
        }
        //set the riddle database data
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        riddleVersionData.setText("" + dbHandler.getRiddleVersion());
        riddleCountData.setText("" + dbHandler.getRiddleCount());
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
