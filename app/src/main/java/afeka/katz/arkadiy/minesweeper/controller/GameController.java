package afeka.katz.arkadiy.minesweeper.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;

import afeka.katz.arkadiy.minesweeper.R;
import afeka.katz.arkadiy.minesweeper.game.CellAdapter;
import afeka.katz.arkadiy.minesweeper.utils.CellType;
import afeka.katz.arkadiy.minesweeper.utils.GameConfig;
import afeka.katz.arkadiy.minesweeper.utils.Level;
import afeka.katz.arkadiy.minesweeper.utils.TouchType;
import afeka.katz.arkadiy.minesweeper.utils.UIViewController;

/**
 * Created by arkokat on 8/28/2017.
 */

public class GameController extends UIViewController implements AdapterView.OnItemClickListener {
    private Level SELECTED_LEVEL;
    private TouchType touchType = TouchType.MINE;
    private GameConfig config;
    private CellAdapter adapter;
    private String m_Text = "";
    Chronometer timer;

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        timer.stop();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (touchType) {
            case FLAG:
                adapter.setFlag(position);
                break;
            case MINE:
                switch (adapter.open(position)) {
                    case EXPLODED:
                        final AlertDialog.Builder exploded = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

                        exploded.setTitle("EXPLODED");
                        exploded.setMessage("You've lost, a new game? or back?");
                        exploded.setPositiveButton("NEW GAME", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(getIntent());
                                finish();
                            }
                        });

                        exploded.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        exploded.show();
                        break;
                    case FINISHED:
                        final AlertDialog.Builder finished = new AlertDialog.Builder(this);
                        finished.setTitle("YOU WON!");
                        finished.setMessage("Input your name for highscores! empty string not to save");

                        final EditText input = new EditText(this);
                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        finished.setView(input);

                        finished.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                m_Text = input.getText().toString();

                                if (m_Text.length() > 0) {

                                }

                                startActivity(new Intent(GameController.this, HighScoresController.class));
                                finish();
                            }
                        });

                        finished.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });

                        finished.show();
                        break;
                }
                break;
        }

        ((BaseAdapter)parent.getAdapter()).notifyDataSetChanged();
    }


}
