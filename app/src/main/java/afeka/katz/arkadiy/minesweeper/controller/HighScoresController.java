package afeka.katz.arkadiy.minesweeper.controller;

import android.content.Intent;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import afeka.katz.arkadiy.minesweeper.R;
import afeka.katz.arkadiy.minesweeper.controller.base.UIViewController;
import afeka.katz.arkadiy.minesweeper.controller.fragment.highscore.HighScoreListFragment;
import afeka.katz.arkadiy.minesweeper.controller.fragment.highscore.HighScoreMapFragment;
import afeka.katz.arkadiy.minesweeper.model.beans.HighScore;
import afeka.katz.arkadiy.minesweeper.model.db.HighScorePersist;

public class HighScoresController extends UIViewController {
    private final int MAX_SCORES = 10;

    private HighScorePersist highScorePersist;
    private boolean mapShown = false;
    private List<HighScore> scores;

    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v.getId() == R.id.map_button) {
            mapShown = !mapShown;

            if (mapShown) {
                showMap();
                v.setBackgroundResource(R.mipmap.ic_map_pressed);
                v.invalidate();
            } else {
                showList();
                v.setBackgroundResource(R.mipmap.ic_map);
                v.invalidate();
            }
        }
    }

    private void showMap() {
        Fragment fragment = HighScoreMapFragment.newInstance(new ArrayList<>(scores));
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    private void showList() {
        Fragment fragment = HighScoreListFragment.newInstance(new ArrayList<>(scores));
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    protected void onLaunch() {
        setContentView(R.layout.activity_high_scores_controller);

        super.onLaunch();
        Intent curIntent = getIntent();

        if (curIntent.getCharSequenceExtra(getString(R.string.player_name)) != null) {
            highScorePersist.insertNewHighScore(
                curIntent.getCharSequenceExtra(getString(R.string.player_name)).toString(),
                curIntent.getLongExtra(getString(R.string.player_time), Long.MAX_VALUE),
                curIntent.getDoubleExtra(getString(R.string.player_location_longitude), Double.MAX_VALUE),
                curIntent.getDoubleExtra(getString(R.string.player_location_latitue), Double.MAX_VALUE)
            );
        }

        highScorePersist = new HighScorePersist(this);
        scores = highScorePersist.getHighScores(MAX_SCORES);

        findViewById(R.id.map_button).setOnClickListener(this);
        showList();
    }
}
