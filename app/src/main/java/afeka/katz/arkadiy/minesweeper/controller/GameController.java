package afeka.katz.arkadiy.minesweeper.controller;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.GridView;

import afeka.katz.arkadiy.minesweeper.R;
import afeka.katz.arkadiy.minesweeper.controller.base.UIViewController;
import afeka.katz.arkadiy.minesweeper.controller.sub.GameEndSubController;
import afeka.katz.arkadiy.minesweeper.controller.sub.LocationSubController;
import afeka.katz.arkadiy.minesweeper.game.adapter.CellAdapter;
import afeka.katz.arkadiy.minesweeper.model.config.GameConfig;
import afeka.katz.arkadiy.minesweeper.model.enums.GameProgress;
import afeka.katz.arkadiy.minesweeper.model.enums.Level;
import afeka.katz.arkadiy.minesweeper.model.enums.TouchType;

/**
 * Created by arkokat on 8/28/2017.
 */

public class GameController extends UIViewController implements AdapterView.OnItemClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private Level SELECTED_LEVEL;
    private TouchType touchType = TouchType.MINE;
    private GameConfig config;
    private CellAdapter adapter;
    private GameEndSubController endController;
    private LocationSubController locController;
    private Chronometer timer;
    private long timerStopped = 0;

    public GameController() {
        endController = new GameEndSubController(this);
        locController = new LocationSubController();
    }

    public void onFlag(View v) {
        touchType = TouchType.FLAG;
        findViewById(R.id.game_flag).setBackgroundResource(R.mipmap.ic_mine);
        findViewById(R.id.game_flag).invalidate();
    }

    public void onMine(View v) {
        touchType = TouchType.MINE;
        findViewById(R.id.game_flag).setBackgroundResource(R.mipmap.ic_flag);
        findViewById(R.id.game_flag).invalidate();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.game_flag:
                if (touchType == TouchType.FLAG)
                    onMine(v);
                else
                    onFlag(v);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationGranted();
        } else {
            this.finish();
        }
    }

    @Override
    protected void onLaunch() {
        setContentView(R.layout.activity_game_controller);
        super.onLaunch();

        config = new GameConfig(getResources().openRawResource(R.raw.mines));

        findViewById(R.id.game_flag).setOnClickListener(this);

        timer = (Chronometer)findViewById(R.id.game_timer);
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        SELECTED_LEVEL = Level.getById(getIntent().getIntExtra(getString(R.string.selected_level), -1));

        int numMines = config.getValue(String.format("%s%s", SELECTED_LEVEL.toString().toLowerCase(), getString(R.string.config_mines)), Integer.class);
        final int numCells = config.getValue(String.format("%s%s", SELECTED_LEVEL.toString().toLowerCase(), getString(R.string.config_cells)), Integer.class);

        GridView grid = (GridView)findViewById(R.id.game_grid);

        grid.setNumColumns(numCells);
        grid.setAdapter(adapter = new CellAdapter(getBaseContext(), SELECTED_LEVEL, numCells, numMines));
        grid.setOnItemClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationGranted();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    private void locationGranted() {
        LocationManager mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        try {
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))  {
                final AlertDialog.Builder finished = new AlertDialog.Builder(this);
                finished.setTitle("Problem");
                finished.setMessage("You must enable GPS in order to play");

                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                finished.setView(input);

                finished.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GameController.this.finish();
                    }
                });
            } else {
                locController.setLastLocation(mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locController);
            }
        } catch (SecurityException ex) {
        }
    }

    private void startLoseAnimation() {

    }

    private void startWinAnimation() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        timer.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();

        timerStopped = timer.getBase() - SystemClock.elapsedRealtime();
        timer.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();

        timer.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        timer.setBase(SystemClock.elapsedRealtime() + timerStopped);
        timer.start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GameProgress prog = GameProgress.CONTINUE;
        switch (touchType) {
            case FLAG:
                prog = adapter.setFlag(position);
                break;
            case MINE:
                prog = adapter.open(position);
                break;
        }

        if (prog != GameProgress.CONTINUE) timer.stop();

        endController.invoke(prog, SystemClock.elapsedRealtime() - timer.getBase(), locController.getLastLocation());
    }


}
