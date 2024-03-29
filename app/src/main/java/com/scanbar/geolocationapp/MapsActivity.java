package com.scanbar.geolocationapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    private final int ACCESS_LOCATION_PERMISSION_CODE = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        this.googleMap = googleMap;

        showMyLocation();
    }


    public void showMyLocation() {

        if ( googleMap != null ) {

            String[] permissions = { android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION };

            if ( hasPermissions( this, permissions ) ) {

                googleMap.setMyLocationEnabled( true );

                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    addMarkerAndZoom( location, "My Location", 15 );
                                }
                            }
                        });
            }
            else {

                ActivityCompat.requestPermissions( this, permissions, ACCESS_LOCATION_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions,
                                            @NonNull int[] grantResults ) {

        for ( int grantResult : grantResults) {

            if ( grantResult == -1) {

                return;
            }
        }

        switch (requestCode) {

            case ACCESS_LOCATION_PERMISSION_CODE:

                showMyLocation();
                break;

            default:
                super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        }
    }

    public static boolean hasPermissions(MapsActivity context, String[] permissions) {

        for (String permission : permissions) {

            if (ContextCompat.checkSelfPermission( context, permission ) == PackageManager.PERMISSION_DENIED) {

                return false;
            }
        }

        return true;
    }

    public void addMarkerAndZoom( Location location, String title, int zoom  ) {

        LatLng myLocation = new LatLng( location.getLatitude(), location.getLongitude());
        googleMap.addMarker( new MarkerOptions().position( myLocation ).title( title ));
        googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom( myLocation, zoom ));
    }
}
