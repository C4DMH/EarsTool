package com.radicalninja.logger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.kml.KmlLayer;
import com.menny.android.anysoftkeyboard.R;
import com.radicalninja.logger.GPSTracker;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean gotLocation = false;
    GPSTracker gps;
    double latitude;
    double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent i = getIntent();

        //Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        //List<Address> address = null;

        gps = new GPSTracker(this);
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        Toast.makeText(this, "WE HAVE GOT YOUR LOCATION: LATITUDE = " + latitude + "LONGITUDE = " + longitude, Toast.LENGTH_LONG).show();

//        try {
//            KmlLayer layer = new KmlLayer(mMap, R.raw.vha, getApplicationContext());
//            layer.addLayerToMap();
//            Log.v("Maps", "We should have added layer");
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//            Log.v("Maps", "Pull parser exception");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.v("Maps", "IO exception");
//
//        }


//            if (geoCoder != null) {
//                try {
//                    address = geoCoder.getFromLocation(latitude, longitude, 1);
//                } catch (IOException e1) {
//                    // TODO Auto-generated catch block
//                    e1.printStackTrace();
//                }
//                if (address.size() > 0) {
//                    postCode = address.get(0).getPostalCode();
//                }
//            }
           // Toast.makeText(this, "WE HAVE GOT YOUR LOCATION: POSTCODE	 = "+ latitude + " and long: " + longitude , Toast.LENGTH_LONG).show();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.setMyLocationEnabled(true);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Current Position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));;



        try {
            KmlLayer layer = new KmlLayer(mMap, R.raw.vha2, getApplicationContext());
            layer.addLayerToMap();
            Log.v("Maps", "We should have added layer");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.v("Maps", "Pull parser exception");

        } catch (IOException e) {
            e.printStackTrace();
            Log.v("Maps", "IO exception");

        }

    }


}
