package afeka.katz.arkadiy.minesweeper.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by arkokat on 8/30/2017.
 */

class DataBase extends SQLiteOpenHelper {
    public static final String HIGH_SCORE_TABLE_NAME = "highscore";
    public static final String COLUMN_NAME = "player";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "LONGITUDE";
    private static final String DATABASE_NAME = "minesweeper.db";
    private static final int DATABASE_VERSION = 2;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + HIGH_SCORE_TABLE_NAME + " (" +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_TIME + " INTEGER," +
                    COLUMN_LATITUDE + " DOUBLE," +
                    COLUMN_LONGITUDE + " DOUBLE)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + HIGH_SCORE_TABLE_NAME;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        onCreate(getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
