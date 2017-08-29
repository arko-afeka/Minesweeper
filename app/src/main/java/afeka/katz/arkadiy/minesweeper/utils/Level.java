package afeka.katz.arkadiy.minesweeper.utils;

/**
 * Created by arkokat on 8/26/2017.
 */

public enum Level {
    EASY("Easy"),
    MED("Medium"),
    HARD("Hard");

    private String text;

    Level(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Level getById(int id) {
        for (Level l :
                Level.values()) {
            if (l.ordinal() == id) return l;
        }

        return EASY;
    }
}
