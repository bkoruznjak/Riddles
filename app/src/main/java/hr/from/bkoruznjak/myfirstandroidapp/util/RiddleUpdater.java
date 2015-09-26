package hr.from.bkoruznjak.myfirstandroidapp.util;

import android.content.Context;
import android.util.Log;

import hr.from.bkoruznjak.myfirstandroidapp.db.DatabaseHandler;
import hr.from.bkoruznjak.myfirstandroidapp.db.Riddle;

/**
 * Created by Borna on 27.9.15.
 */
public class RiddleUpdater implements Runnable {

    private Riddle riddle;
    private Context context;
    private static final String TAG = "UPDATE";

    public RiddleUpdater(Context context, Riddle riddle) {
        this.riddle = riddle;
        this.context = context;
    }

    @Override
    public void run() {
        Log.d(TAG, "started the run method for " + this.toString());
        DatabaseHandler dbHandler = new DatabaseHandler(context);
        dbHandler.updateRiddle(riddle);
    }
}
