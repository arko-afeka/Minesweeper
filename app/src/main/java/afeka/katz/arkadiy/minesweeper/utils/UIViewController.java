package afeka.katz.arkadiy.minesweeper.utils;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import afeka.katz.arkadiy.minesweeper.R;

/**
 * Created by arkokat on 8/26/2017.
 */

public abstract class UIViewController extends AppCompatActivity implements View.OnClickListener{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        super.onCreate(savedInstanceState);
        onLaunch();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.game_back:
                finish();
        }
    }

    protected void onLaunch() {
        View backButton = findViewById(R.id.game_back);
        if (backButton != null) {
            backButton.setOnClickListener(this);
        }
    }
}
