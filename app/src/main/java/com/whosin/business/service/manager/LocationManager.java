package com.whosin.business.service.manager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.whosin.business.comman.interfaces.CommanCallback;

public class LocationManager {
    @NonNull
    public static LocationManager shared = LocationManager.getInstance();

    @Nullable
    private static volatile LocationManager instance = null;

    private Context context;
    private FusedLocationProviderClient fusedLocationClient;
    public double lat = 0.0;
    public double lng = 0.0;
    public boolean isRequested = false;
    private Geocoder geocoder;


    // --------------------------------------
    // region Singleton
    // --------------------------------------

    private LocationManager() {

    }

    @NonNull
    private static synchronized LocationManager getInstance() {
        if (instance == null) {
            instance = new LocationManager();
        }
        return instance;
    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public void requestLocation(Context context) {
        this.context = context;
        getLocation();
        geocoder = new Geocoder(context, Locale.ENGLISH);
    }

    public boolean isLocationGranted() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }


    public void getLocation() {
        Log.d( "TAG", "getLocation: "+"test" );
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                double tmp = lat;
                lat = locationResult.getLastLocation().getLatitude();
                lng = locationResult.getLastLocation().getLongitude();
                if (tmp == 0.0) {
                    EventBus.getDefault().post(locationResult.getLastLocation());

                }

            }
        }, Looper.getMainLooper());
    }

    private void getAddressUsingIp(CommanCallback<String> callback) {

    }

    private void getAddressFromLocation(CommanCallback<String> callback) {
        if (lat > 0 && lng > 0) {
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String city = address.getLocality();
                    String county = address.getSubAdminArea();
                    callback.onReceive(city + ", " + county);
                } else {
                    getAddressUsingIp(callback);
                }
            } catch (IOException e) {
                getAddressUsingIp(callback);
            }
        } else {
            getAddressUsingIp(callback);
        }
    }
}
