package hr.from.bkoruznjak.myfirstandroidapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import hr.from.bkoruznjak.myfirstandroidapp.db.Riddle;
import hr.from.bkoruznjak.myfirstandroidapp.util.RiddleUpdater;
import hr.from.bkoruznjak.myfirstandroidapp.util.ToastHelper;

public class RiddleSlidePageFragment extends Fragment {

    public static final String ARG_PAGE = "page";
    public static final String ARG_RIDDLE = "riddle";
    private final String TAG = "RIDDLES";

    private int mPageNumber;
    private String riddleId;
    private String riddleText;
    private String riddleAnwser;
    private Riddle argRiddle;
    private boolean isAnwserShown;
    private CheckBox riddleFavoriteCheckbox;
    private TextSwitcher mSwitcher;
    private Button mShowAnwserButton;
    private Typeface ubuntuLTypeFace;

    public RiddleSlidePageFragment() {

    }

    public static RiddleSlidePageFragment create(int pageNumber, Riddle riddle) {
        RiddleSlidePageFragment fragment = new RiddleSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putSerializable(ARG_RIDDLE, riddle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        argRiddle = (Riddle) getArguments().getSerializable(ARG_RIDDLE);
        this.riddleId = argRiddle.getId();
        this.riddleText = argRiddle.getRiddleText();
        this.riddleAnwser = argRiddle.getRiddleAnwser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context context = container.getContext();
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_riddle, container, false);
        //set all views
        ubuntuLTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Ubuntu-L.ttf");

        TextView favoriteCheckboxText = (TextView) rootView.findViewById(R.id.checkBox_toggle_favorite_text);
        favoriteCheckboxText.setTypeface(ubuntuLTypeFace);
        riddleFavoriteCheckbox = (CheckBox) rootView.findViewById(R.id.checkBox_toggle_favorite);
        riddleFavoriteCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (argRiddle != null) {
                    switch (argRiddle.getFavorite()) {
                        case 0:
                            argRiddle.setFavorite(1);
                            new ToastHelper(context, "Riddle added to favorites", 65, ubuntuLTypeFace).show();
                            break;
                        case 1:
                            argRiddle.setFavorite(0);
                            new ToastHelper(context, "Riddle removed from favorites", 65, ubuntuLTypeFace).show();
                            break;
                    }
                    Log.i(TAG, "changing riddle favorite status to:" + argRiddle.getFavorite());
                    (new Thread(new RiddleUpdater(context, argRiddle))).start();
                    Log.i(TAG, "update done");
                }
            }
        });
        favoriteStatusHandler(argRiddle);


        mShowAnwserButton = (Button) rootView.findViewById(R.id.buttonShowAnwser);
        mShowAnwserButton.setTypeface(ubuntuLTypeFace);
        mSwitcher = (TextSwitcher) rootView.findViewById(R.id.textSwitcher);

        // Set the ViewFactory of the TextSwitcher that will create TextView object when asked
        mSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                TextView myText = new TextView(context);
                myText.setTypeface(ubuntuLTypeFace);
                myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                myText.setTextSize(36);
                myText.setTextColor(Color.GRAY);
                return myText;
            }
        });

        //set initial display of riddle text

        mSwitcher.setText(riddleText);
        // Declare the in and out animations and initialize them
        Animation in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        in.setDuration(2000);
        Animation out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        out.setDuration(500);

        // set the animation type of textSwitcher
        mSwitcher.setInAnimation(in);
        mSwitcher.setOutAnimation(out);

        mShowAnwserButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (isAnwserShown) {
                    mSwitcher.setText(riddleText);
                    mShowAnwserButton.setText(R.string.button_show_anwser);
                    isAnwserShown = false;
                } else {
                    mSwitcher.setText(riddleAnwser);
                    mShowAnwserButton.setText(R.string.button_show_riddle);
                    isAnwserShown = true;
                }
            }
        });

        return rootView;
    }

    public int getPageNumber() {
        return mPageNumber;
    }

    /**
     * @param riddle object
     * @desc checks or unchecks the favorite checkbox based
     * on the riddle status
     */
    private void favoriteStatusHandler(Riddle riddle) {
        if (riddle.getFavorite() == 1 && !riddleFavoriteCheckbox.isChecked()) {
            //checkbox must be checked
            riddleFavoriteCheckbox.setChecked(true);
        } else if (riddle.getFavorite() == 0 && riddleFavoriteCheckbox.isChecked()) {
            //checkbox can't be checked
            riddleFavoriteCheckbox.setChecked(false);
        }
    }
}
