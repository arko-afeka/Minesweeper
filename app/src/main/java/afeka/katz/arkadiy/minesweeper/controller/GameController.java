package afeka.katz.arkadiy.minesweeper.controller;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.IBinder;
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

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import afeka.katz.arkadiy.minesweeper.R;
import afeka.katz.arkadiy.minesweeper.controller.base.UIViewController;
import afeka.katz.arkadiy.minesweeper.controller.sub.GameEndSubController;
import afeka.katz.arkadiy.minesweeper.controller.sub.LocationSubController;
import afeka.katz.arkadiy.minesweeper.game.adapter.CellAdapter;
import afeka.katz.arkadiy.minesweeper.model.config.GameConfig;
import afeka.katz.arkadiy.minesweeper.model.enums.AngleState;
import afeka.katz.arkadiy.minesweeper.model.enums.GameProgress;
import afeka.katz.arkadiy.minesweeper.model.enums.Level;
import afeka.katz.arkadiy.minesweeper.model.enums.TouchType;
import afeka.katz.arkadiy.minesweeper.service.AngleService;

public class GameController extends UIViewController implements ServiceConnection, AdapterView.OnItemClickListener, ActivityCompat.OnRequestPermissionsResultCallback, Observer {
    private Level SELECTED_LEVEL;
    private TouchType touchType = TouchType.MINE;
    private boolean timerRunning = false;
    private GameConfig config;
    private CellAdapter adapter;
    private GameEndSubController endController;
    private LocationSubController locController;
    private Chronometer timer;
    private Timer criticalTimer;
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
    public void update(Observable o, Object arg) {
        AngleState state = (AngleState)arg;

        switch (state) {
            case CRITICAL:
                if (!timerRunning) {
                    criticalTimer = new Timer();
                    criticalTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            GameController.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.hideAllOpenCells();
                                    checkProgress(adapter.addMine());
                                }
                            });
                        }
                    }, 2000, 500);
                }
                timerRunning = true;
                break;
            case NORMAL:
                if (timerRunning) {
                    criticalTimer.cancel();
                    criticalTimer.purge();
                }
                timerRunning = false;
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ((AngleService.AngleServiceBinder)service).startListening(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

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

        Intent angleService = new Intent(this, AngleService.class);
        bindService(angleService, this, BIND_AUTO_CREATE);
        criticalTimer = new Timer();
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
        unbindService(this);

        timer.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();

        timerStopped = timer.getBase() - SystemClock.elapsedRealtime();
        timer.stop();
        unbindService(this);
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

    private void checkProgress(GameProgress progress) {
        if (progress != GameProgress.CONTINUE) timer.stop();

        endController.invoke(progress, SystemClock.elapsedRealtime() - timer.getBase(), locController.getLastLocation());
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

        checkProgress(prog);
    }


}
