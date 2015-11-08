package hr.from.bkoruznjak.myfirstandroidapp;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import hr.from.bkoruznjak.myfirstandroidapp.db.Riddle;
import hr.from.bkoruznjak.myfirstandroidapp.util.RiddleUpdater;

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

    private TextSwitcher mSwitcher;
    private Button mNextButton;

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

        // Set the id view to show the page number.
        ((TextView) rootView.findViewById(R.id.riddleId1)).setText("Riddle number:" + mPageNumber);

        Button changeToFavoriteButton = (Button) rootView.findViewById(R.id.button_toggle_anwser);
        changeToFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (argRiddle != null) {
                    switch (argRiddle.getFavorite()) {
                        case 0:
                            argRiddle.setFavorite(1);
                            break;
                        case 1:
                            argRiddle.setFavorite(0);
                            break;
                    }
                    Log.i(TAG, "changing riddle favorite status to:" + argRiddle.getFavorite());
                    (new Thread(new RiddleUpdater(context, argRiddle))).start();
                    Log.i(TAG, "update done");
                }
            }
        });


        mNextButton = (Button) rootView.findViewById(R.id.buttonNext);
        mSwitcher = (TextSwitcher) rootView.findViewById(R.id.textSwitcher);

        // Set the ViewFactory of the TextSwitcher that will create TextView object when asked
        mSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // TODO Auto-generated method stub
                // create new textView and set the properties like clolr, size etc
                TextView myText = new TextView(context);
                myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                myText.setTextSize(36);
                myText.setTextColor(Color.BLUE);
                return myText;
            }
        });

        //set initial display of riddle text

        mSwitcher.setText(riddleText);
        // Declare the in and out animations and initialize them
        Animation in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);

        // set the animation type of textSwitcher
        mSwitcher.setInAnimation(in);
        mSwitcher.setOutAnimation(out);

        mNextButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (isAnwserShown) {
                    mSwitcher.setText(riddleText);
                    isAnwserShown = false;
                } else {
                    mSwitcher.setText(riddleAnwser);
                    isAnwserShown = true;
                }
            }
        });

        return rootView;
    }

    public int getPageNumber() {
        return mPageNumber;
    }
}
