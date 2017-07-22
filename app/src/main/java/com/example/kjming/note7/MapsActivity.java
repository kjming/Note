package com.example.kjming.note7;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener{

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private Marker mCurrentMarker, mItemMarker;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        configGoogleApiClient();
        configLocationRequest();
        if(!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
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
        Intent intent=getIntent();
        double lat = intent.getDoubleExtra("lat",0.0);
        double lng = intent.getDoubleExtra("lng",0.0);
        if(lat != 0.0 && lng != 0.0) {
            LatLng itemPlace = new LatLng(lat,lng);
            addMarker(itemPlace,intent.getStringExtra("title"),
                    intent.getStringExtra("datetime"));
            moveMap(itemPlace);
        }
        processController();
    }

    private void moveMap(LatLng place) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(place).zoom(17).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void addMarker(LatLng place,String title,String context) {
        BitmapDescriptor item = BitmapDescriptorFactory.fromResource(R.drawable.marker);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place).title(title).snippet(context).icon(item);
        mItemMarker = mMap.addMarker(markerOptions);
    }

    private synchronized void configGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    private void configLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, MapsActivity.this);
        } catch (SecurityException ignore) {
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        if(mCurrentMarker == null) {
            mCurrentMarker = mMap.addMarker(new MarkerOptions().position(latLng));
        }else {
            mCurrentMarker.setPosition(latLng);
        }
        moveMap(latLng);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        int errorCode = connectionResult.getErrorCode();
        if (errorCode ==ConnectionResult.SERVICE_MISSING) {
            Toast.makeText(this,R.string.google_play_service_missing,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mGoogleApiClient.isConnected()&&mCurrentMarker!=null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void processController() {
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if(!mGoogleApiClient.isConnected()) {
                            mGoogleApiClient.connect();
                        }
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        Intent result = new Intent();
                        result.putExtra("lat",0);
                        result.putExtra("lng",0);
                        setResult(Activity.RESULT_OK,result);
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }

            }
        };

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(marker.equals(mItemMarker)) {
                    AlertDialog.Builder ab=new AlertDialog.Builder(MapsActivity.this);
                    ab.setTitle(R.string.title_update_location).setMessage(R.string.message_update_location)
                            .setCancelable(true);
                    ab.setPositiveButton(R.string.uptate,listener);
                    ab.setNeutralButton(R.string.clear,listener);
                    ab.setNegativeButton(android.R.string.cancel,listener);
                    ab.show();

                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.equals(mCurrentMarker)) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(MapsActivity.this);
                    ab.setTitle(R.string.title_current_location).setMessage(R.string.message_current_location)
                            .setCancelable(true);
                    ab.setPositiveButton(android.R.string.ok,new  DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent result = new Intent();
                            result.putExtra("lag",mCurrentLocation.getLatitude());
                            result.putExtra("lng",mCurrentLocation.getLongitude());
                            setResult(Activity.RESULT_OK,result);
                            finish();
                        }
                    });
                    ab.setNegativeButton(android.R.string.cancel,null);
                    ab.show();
                    return true;
                }


                return false;
            }
        });





    }
}


