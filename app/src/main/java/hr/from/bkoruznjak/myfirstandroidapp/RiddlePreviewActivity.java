package hr.from.bkoruznjak.myfirstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Collections;
import java.util.List;

import hr.from.bkoruznjak.myfirstandroidapp.db.DatabaseHandler;
import hr.from.bkoruznjak.myfirstandroidapp.db.Riddle;
import hr.from.bkoruznjak.myfirstandroidapp.db.enums.RiddleParameterEnum;
import hr.from.bkoruznjak.myfirstandroidapp.util.RiddleUpdater;

public class RiddlePreviewActivity extends FragmentActivity {
    private static int NUM_PAGES = 0;
    private final String TAG = "RIDDLES";
    private List<Riddle> riddleArrayList;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private DatabaseHandler dbHandler;
    private Riddle returnedRiddle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riddle_preview);
        dbHandler = new DatabaseHandler(this);
        riddleArrayList = dbHandler.getAllRiddles(RiddleParameterEnum.DEFAULT);
        //putting the most viewed riddles to the end of the list
        Collections.sort(riddleArrayList);
        NUM_PAGES = riddleArrayList.size();

        //check for callback riddles from other intents
        returnedRiddle = (Riddle) (this.getIntent().getSerializableExtra("returnToMainRiddle"));

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setPageTransformer(true, new NeatPageTransformer());
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                Log.i(TAG, "on Page Selected called:" + position);
                invalidateOptionsMenu();
            }

        });
        int returnedRiddleListPosition = 0;
        if (returnedRiddle != null) {
            Log.i(TAG, "returning from favorites activity");
            String id = returnedRiddle.getId();
            int counter = 0;
            for (Riddle riddle : riddleArrayList) {
                counter++;
                String tempId = riddle.getId();
                if (id.equals(tempId)) {
                    returnedRiddleListPosition = counter;
                }
            }
        }
        mPager.setCurrentItem(returnedRiddleListPosition - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_riddle_preview, menu);

        menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                        ? R.string.action_finish
                        : R.string.action_next);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_previous:
                // Go to the previous step in the wizard. If there is no previous step,
                // setCurrentItem will do nothing.
                Log.d(TAG, "action prev");
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                return true;

            case R.id.action_next:
                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
                // will do nothing.
                Log.d(TAG, "action next");
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            Riddle updateViewCountRiddle = riddleArrayList.get(position);
            int riddleViewCount = updateViewCountRiddle.getViewCount();
            updateViewCountRiddle.setViewCount(++riddleViewCount);
            Log.i(TAG, "updating riddle viewcount to:" + updateViewCountRiddle.getViewCount());
            (new Thread(new RiddleUpdater(RiddlePreviewActivity.this.getApplicationContext(), updateViewCountRiddle))).start();
            Log.i(TAG, "update done");
            return RiddleSlidePageFragment.create(position, riddleArrayList.get(position));
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
