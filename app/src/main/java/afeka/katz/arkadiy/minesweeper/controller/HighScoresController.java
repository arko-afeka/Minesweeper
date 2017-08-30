package afeka.katz.arkadiy.minesweeper.controller;

import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import afeka.katz.arkadiy.minesweeper.R;
import afeka.katz.arkadiy.minesweeper.controller.base.UIViewController;
import afeka.katz.arkadiy.minesweeper.model.beans.KeyValue;
import afeka.katz.arkadiy.minesweeper.model.db.HighScorePersist;

public class HighScoresController extends UIViewController {
    private final int MAX_SCORES = 8;

    private HighScorePersist highScorePersist;
    private LayoutInflater inflater;

    @Override
    protected void onLaunch() {
        setContentView(R.layout.activity_high_scores_controller);

        super.onLaunch();
        highScorePersist = new HighScorePersist(this);
        Intent curIntent = getIntent();

        if (curIntent.getCharSequenceExtra(getString(R.string.player_name)) != null) {
            highScorePersist.insertNewHighScore(
                    curIntent.getCharSequenceExtra(getString(R.string.player_name)).toString(),
                    curIntent.getLongExtra(getString(R.string.player_time), Long.MAX_VALUE)
            );
        }

        List<KeyValue<String, Long>> scores = highScorePersist.getHighScores(MAX_SCORES);
        inflater = LayoutInflater.from(this);

        for (KeyValue<String, Long> score : scores) {
            View v = inflater.inflate(R.layout.highscore_item, (ViewGroup) findViewById(R.id.high_score_data), false);
            ((TextView)v.findViewById(R.id.player_name)).setText(score.getKey());
            long time = score.getValue() / 1000;

            ((TextView)v.findViewById(R.id.player_time)).setText(String.format("%02d::%02d::%02d", time / 3600, time % 3600 / 60, time % 60));

            ((ViewGroup) findViewById(R.id.high_score_data)).addView(v);
        }
    }
}
