package cs496team1.beerisgood;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //Views
    Toolbar toolbar;

    LinearLayout progressLoading;
    FloatingActionButton button_refresh;

    TabLayout tabLayout;
    ViewPager viewPager;

    // Bottom Sheet
    View bottomSheet;
    View bottomsheet_nested_scrollview;
    BottomSheetBehavior mBottomSheetBehavior;

    // Name and type
    Toolbar bottomsheet_toolbar;

    // Description
    LinearLayout bottomsheet_layout_description;
    TextView bottomsheet_description;
    // Location
    LinearLayout bottomsheet_layout_location;
    TextView bottomsheet_location;
    // Phone
    LinearLayout bottomsheet_layout_phone;
    TextView bottomsheet_phone;
    // Website
    LinearLayout bottomsheet_layout_website;
    TextView bottomsheet_website;
    // Hours of Operation
    LinearLayout bottomsheet_layout_hours;
    TextView bottomsheet_hours;
    // Brewery
    //TextView bottomsheet_brewery;

    // Beers fragment
    FragmentBeers beersView;

    // Map fragment
    ArrayList<Marker> mapMarkers;

    // Google maps API
    SupportMapFragment mapView;
    GoogleMap _map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get views
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressLoading = (LinearLayout)findViewById(R.id.progress_database_loading);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        button_refresh = (FloatingActionButton) findViewById(R.id.FAB_reload);

        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomsheet_nested_scrollview = findViewById(R.id.bottomsheet_nested_scrollview);

        bottomsheet_toolbar = (Toolbar) findViewById(R.id.bottomsheet_toolbar);
        bottomsheet_description = (TextView) findViewById(R.id.bottomsheet_description);
        bottomsheet_location = (TextView) findViewById(R.id.bottomsheet_location);
        bottomsheet_phone = (TextView) findViewById(R.id.bottomsheet_phone);
        bottomsheet_website = (TextView) findViewById(R.id.bottomsheet_website);
        bottomsheet_hours = (TextView) findViewById(R.id.bottomsheet_hours);

        bottomsheet_layout_description = (LinearLayout) findViewById(R.id.bottomsheet_layout_description);
        bottomsheet_layout_location = (LinearLayout) findViewById(R.id.bottomsheet_layout_location);
        bottomsheet_layout_phone = (LinearLayout) findViewById(R.id.bottomsheet_layout_phone);
        bottomsheet_layout_website = (LinearLayout) findViewById(R.id.bottomsheet_layout_website);
        bottomsheet_layout_hours = (LinearLayout) findViewById(R.id.bottomsheet_layout_hours);
        //bottomsheet_brewery = (TextView) findViewById(R.id.bottomsheet_brewery);

        // Fragments
        mapView = new SupportMapFragment();
        beersView = new FragmentBeers();


        // ViewPager & tabs
        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mapView, getString(R.string.title_locations));
        adapter.addFragment(beersView, getString(R.string.title_beers));
        viewPager.setAdapter(adapter);

        
        //Set up toolbar
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.assignment_no);
        toolbar.inflateMenu(R.menu.submenu_main);
        setSupportActionBar(toolbar);

        
        // Set up refresh button
        button_refresh.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                progressLoading.setVisibility( (progressLoading.getVisibility()==View.VISIBLE ? View.GONE : View.VISIBLE) );
            }
        });


        // Set up bottom sheet (details)
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override public void onStateChanged(View bottomSheet, int newState) {
                // Expanded behavior
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    //bottomSheetHeading.setText(getString(R.string.text_collapse_me));
                } else {
                    //bottomSheetHeading.setText(getString(R.string.text_expand_me));
                }

                // Behavior switch
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        button_refresh.hide();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:

                        button_refresh.hide();
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        button_refresh.show();
                        resetMarkers();
                        break;
                }
            }


            @Override public void onSlide(View bottomSheet, float slideOffset) {}
        });

        // Bottom sheet click listeners
        bottomsheet_layout_location.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + bottomsheet_location.getText().toString()));
                startActivity(intent);
            }
        });
        bottomsheet_layout_phone.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + bottomsheet_phone.getText().toString()));
                startActivity(intent);
            }
        });
        bottomsheet_layout_website.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                String url = bottomsheet_website.getText().toString();
                if (!url.startsWith("http://") && !url.startsWith("https://")) { url = "http://" + url; }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });


        // Map markers
        mapMarkers = new ArrayList<>();


        // Make Google Maps API call
        mapView.getMapAsync(this);

    }


    // Activity actions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.submenu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //switch (item.getItemId()) {}
        return true;
    }


    // Permissions callback
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onMapReady(_map);
                }
                break;
            }
        }
    }


    // Google maps API
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        _map = googleMap;

        // Request location permission
        if ( ! Helper.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION) && Build.VERSION.SDK_INT >= 23 ){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            try {
                // Allow the user to focus on their location
                _map.setMyLocationEnabled(true);

                // Set map marker click listener
                _map.setOnMarkerClickListener(this);

                // Set map click listener (Hide bottom sheet)
                _map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override public void onMapClick(LatLng latLng) {
                        resetMarkers();
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                });

                // Move camera to 'myLocation'
                //_map.moveCamera(CameraUpdateFactory.newLatLng(_map.getMyLocation()));

                // Set zoom level
                zoomCamera(_map, 10.0f);

                // Make Async API call for locations
                getLocations(_map);

            } catch (SecurityException e){}
        }
    }

    public void zoomCamera(GoogleMap map, float zoom){
        map.moveCamera(CameraUpdateFactory.zoomTo(zoom));
    }

    @Override
    public boolean onMarkerClick(final Marker marker){
        // Set bottom sheet details (marker.getSnippet() is the ID of the location object)
        setBottomSheetDetails(ObjectManager.getLocation(marker.getSnippet()));

        // Highlight selected marker (and reset all others)
        resetMarkers();
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        marker.setZIndex(2.0f);

        // Set bottom sheet state
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        return true;
    }

    public void resetMarkers() {
        for (Marker m : mapMarkers){
            m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            m.setZIndex(1.0f);
        }
    }

    // Bottom sheet
    public void setBottomSheetDetails(Location location){
        // Name and type
        bottomsheet_toolbar.setTitle(location.getName());
        bottomsheet_toolbar.setSubtitle(String.format(getString(R.string.template_type_established), location.locationTypeDisplay, String.valueOf(location.brewery_established)));

        // Description
        bottomsheet_description.setText(location.brewery_description);

        // Location
        bottomsheet_location.setText(String.format(getString(R.string.template_location), location.streetAddress, location.city, location.region, String.valueOf(location.postalcode)));

        // Phone
        bottomsheet_phone.setText(location.phone);

        // Website
        bottomsheet_website .setText(location.getWebsite());

        // Hours of Operation
        String hours = location.getHoursOfOperationFormatted();
        if (hours == null || hours.equals("")){ bottomsheet_layout_hours.setVisibility(View.GONE); }
        else { bottomsheet_hours.setText(hours); }

    }


    // BreweryDB API
    public void getLocations(final GoogleMap map){
        // Make Async API call for locations
        AsyncRequest LocationRequest = new AsyncRequest();
        LocationRequest.execute(HttpRequest.buildLocationUrl(this), new CallBack(){
            @Override public void call(String data) {
                // Debug
                Log.e("APP", "Completed location request - " + data);
                //textView_debug.setText(data);

                // Add to object manager
                ObjectManager.addLocations(HttpRequest.formatLocationResponse(MainActivity.this, data));

                // Plot locations on map
                for (Location loc : ObjectManager.getLocations()){
                    //Log.e("MAP", "Adding location " + loc.brewery_name + " at " + loc.latitude + "," + loc.longitude);
                    // Add a marker and move the camera
                    LatLng point = new LatLng(loc.latitude, loc.longitude);
                    mapMarkers.add(map.addMarker(new MarkerOptions().position(point).title(loc.brewery_name).snippet(loc.id))); // Add map marker to map and to internal arraylist
                    map.moveCamera(CameraUpdateFactory.newLatLng(point));
                }
            }
        });
    }

    public void getBeers(){
        // Make Async API call for beers
        AsyncRequest BeerRequest = new AsyncRequest();
        BeerRequest.execute(HttpRequest.buildBeerUrl(this), new CallBack(){
            @Override public void call(String data) {
                Log.e("APP", "Completed beer request - " + data);
                //addBeer();
                progressLoading.setVisibility(View.GONE);
            }
        });
    }

}
