package afeka.katz.arkadiy.minesweeper.controller.fragment.highscore;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import afeka.katz.arkadiy.minesweeper.R;
import afeka.katz.arkadiy.minesweeper.model.beans.HighScore;
import afeka.katz.arkadiy.minesweeper.model.db.HighScorePersist;

public class HighScoreListFragment extends Fragment {
    private List<HighScore> scores;
    private static final String HIGH_SCORES = "high_scores";

    public static Fragment newInstance(ArrayList<HighScore> scores) {
        HighScoreListFragment fragment = new HighScoreListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(HIGH_SCORES, scores);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.scores = getArguments().getParcelableArrayList(HIGH_SCORES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup list = (ViewGroup)inflater.inflate(R.layout.fragment_high_score_list, container, false);

        for (HighScore score : scores) {
            View v = inflater.inflate(R.layout.highscore_item, container, false);
            ((TextView)v.findViewById(R.id.player_name)).setText(score.getName());
            long time = score.getTime() / 1000;

            ((TextView)v.findViewById(R.id.player_time)).setText(String.format("%02d::%02d::%02d", time / 3600, time % 3600 / 60, time % 60));

            list.addView(v);
        }
        container.addView(list);
        return inflater.inflate(R.layout.fragment_high_score_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
