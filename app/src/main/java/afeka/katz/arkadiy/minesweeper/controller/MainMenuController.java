package afeka.katz.arkadiy.minesweeper.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import afeka.katz.arkadiy.minesweeper.R;
import afeka.katz.arkadiy.minesweeper.utils.UIViewController;

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

    }

    private void initConfiguration() {
        SharedPreferences prefs = getSharedPreferences("mines", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();


    }

    @Override
    protected void onLaunch() {
        setContentView(R.layout.main_activity);
        super.onLaunch();

        findViewById(R.id.start_button).setOnClickListener(this);
        findViewById(R.id.high_score_button).setOnClickListener(this);
    }
}
