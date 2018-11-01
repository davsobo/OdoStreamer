package surya.skripsi.odostreamer;

import android.*;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pl.pawelkleczkowski.customgauge.CustomGauge;

/**
 * Created by David on 10/5/2018.
 */

public class MainActivity extends BlunoLibrary {
    android.support.v7.widget.Toolbar toolbar;
    LocationService myService;
    static boolean status;
    LocationManager locationManager;
    static TextView dist, time, speed;
    static long startTime, endTime;
    static private TextView CSpeed;
    static private TextView AvgSpeed;
    ImageView image, help;
    static ProgressDialog locate;
    static int p = 0;
    static private CustomGauge gauge;
    private Button buttonScan, buttonStop, buttonStart;
    private static boolean startStatus = false;
    int i;
    int totalSpeed = 0;
    int totalDataSpeed = 0;
    int speedNow = 120;
    boolean testing = true;
    Thread speedMeter;
    static int topSpeed = 220;

    static double totalDistance;
    static long totalTime, beforeTime;
    static TextView Yaw, Pitch, Roll, GForce;
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            myService = binder.getService();
            status = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status = false;
        }
    };

    void bindService() {
        if (status == true)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        bindService(i, sc, BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();
    }

    void unbindService() {
        if (status == false)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        unbindService(sc);
        status = false;
    }

    static int setMeter(long now) {
        double later = 200.0;
        later += 600.0 * now / topSpeed;
        return (int) later;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("BlUNOActivity onResume");
        onResumeProcess();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);                    //onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();                                                        //onPause Process by BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();                                                        //onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();
        if (status == true)
            unbindService();
    }

    @Override
    public void onBackPressed() {
        if (status == false)
            super.onBackPressed();
        else
            moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlayout);
        onCreateProcess();

        serialBegin(115200);

        //Permission Checker
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "The permission to get BLE location data is required", Toast.LENGTH_SHORT).show();
            } else {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
            Toast.makeText(this, "Location permissions already granted", Toast.LENGTH_SHORT).show();
        }


        buttonScan = (Button) findViewById(R.id.buttonScan);                      //initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                buttonScanOnClickProcess();                                       //Alert Dialog for selecting the BLE device
            }
        });
        buttonStart = (Button) findViewById(R.id.mainBtnStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(startStatus){
                    startStatus = false;
                    dataStore.printToFile(dataStore.dataToText);
                    buttonStart.setText("Start");
                    CSpeed.setText(dataStore.addKmphToSpeedometer(0));
                }
                else {
                    startStatus = true;
                    beforeTime = System.currentTimeMillis();
                    totalDistance = 0;
                    buttonStart.setText("Stop");
                }

            }
        });
        if (status == false)
            bindService();
        gauge = findViewById(R.id.gaugeSpeed);
        gauge.setEndValue(700);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        dataStore.filename = sdf.format(new Date()) + ".csv";

        CSpeed = findViewById(R.id.speedDisplay);
        AvgSpeed = findViewById(R.id.avgSDisplay);
        Yaw = findViewById(R.id.yawValue);
        Pitch = findViewById(R.id.pitchValue);
        Roll = findViewById(R.id.rollValue);
        GForce = findViewById(R.id.GValue);
//        CSpeed.setText(dataStore.addKmphToSpeedometer(speedNow));
//        gauge.setValue(setMeter(speedNow));

//        speedMeter = new Thread() {
//            public void run() {
//                if (testing) {
//                    for (i = 0; i <= topSpeed; i++) {
//                        try {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    long now = System.currentTimeMillis();
//                                    gauge.setValue(setMeter(i));
//                                    Log.d("RUN THREAD", "run: " + i + ", meter: " + setMeter(i));
//                                    dataStore.printToFile(String.valueOf(i));
//                                    CSpeed.setText(dataStore.addKmphToSpeedometer(i));
//                                    totalSpeed += i;
//                                    totalDataSpeed++;
//                                    AvgSpeed.setText(dataStore.addKmphToSpeedometer(totalSpeed / totalDataSpeed));
//                                }
//                            });
//                            Thread.sleep(50);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } else {
//                    try {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                gauge.setValue(speedNow);
//                            }
//                        });
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        };
//        speedMeter.start();
    }

    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
        switch (theConnectionState) {                                            //Four connection state
            case isConnected:
                buttonScan.setText("Connected");                                 //Will use later for Easter Egg;
                break;
            case isConnecting:
                buttonScan.setText("Connecting");
                break;
            case isToScan:
                buttonScan.setText("Scan");
                break;
            case isScanning:
                buttonScan.setText("Scanning");
                break;
            case isDisconnecting:
                buttonScan.setText("isDisconnecting");
                break;
            default:
                break;
        }
    }

    @Override
    public void onSerialReceived(String theString) {                            //Once connection data received, this function will be called
        // TODO Auto-generated method stub
        if(startStatus){
            if(!theString.isEmpty()){
                dataStore.dataToText += theString + '\n';
                List<String> rawData = Arrays.asList(theString.split("\\s*,\\s*"));
                if (rawData.size() == 3)
                {
                    for (int i = 0; i < rawData.size(); i++){
                        Log.d("rawData", "onSerialReceived: "+ rawData.get(i));
                    }
                    Yaw.setText(rawData.get(0)+"°");
                    Pitch.setText(rawData.get(1)+"°");
                    Roll.setText(rawData.get(2)+"°");
//            double G = Double.parseDouble(rawData.get(0);
//            GForce.setText(String.valueOf(G));
                }
            }
            else {
                Log.d("EmptyData", "onSerialReceived: "+ theString);
            }
        }
        Log.d("StartStatus", "onSerialReceived: "+ (startStatus ? "true" : "false"));
        Log.d("Serial Received", theString);
    }

    public static void processSpeed(double spd) {
        if(startStatus) {
            totalTime += System.currentTimeMillis() - beforeTime;
            totalDistance += spd / beforeTime;
            Log.d("totalTime", "processSpeed: "+totalTime);
            Log.d("totalDistance", "processDistance: "+totalDistance);
            gauge.setValue(setMeter(Math.round(spd)));
            CSpeed.setText(dataStore.addKmphToSpeedometer((int)spd));
            AvgSpeed.setText(dataStore.addKmphToSpeedometer((int)totalDistance / beforeTime ));
        }
    }
}
