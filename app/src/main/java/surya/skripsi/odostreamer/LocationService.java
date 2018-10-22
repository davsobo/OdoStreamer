package surya.skripsi.odostreamer;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by vipul on 12/13/2015.
 */
public class LocationService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final long INTERVAL = 1000* 2;
    private static final long FASTEST_INTERVAL = 1000 *1;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation,lStart,lEnd;
    static double distance=0;
    double speed;



    private final IBinder mBinder=new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
       createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        return mBinder;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onConnected(Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
        catch (SecurityException e){}
    }

    protected void startLocationUpdates() {
        try {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
        catch (SecurityException e){}
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        distance=0;
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: function start");
//      MainActivity.locate.dismiss();
        Log.d(TAG, "onLocationChanged: something dismissed");
        mCurrentLocation=location;
        Log.d(TAG, "onLocationChanged: set location");
        if(lStart==null) {
            Log.d(TAG, "onLocationChanged: if start");
            lStart = mCurrentLocation;
            Log.d(TAG, "onLocationChanged: loc start");
            lEnd=mCurrentLocation;
            Log.d(TAG, "onLocationChanged: loc end");
        }
        else
            lEnd=mCurrentLocation;
        Log.d(TAG, "onLocationChanged: else end");
//      updateUI();
        Log.d(TAG, "onLocationChanged: update ui");
        speed=location.getSpeed()*18/5;
        Log.d(TAG, "onLocationChanged: speed set");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class LocalBinder extends Binder {

       public LocationService getService()
       {
           return LocationService.this;
       }


    }

    private void updateUI() {
        if(MainActivity.p==0) {
            distance = distance + (lStart.distanceTo(lEnd) / 1000.00);
           MainActivity.endTime=System.currentTimeMillis();
           long diff= MainActivity.endTime- MainActivity.startTime;
            diff= TimeUnit.MILLISECONDS.toMinutes(diff);
            MainActivity.time.setText("Total Time: " + diff + " minutes");
            if(speed>0.0)
                MainActivity.speed.setText("Current speed: " + new DecimalFormat("#.##").format(speed) + " km/hr");
            else
                MainActivity.speed.setText(".......");

//        Intent local = new Intent();
//
//        local.setAction("com.hello.action");
//        local.putExtra("distance",distance);
            MainActivity.dist.setText(new DecimalFormat("#.###").format(distance) + " Km's.");


            lStart = lEnd;
//        this.sendBroadcast(local);
        }

    }

    public void connect()
    {
        mGoogleApiClient.connect();
    }


    public Location returnLocation()
    {

        return mCurrentLocation;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopLocationUpdates();
        if(mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        lStart=null;
        lEnd=null;
        distance=0;
        return super.onUnbind(intent);
    }
}

