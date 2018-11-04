package com.example.hp.kleanit.HelperClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * Created by hp on 28-03-2016.
 */
public class MyLocationListener implements LocationListener {

    final String TAG = "LocManager LocLis";
    Context ctx;
    public Double longitude, latitude;
    public Location obtainedLocation;

    public MyLocationListener(Context context) {
        this.ctx = context;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            obtainedLocation = location;
            //Toast.makeText(ctx, "Location is " + location.getLatitude() + " , " + location.getLongitude() + " , " + location.getAltitude(), Toast.LENGTH_LONG).show();
            Log.d("HK", "Async to get loc");
            //new HttpGetDistance().execute(ctx);
            getCompleteAddress(location.getLatitude(),location.getLongitude());
        }

    }

    public Double getLatitude(){
        return latitude;
    }

    public Double getLongitude(){
        return longitude;
    }


    private String getCompleteAddress(double LATITUDE, double LONGITUDE) {

        String strAdd = "";

        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
        try {
            Log.i("TAG", "Started decoding");
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

            final SharedPreferences prefs = ctx.getSharedPreferences("Complaint", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }


                strAdd = strReturnedAddress.toString();

                editor.putString("fullAddress", strAdd);

                editor.putString("feature", addresses.get(0).getFeatureName());
                editor.putString("thoroughFare", addresses.get(0).getThoroughfare());
                editor.putString("subLocality", addresses.get(0).getSubLocality());

                editor.putString("locality", addresses.get(0).getLocality());
                editor.putString("adminArea", addresses.get(0).getAdminArea());
                editor.putString("postalCode", addresses.get(0).getPostalCode());
                editor.putString("countryName",addresses.get(0).getCountryName());

                editor.putString("latitude",String.valueOf(latitude));
                editor.putString("longitude",String.valueOf(longitude));

                editor.commit();

                Log.i("My Current  address", "" + strReturnedAddress.toString());
                Log.i("Locality", addresses.get(0).getLocality());
                Log.i("AdminArea", addresses.get(0).getAdminArea());
                Log.i("ADDRESS", addresses.get(0).getPostalCode());
                Log.i("ADDRESS", addresses.get(0).getCountryName());



                if (addresses.get(0).getLocale()!=null){
                    Log.i("Locale", addresses.get(0).getLocale().toString());

                }
                if (addresses.get(0).getPhone() !=null){
                    Log.i("Phone", addresses.get(0).getPhone());


                }
                if (addresses.get(0).getPremises()!=null){
                    Log.i("Premises ", addresses.get(0).getPremises());

                }
                if (addresses.get(0).getSubAdminArea()!=null){
                    Log.i("SubAdminArea ", addresses.get(0).getSubAdminArea());

                }
                if (addresses.get(0).getSubLocality()!=null){
                    Log.i("SubLocality", addresses.get(0).getSubLocality());

                }
                if (addresses.get(0).getThoroughfare()!=null){
                    Log.i("ThoroughFare", addresses.get(0).getThoroughfare());

                }
                if (addresses.get(0).getSubThoroughfare()!=null){
                    Log.i("SubThoroughFare", addresses.get(0).getSubThoroughfare());

                }


            } else {
                Log.i("My Current  address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("My Current  address", "Canont get Address!");
        }
        return strAdd;

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, provider + "is enabled");
        //Toast.makeText(ctx, provider+"is enabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, provider + "is diabled");
        //Toast.makeText(ctx, provider+"is disabled", Toast.LENGTH_LONG).show();
    }
}
