package afeka.katz.arkadiy.minesweeper.controller;

import android.content.Intent;
import android.view.View;

import afeka.katz.arkadiy.minesweeper.R;
import afeka.katz.arkadiy.minesweeper.controller.base.UIViewController;
import afeka.katz.arkadiy.minesweeper.controller.sub.GameEndSubController;

public class MainMenuController extends UIViewController {
    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.start_button:
                onGameStart(v);
                break;
            case R.id.high_score_button:
                onScore(v);
                break;
        }
    }

    private void onGameStart(View view) {
        startActivity(new Intent(this, GameStartController.class));
    }

    private void onScore(View view) {
        startActivity(new Intent(this, HighScoresController.class));
    }

    @Override
    protected void onLaunch() {
        setContentView(R.layout.main_activity);
        super.onLaunch();

        findViewById(R.id.start_button).setOnClickListener(this);
        findViewById(R.id.high_score_button).setOnClickListener(this);
    }
}
