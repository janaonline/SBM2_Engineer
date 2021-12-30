package com.ichangemycity.webservice;

/**
 * Created by pattabi.raman on 28-08-2017.
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.ichangemycity.appdata.AppController;

import java.util.Timer;
import java.util.TimerTask;

public class MyLocation {


    private Timer timer1;
    private LocationManager lm;
    private LocationResult locationResult;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private Context context;

    public void getLocation(Context context, LocationResult result) {
        this.context = context;
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult = result;
        if (lm == null) {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        //exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        //don't start listeners if no provider is enabled
        if (!gps_enabled && !network_enabled) {
            return;
        }

        if (gps_enabled) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat
                            .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListenerGps);
        if (network_enabled) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 1,
                    locationListenerNetwork);
        }
        timer1 = new Timer();
        timer1.schedule(new GetLastLocation(), 20000);
    }

    private final LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
//      AppUtils.getInstance().showToast(context, AppConstant.getInstance().TOAST_TYPE_INFO,
//          "GPS : Your current location is accurate to " + location.getAccuracy() + " metres");
            locationResult.gotLocation(location, timer1, lm);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
//      AppUtils.getInstance().showToast(context, AppConstant.getInstance().TOAST_TYPE_INFO,
//          "Network : Your current location is accurate to " + location.getAccuracy() + " metres");
            locationResult.gotLocation(location, timer1, lm);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    class GetLastLocation extends TimerTask {

        @Override
        public void run() {
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);

            Location net_loc = null, gps_loc = null;
            if (gps_enabled) {
                if (ActivityCompat
                        .checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat
                                .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
            }
            gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (network_enabled) {
                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            //if there are both values use the latest one
            if (gps_loc != null && net_loc != null) {
                if (gps_loc.getTime() > net_loc.getTime()) {
                    locationResult.gotLocation(gps_loc, timer1, lm);
                } else {
                    locationResult.gotLocation(net_loc, timer1, lm);
                }
                return;
            }

            if (gps_loc != null) {
                locationResult.gotLocation(gps_loc, timer1, lm);
                return;
            }
            if (net_loc != null) {
                locationResult.gotLocation(net_loc, timer1, lm);
                return;
            }
            locationResult.gotLocation(null, timer1, lm);
        }


    }

    public void removePendingIntentAndLocationUpdates() {
        this.lm.removeUpdates(locationListenerGps);
        this.lm.removeUpdates(locationListenerNetwork);
    }

    public static abstract class LocationResult {

        public abstract void gotLocation(Location location, Timer timer, LocationManager lm);
    }
}