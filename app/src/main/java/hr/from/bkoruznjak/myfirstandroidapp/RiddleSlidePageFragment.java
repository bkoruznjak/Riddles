package hr.from.bkoruznjak.myfirstandroidapp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

        // Set the riddle Text view to show the page number.
        ((TextView) rootView.findViewById(R.id.riddleText1)).setText(riddleText);

        // Set the riddle Anwser view to show the page number.
        ((TextView) rootView.findViewById(R.id.riddleAnwser1)).setText(riddleAnwser);

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

        return rootView;
    }

    public int getPageNumber() {
        return mPageNumber;
    }
}
