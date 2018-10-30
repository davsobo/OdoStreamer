package surya.skripsi.odostreamer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_APPEND;


public class dataStore {
    static public double maxSpeed;
    static public double averageSpeed;
    static public double totalDistance;
    static public double maxGForce;
    static public GoogleMap gMap;
    static public ArrayList<LatLng> path;
    static public String unit = "Kmph";
    ArrayList<String> dataToText;
    public static String filename;

    static public void updatePath(double latitude, double longitude) {
        path.add(new LatLng(latitude, longitude));
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

    static public void printToFile(String data) {

        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dataSkripsi";
//        try {
//        {
//            File dir = new File(fullPath);
//            Log.d("path", fullPath);
//            Log.d("filename", filename);
//
//            if (!dir.exists()) {
//                dir.mkdirs();
//                Log.d("path", "make");
//
//            }
//            OutputStream fOut = null;
//            File file = new File(fullPath, filename);
////            if(file.exists()){
////                file.delete();
////                Log.d("path", "remove");
////            }
//            if(!file.exists()){
//                file.createNewFile();
//            }
//            fOut = context.openFileOutput(file.getCanonicalPath(),  context.MODE_APPEND);;
//            fOut.write(data.getBytes());
//            fOut.flush();
//            fOut.close();
//            Log.d(TAG, "printToFile: test");
//            FileOutputStream fOut = context.openFileOutput(filename, MODE_APPEND);
//            fOut.write(data.getBytes());
//            fOut.close();
//        } catch (Exception e) {
//            Log.e("saveToExternalStorage()", e.getMessage());
//        }
        FileWriter writer;
        try {
            writer = new FileWriter(fullPath+File.separator+filename, true);
            writer.write(data+'\n');
            writer.flush();
            writer.close();
        } catch (IOException e) {
            //Error handling
        }
    }
}
