package afeka.katz.arkadiy.minesweeper.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;
import android.widget.GridLayout;

import afeka.katz.arkadiy.minesweeper.R;
import afeka.katz.arkadiy.minesweeper.utils.Level;
import afeka.katz.arkadiy.minesweeper.utils.UIViewController;

public class GameStartController extends UIViewController implements View.OnFocusChangeListener {
    private Level selectedLevel = null;

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) return;

        switch (v.getId()) {
            case R.id.game_level_easy:
                onEasy();
                break;
            case R.id.game_level_med:
                onMed();
                break;
            case R.id.game_level_hard:
                onHard();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.game_start:
                onGameStart();

        }
    }

    private void initializeSelectedLevel() {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        selectedLevel = Level.getById(prefs.getInt(getString(R.string.last_played_level), Level.EASY.ordinal()));

        ViewGroup layout = (ViewGroup) findViewById(R.id.game_start_layout);

        for (int i = 0; i < layout.getChildCount(); ++i) {
            if (selectedLevel.ordinal() == i) layout.getChildAt(i).requestFocusFromTouch();
        }
    }

    @Override
    protected void onLaunch() {
        setContentView(R.layout.activity_game_start_controller);
        super.onLaunch();

        findViewById(R.id.game_level_easy).setOnFocusChangeListener(this);
        findViewById(R.id.game_level_med).setOnFocusChangeListener(this);
        findViewById(R.id.game_level_hard).setOnFocusChangeListener(this);
        findViewById(R.id.game_start).setOnClickListener(this);

        findViewById(R.id.game_level_easy).setFocusableInTouchMode(true);
        findViewById(R.id.game_level_med).setFocusableInTouchMode(true);
        findViewById(R.id.game_level_hard).setFocusableInTouchMode(true);

        initializeSelectedLevel();
    }

    protected void onGameStart() {
        if (selectedLevel == null) {
            return;
        }

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        prefs.edit().putInt(getString(R.string.last_played_level), selectedLevel.ordinal()).commit();

        Intent gameIntenet = new Intent(this, GameController.class);
        gameIntenet.putExtra(getString(R.string.selected_level), selectedLevel.ordinal());

        startActivity(gameIntenet);
        finish();
    }

    protected void onEasy() {
        selectedLevel = Level.EASY;
    }

    protected void onMed() {
        selectedLevel = Level.MED;
    }

    protected void onHard() {
        selectedLevel = Level.HARD;
    }
}
