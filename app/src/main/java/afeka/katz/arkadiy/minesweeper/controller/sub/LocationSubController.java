package afeka.katz.arkadiy.minesweeper.controller.sub;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by arkokat on 9/20/2017.
 */

public class LocationSubController implements LocationListener {
    private Location lastLocation;

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location location) {
        this.lastLocation = location;
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
