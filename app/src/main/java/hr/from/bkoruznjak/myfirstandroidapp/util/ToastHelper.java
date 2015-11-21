package hr.from.bkoruznjak.myfirstandroidapp.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Borna on 21.11.15.
 *
 * @desc ToastHelper creates a toast with a custom message, font and has an option to modify
 * distance from the bottom of the device screen
 */
public class ToastHelper {

    private Context context;
    private String messageToDisplay;
    private int bottomDistance;
    private Typeface customTypeFace;

    /**
     * @param context
     * @param messageToDisplay
     * @param bottomDistance
     * @param customTypeFace
     * @desc public constructor
     */
    public ToastHelper(Context context, String messageToDisplay, int bottomDistance, Typeface customTypeFace) {
        this.context = context;
        this.messageToDisplay = messageToDisplay;
        this.bottomDistance = bottomDistance;
        this.customTypeFace = customTypeFace;
    }

    /**
     * @desc method to display the Toast
     */
    public void show() {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, messageToDisplay, duration);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastText = (TextView) toastLayout.getChildAt(0);
        toastText.setTypeface(customTypeFace);
        toast.setGravity(Gravity.BOTTOM, 0, bottomDistance);
        toast.show();
    }
}
