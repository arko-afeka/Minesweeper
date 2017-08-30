package afeka.katz.arkadiy.minesweeper.model.config;

import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import afeka.katz.arkadiy.minesweeper.R;

/**
 * Created by arkokat on 8/28/2017.
 */

public class GameConfig {
    private final String DELIMITER = "=";
    private Map<String, String> config;

    public GameConfig(InputStream config) {
        this.config = new HashMap<>();
        initConfig(config);
    }

    public static String getLogTag() {
        return "afeka.katz.arkadiy.minesweeper";
    }

    private void initConfig(InputStream inS) {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(inS))) {
            while (in.ready()) {
                String line = in.readLine();

                if (line.length() == 0) continue;

                String[] data = line.split(DELIMITER);

                config.put(data[0], data[1]);
            }
        } catch (IOException ex) {
            Log.e(getLogTag(), "initConfig: problem opening configuration", ex);
        }
    }

    public <T> T getValue(String key, Class<T> dataType) {
        try {
            Constructor<T> cons = dataType.getConstructor(String.class);
            return cons.newInstance(config.get(key));
        } catch (Exception ex) {
            Log.e(getLogTag(), "getValue: Cannot find key or no constructor from String class", ex);
        }

        return null;
    }
}
