package afeka.katz.arkadiy.minesweeper.model.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by arkokat on 9/20/2017.
 */

public class HighScore implements Parcelable {
    private String name;
    private long time;
    private double longitude;
    private double latitude;

    public HighScore(String name, long time, double longitude, double latitude) {
        this.name = name;
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public HighScore(Parcel parcel) {
        this.name = parcel.readString();
        this.time = parcel.readLong();
        this.longitude = parcel.readDouble();
        this.longitude = parcel.readDouble();
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.time);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
    }

    public static final Parcelable.Creator<HighScore> CREATOR
            = new Parcelable.Creator<HighScore>() {
        public HighScore createFromParcel(Parcel in) {
            return new HighScore(in);
        }

        public HighScore[] newArray(int size) {
            return new HighScore[size];
        }
    };
}
