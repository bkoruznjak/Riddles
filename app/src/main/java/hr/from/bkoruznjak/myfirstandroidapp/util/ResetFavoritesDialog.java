package hr.from.bkoruznjak.myfirstandroidapp.util;

/**
 * Created by Borna on 9.11.15.
 */

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hr.from.bkoruznjak.myfirstandroidapp.R;
import hr.from.bkoruznjak.myfirstandroidapp.db.DatabaseHandler;
import hr.from.bkoruznjak.myfirstandroidapp.db.Riddle;
import hr.from.bkoruznjak.myfirstandroidapp.db.enums.RiddleParameterEnum;

public class ResetFavoritesDialog extends DialogFragment {

    private final String TAG = "RIDDLES";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Typeface ubuntuLTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Ubuntu-L.ttf");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        TextView resetText = new TextView(getActivity().getApplicationContext());
        resetText.setText(R.string.dialog_erase_favorites_text);
        resetText.setTextSize(15);
        resetText.setPadding(10, 10, 10, 10);
        resetText.setTextColor(Color.parseColor("#000000"));
        resetText.setTypeface(ubuntuLTypeFace);
        builder.setView(resetText);
        builder.setPositiveButton(R.string.dialog_erase_favorites_action, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i(TAG, "reseting favorites...");
                resetFavorites();
            }
        })
                .setNegativeButton(R.string.dialog_erase_favorites_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG, "canceling...");
                    }
                });

        return builder.create();
    }

    public void resetFavorites() {
        Log.i(TAG, "resetViewCount called...");
        Context tempContext = getActivity().getApplicationContext();
        DatabaseHandler dbHandler = new DatabaseHandler(tempContext);
        List<Riddle> favoriteRiddleList = dbHandler.getAllRiddles(RiddleParameterEnum.FAVORITE);
        List<Riddle> resetRiddleList = new ArrayList<>();
        for (Riddle seenRiddle : favoriteRiddleList) {
            Log.i(TAG, "" + seenRiddle.getId() + " fav status:" + seenRiddle.getFavorite());
            seenRiddle.setFavorite(0);
            resetRiddleList.add(seenRiddle);
        }
        (new Thread(new RiddleUpdater(tempContext, resetRiddleList))).start();
        summonToast(tempContext, "Favorites erased!");
    }

    /**
     * @param application context
     * @param message     to toast display
     * @desc handles the Toast display when settings options are done executing
     */
    public void summonToast(Context context, String message) {
        final Typeface ubuntuLTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Ubuntu-L.ttf");
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastText = (TextView) toastLayout.getChildAt(0);
        toastText.setTypeface(ubuntuLTypeFace);
        toast.setGravity(Gravity.BOTTOM, 0, 65);
        toast.show();
    }
}

