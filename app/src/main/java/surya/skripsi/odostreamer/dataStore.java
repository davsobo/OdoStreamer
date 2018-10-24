package surya.skripsi.odostreamer;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;


public class dataStore {
    static public double maxSpeed;
    static public double averageSpeed;
    static public double totalDistance;
    static public double maxGForce;
    static public GoogleMap gMap;
    static public ArrayList<LatLng> path;
    static public String unit = "Kmph";
    static public void updatePath( double latitude, double longitude){
        path.add(new LatLng(latitude,longitude));
        gMap.addPolygon(new PolygonOptions()
                .addAll(path)
                .strokeColor(Color.YELLOW)
                .strokeWidth(10)
                .fillColor(Color.YELLOW)
        );
    }
    static public String addKmphToSpeedometer(double speed) {
        return String.valueOf(speed) + " " + unit;
    }
    static public String addKmphToSpeedometer(float speed) {
        return String.valueOf(speed) + " " + unit;
    }
    static public String addKmphToSpeedometer(int speed) {
        return String.valueOf(speed) + " " + unit;
    }
}
