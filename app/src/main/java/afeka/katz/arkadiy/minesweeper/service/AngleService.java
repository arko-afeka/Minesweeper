package afeka.katz.arkadiy.minesweeper.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import afeka.katz.arkadiy.minesweeper.controller.GameController;

public class AngleService extends Service {
    private GameController controller;
    private Long startAngle;

    public AngleService(GameController controller) {
        this.controller = controller;
    }

    private void onAngleChange(Long angle) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
