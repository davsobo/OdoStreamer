package surya.skripsi.odostreamer;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import pl.pawelkleczkowski.customgauge.CustomGauge;

/**
 * Created by David on 10/5/2018.
 */

public class MainActivity extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;
    LocationService myService;
    static boolean status;
    LocationManager locationManager;
    static TextView dist, time, speed;
    Button start, pause, stop;
    static long startTime, endTime;
    private TextView CSpeed;
    private TextView AvgSpeed;
    ImageView image, help;
    static ProgressDialog locate;
    static int p = 0;
    private CustomGauge gauge;
    int i;
    int totalSpeed = 0;
    int totalDataSpeed = 0;
    int speedNow = 120;
    boolean testing = true;
    Thread speedMeter;
    int topSpeed = 220;
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

    int setMeter(int now) {
        double later = 200.0;
        later += 600.0*now/topSpeed;
        return (int)later;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        if (status == false)
            bindService();
        gauge = findViewById(R.id.gaugeSpeed);
        gauge.setEndValue(700);

        CSpeed = findViewById(R.id.speedDisplay);
        AvgSpeed = findViewById(R.id.avgSDisplay);
//        CSpeed.setText(dataStore.addKmphToSpeedometer(speedNow));
//        gauge.setValue(setMeter(speedNow));

        speedMeter = new Thread() {
            public void run() {
                if (testing) {
                    for (i = 0; i <= topSpeed; i++) {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    long now = System.currentTimeMillis();
                                    gauge.setValue(setMeter(i));
                                    Log.d("RUN THREAD", "run: "+i+", meter: "+setMeter(i));
                                    CSpeed.setText(dataStore.addKmphToSpeedometer(i));
                                    totalSpeed += i;
                                    totalDataSpeed++;
                                    AvgSpeed.setText(dataStore.addKmphToSpeedometer(totalSpeed/totalDataSpeed));
                                }
                            });
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gauge.setValue(speedNow);
                            }
                        });
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        speedMeter.start();

    }

}
