package hr.from.bkoruznjak.smarterbytheday.util;

import android.content.Context;

import java.util.List;

import hr.from.bkoruznjak.smarterbytheday.db.DatabaseHandler;
import hr.from.bkoruznjak.smarterbytheday.db.Riddle;

/**
 * Created by Borna on 27.9.15.
 */
public class RiddleUpdater implements Runnable {

    private static final String TAG = "UPDATE";
    private Riddle riddle;
    private Context context;
    private List<Riddle> riddleList;
    private int option = 0;

    public RiddleUpdater(Context context, Riddle riddle) {
        this.option = 1;
        this.riddle = riddle;
        this.context = context;
    }

    public RiddleUpdater(Context context, List<Riddle> riddleList) {
        this.option = 2;
        this.riddleList = riddleList;
        this.context = context;
    }

    @Override
    public void run() {
        //Log.d(TAG, "started the run method for " + this.toString());
        DatabaseHandler dbHandler = new DatabaseHandler(context);
        switch (option) {
            // use for single riddles
            case 1:
                dbHandler.updateRiddle(riddle);
                break;
            //use for lots of riddles just pop a new thread with the list like so:
            // (new Thread(new RiddleUpdater(tempContext, resetRiddleList))).start();
            case 2:
                for (Riddle riddle : riddleList) {
                    if (dbHandler.recordIdExistsInDb(riddle.getId())) {
                        dbHandler.updateRiddle(riddle);
                    } else {
                        dbHandler.addRiddle(riddle);
                    }
                }
                break;
            default:
                break;
        }
    }
}
