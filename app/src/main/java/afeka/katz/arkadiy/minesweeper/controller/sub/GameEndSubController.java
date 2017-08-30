package afeka.katz.arkadiy.minesweeper.controller.sub;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.widget.EditText;

import afeka.katz.arkadiy.minesweeper.R;
import afeka.katz.arkadiy.minesweeper.controller.GameController;
import afeka.katz.arkadiy.minesweeper.controller.HighScoresController;
import afeka.katz.arkadiy.minesweeper.model.enums.GameProgress;

/**
 * Created by arkokat on 8/30/2017.
 */

public class GameEndSubController {
    private AppCompatActivity cx;
    private String playerName;

    public GameEndSubController(AppCompatActivity cx) {
        this.cx = cx;
        this.playerName = "";
    }

    private void lose() {
        final AlertDialog.Builder exploded = new AlertDialog.Builder(cx, R.style.MyAlertDialogStyle);

        exploded.setTitle("EXPLODED");
        exploded.setMessage("You've lost, a new game? or back?");
        exploded.setPositiveButton("NEW GAME", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cx.startActivity(cx.getIntent());
                cx.finish();
            }
        });

        exploded.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cx.finish();
            }
        });
        exploded.show();
    }

    private void win(final long currentTime) {
        final AlertDialog.Builder finished = new AlertDialog.Builder(cx);
        finished.setTitle("YOU WON!");
        finished.setMessage("Input your name for highscores! empty string not to save");

        final EditText input = new EditText(cx);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        finished.setView(input);

        finished.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playerName = input.getText().toString();

                Intent highScores = new Intent(cx, HighScoresController.class);

                if (playerName.length() > 0) {
                    highScores.putExtra(cx.getString(R.string.player_name), playerName);
                    highScores.putExtra(cx.getString(R.string.player_time), currentTime);
                }

                cx.startActivity(highScores);
                cx.finish();
            }
        });

        finished.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cx.finish();
            }
        });

        finished.show();
    }

    public void invoke(GameProgress progress, long currentTime) {
        switch (progress) {
            case EXPLODED:
                lose();
                break;
            case FINISHED:
                win(currentTime);
                break;
        }
    }
}
