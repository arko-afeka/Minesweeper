package afeka.katz.arkadiy.minesweeper.controller.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import afeka.katz.arkadiy.minesweeper.R;

/**
 * Created by arkokat on 8/26/2017.
 */

public abstract class UIViewController extends AppCompatActivity implements View.OnClickListener{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
