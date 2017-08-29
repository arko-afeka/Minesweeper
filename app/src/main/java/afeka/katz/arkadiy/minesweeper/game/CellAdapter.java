package afeka.katz.arkadiy.minesweeper.game;

import android.content.ClipData;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import afeka.katz.arkadiy.minesweeper.R;
import afeka.katz.arkadiy.minesweeper.utils.CellType;
import afeka.katz.arkadiy.minesweeper.utils.GameConfig;
import afeka.katz.arkadiy.minesweeper.utils.GameProgress;
import afeka.katz.arkadiy.minesweeper.utils.Level;

/**
 * Created by arkokat on 8/29/2017.
 */

public class CellAdapter extends BaseAdapter {
    private final int NUMBER_OF_CELLS;
    private final int NUMBER_OF_MINES;

    private Context cx;

    private LayoutInflater mInflater;

    CellType[][] cells;

    List<Position> minesLocation;
    Map<Position, Integer> nearMines;

    GameConfig conf;
    private float textSize;

    public CellAdapter(Context cx, Level selectedLevel, int cells, int mines) {
        this.NUMBER_OF_CELLS = cells;
        this.NUMBER_OF_MINES = mines;

        this.cx = cx;

        this.cells = new CellType[cells][cells];
        minesLocation = new ArrayList<>(mines);
        mInflater = LayoutInflater.from(cx);

        nearMines = new HashMap<>();

        for (int i = 0; i < this.cells.length; ++i) {
            for (int j = 0; j < this.cells.length; ++j) {
                this.cells[i][j] = CellType.NONE;
            }
        }

        conf = new GameConfig(cx.getResources().openRawResource(R.raw.mines));
        textSize = conf.getValue(String.format("%s%s", selectedLevel.toString().toLowerCase(), cx.getString(R.string.config_text)), Float.class);
        generateCells();
    }

    private void generateCells() {
        Set<Integer> generatedMines = new HashSet<>();
        Random rand = new Random();

        while (generatedMines.size() != NUMBER_OF_MINES) {
            generatedMines.add(rand.nextInt(NUMBER_OF_CELLS * NUMBER_OF_CELLS));
        }

        for (Integer minePos: generatedMines) {
            minesLocation.add(new Position(minePos / NUMBER_OF_CELLS, minePos % NUMBER_OF_CELLS));
        }
    }

    @Override
    public int getCount() {
        return NUMBER_OF_CELLS * NUMBER_OF_CELLS;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Button picture;

        if (v == null) {
            v = mInflater.inflate(R.layout.grid_item, parent, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (Button) v.getTag(R.id.picture);

        Position pos = getPos(position);

        switch (cells[pos.getX()][pos.getY()]) {
            case FLAG:
                picture.setBackgroundResource(R.mipmap.ic_flag);
                break;
            case OPEN:
                if (nearMines.containsKey(pos)) {
                    picture.setText(String.valueOf(nearMines.get(pos)));
                }

                picture.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
                picture.setBackgroundColor(ContextCompat.getColor(cx, R.color.colorAccent));
                break;
            case MINE:
                picture.setBackgroundResource(R.mipmap.ic_mine);
                break;
            case NONE:
                picture.setBackgroundColor(ContextCompat.getColor(cx, R.color.semiTransparentGrey));
        }

        return v;
    }

    private Position getPos(int position) {
        return new Position(position / NUMBER_OF_CELLS, position % NUMBER_OF_CELLS);
    }

    public void setFlag(int position) {
        Position pos = getPos(position);

        switch (cells[pos.getX()][pos.getY()]) {
            case FLAG:
                cells[pos.getX()][pos.getY()] = CellType.NONE;
                break;
            case NONE:
                cells[pos.getX()][pos.getY()] = CellType.FLAG;
                break;
        }
    }

    private void openMines() {
        for (Position minePos: minesLocation) {
            cells[minePos.getX()][minePos.getY()] = CellType.MINE;
        }
    }

    private int countNearMines(Position pos) {
        int counter = 0 ;

        for (int i = pos.getX() - 1; i <= pos.getX() + 1; ++i) {
            for (int j = pos.getY() - 1; j <= pos.getY() + 1; ++j) {
                Position testedPos = new Position(i, j);
                if (!testPosition(testedPos) || pos.equals(testedPos)) continue;

                if (getCellType(testedPos) == CellType.MINE) counter++;
            }
        }

        return counter;
    }

    private boolean testPosition(Position pos) {
        if (pos.getY() >= NUMBER_OF_CELLS || pos.getX() >= NUMBER_OF_CELLS ||
                pos.getX() < 0 || pos.getY() < 0) return false;

        return true;
    }

    private void openNearCells(Position pos) {
        if (!testPosition(pos)) return;

        int nearMinesCount = countNearMines(pos);

        if (getCellType(pos) == CellType.MINE || cells[pos.getX()][pos.getY()] == CellType.OPEN) return;

        cells[pos.getX()][pos.getY()] = CellType.OPEN;

        if (nearMinesCount == 0) {
            openNearCells(new Position(pos.getX() + 1, pos.getY()));
            openNearCells(new Position(pos.getX(), pos.getY() + 1));
            openNearCells(new Position(pos.getX(), pos.getY() - 1));
            openNearCells(new Position(pos.getX() - 1, pos.getY()));
        } else {
            nearMines.put(pos, nearMinesCount);
        }
    }

    private boolean checkFinished() {
        for (int i = 0; i < cells.length; ++i) {
            for (int j = 0; j < cells[i].length; ++j) {
                if (cells[i][j] == CellType.NONE) {
                    return false;
                }
            }
        }

        return true;
    }

    public GameProgress open(int position) {
        Position pos = getPos(position);

        switch (getCellType(position)) {
            case MINE:
                openMines();
                return GameProgress.EXPLODED;
            case NONE:
                nearMines = new HashMap<>();
                openNearCells(pos);
                if (checkFinished()) return GameProgress.FINISHED;
        }
        return GameProgress.CONTINUE;
    }

    private CellType getCellType(Position pos) {
        return minesLocation.contains(pos) ? CellType.MINE : cells[pos.getX()][pos.getY()] == null ? CellType.NONE : cells[pos.getX()][pos.getY()];
    }

    public CellType getCellType(int position) {
        Position pos = getPos(position);
        return getCellType(pos);
    }
}
