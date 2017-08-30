package afeka.katz.arkadiy.minesweeper.game.data;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

/**
 * Created by arkokat on 8/30/2017.
 */

public class CellData {
    private Integer backgroundColor;
    private String text;
    private Integer backgroundResource;

    public CellData(Integer backgroudColor, String text, Integer backgroundResource) {
        this.backgroundResource = backgroundResource;
        this.backgroundColor = backgroudColor;
        this.text = text;
    }

    public Integer getBackgroundColor() {
        return backgroundColor;
    }

    public String getText() {
        return text;
    }

    public Integer getBackgroundResource() {
        return backgroundResource;
    }
}
