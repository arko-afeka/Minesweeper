package afeka.katz.arkadiy.minesweeper.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import afeka.katz.arkadiy.minesweeper.model.beans.HighScore;
import afeka.katz.arkadiy.minesweeper.model.beans.KeyValue;

/**
 * Created by arkokat on 8/30/2017.
 */

public class HighScorePersist {
    private DataBase dataBase;

    public HighScorePersist(Context cx) {
        dataBase = new DataBase(cx);
    }

    public void insertNewHighScore(String name, long time, double longitude, double latitude) {
        ContentValues values = new ContentValues();

        values.put(DataBase.COLUMN_NAME, name);
        values.put(DataBase.COLUMN_TIME, time);
        values.put(DataBase.COLUMN_LATITUDE, latitude);
        values.put(DataBase.COLUMN_LONGITUDE, longitude);

        SQLiteDatabase db = dataBase.getWritableDatabase();

        db.insert(DataBase.HIGH_SCORE_TABLE_NAME, null, values);

        db.close();
    }

    public List<HighScore> getHighScores(int count) {
        SQLiteDatabase db = dataBase.getReadableDatabase();

        String[] projection = {
                DataBase.COLUMN_NAME,
                DataBase.COLUMN_TIME,
                DataBase.COLUMN_LATITUDE,
                DataBase.COLUMN_LONGITUDE
        };

        String sortOrder =
                DataBase.COLUMN_TIME + " ASC";

        Cursor cursor = db.query(
                DataBase.HIGH_SCORE_TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        List<HighScore> result = new ArrayList<>(count);

        while (cursor.moveToNext() && result.size() < count) {
            result.add(new HighScore(
                    cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME)),
                    cursor.getLong(cursor.getColumnIndex(DataBase.COLUMN_TIME)),
                    cursor.getDouble(cursor.getColumnIndex(DataBase.COLUMN_LONGITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(DataBase.COLUMN_LATITUDE))
            ));
        }

        cursor.close();
        db.close();

        return result;
    }
}
