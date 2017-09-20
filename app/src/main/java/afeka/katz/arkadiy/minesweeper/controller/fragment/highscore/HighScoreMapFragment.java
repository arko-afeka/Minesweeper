package afeka.katz.arkadiy.minesweeper.controller.fragment.highscore;

import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import afeka.katz.arkadiy.minesweeper.R;
import afeka.katz.arkadiy.minesweeper.model.beans.HighScore;

public class HighScoreMapFragment extends MapFragment implements OnMapReadyCallback {
    private static final String HIGH_SCORES = "high_scores";
    private List<HighScore> scores;

    public HighScoreMapFragment() {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        for (HighScore score : scores) {
            long time = score.getTime();

            googleMap.addMarker(new MarkerOptions().position(new LatLng(score.getLatitude(), score.getLongitude())).
            title(String.format("%s, %s", score.getName(), String.format("%02d::%02d::%02d", time / 3600, time % 3600 / 60, time % 60))));
        }

        if (scores.size() > 0) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(scores.get(0).getLatitude(), scores.get(0).getLongitude()), 10));
        }

        try {
            googleMap.setMyLocationEnabled(true);
        } catch(SecurityException ex) {

        }
    }

    public static HighScoreMapFragment newInstance(ArrayList<HighScore> scores) {
        HighScoreMapFragment fragment = new HighScoreMapFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(HIGH_SCORES, scores);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.scores = getArguments().getParcelableArrayList(HIGH_SCORES);
        }

        getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
