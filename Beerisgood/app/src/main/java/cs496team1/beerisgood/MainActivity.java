package cs496team1.beerisgood;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
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
import android.view.*;
import android.widget.*;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

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
    ClusterManager<CustomMarker> clusterManager;
    ArrayList<CustomMarker> recentMarkers; //Markers that were recently tapped by the user (deselect them!)

    // Google maps API
    SupportMapFragment mapView;
    GoogleMap _map;
    ArrayList<String> localities;

    // Booleans for refreshing
    boolean refreshing_locations = false;
    boolean refreshing_beers = false;


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

        // Fragments
        mapView = new SupportMapFragment();
        beersView = new FragmentBeers();


        // ViewPager & tabs
        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mapView, getString(R.string.title_locations));
        adapter.addFragment(beersView, getString(R.string.title_beers));
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override public void onPageSelected(int position) {
                // Hide bottom sheet
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                // Switch on tab index
                switch(position){
                    case 0: // Map
                        button_refresh.setImageResource(R.drawable.ic_search);
                        if (refreshing_locations) {
                            button_refresh.hide();
                            progressLoading.setVisibility(View.VISIBLE);
                        } else {
                            button_refresh.show();
                            progressLoading.setVisibility(View.GONE);
                        }
                        break;
                    case 1: // Beer
                        button_refresh.setImageResource(R.drawable.ic_reload);

                        if (refreshing_beers) {
                            button_refresh.hide();
                            progressLoading.setVisibility(View.VISIBLE);
                        } else {
                            button_refresh.show();
                            progressLoading.setVisibility(View.GONE);
                        }
                        break;
                }
            }

            @Override public void onPageScrollStateChanged(int state) {}
        });

        
        //Set up toolbar
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.assignment_no);
        toolbar.inflateMenu(R.menu.submenu_main);
        setSupportActionBar(toolbar);

        
        // Set up refresh button
        button_refresh.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                // Hide bottom sheet
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                // Show progress bar
                progressLoading.setVisibility( View.VISIBLE );

                // Hide this button
                button_refresh.hide();

                // Make API calls (dependant on tab)
                switch(viewPager.getCurrentItem()){
                    case 0: // Map
                        refreshing_locations = true;
                        findLocationsFromCity(_map);
                        break;
                    case 1: // Beers
                        refreshing_beers = true;
                        getBeers();
                        break;
                }
            }
        });


        // Set up bottom sheet (details)
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override public void onStateChanged(View bottomSheet, int newState) {
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
        recentMarkers = new ArrayList<>();

        // Localities and regions
        localities = new ArrayList<>();

        // Make Google Maps API call
        mapView.getMapAsync(this);

        // Make BreweryDB API beers call
        getBeers();

    }


    // Activity actions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.submenu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_action_settings:
                Intent intent = new Intent(MainActivity.this, ActivitySettings.class);
                startActivity(intent);
                break;
        }
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

                // Move camera to 'myLocation'
                LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                android.location.Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                _map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

                // Set zoom level
                zoomCamera(_map, 10.0f);

                // Set map click listener (Hide bottom sheet)
                _map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override public void onMapClick(LatLng latLng) { mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN); }
                });

                // Map options
                //.setBuildingsEnabled(true);
                //_map.setMapType(GoogleMap.MAP_TYPE_SATELLITE); // Move to settings

                // Cluster manager
                clusterManager = new ClusterManager<>(this, googleMap);
                _map.setOnCameraIdleListener(clusterManager);
                _map.setOnMarkerClickListener(clusterManager);
                _map.setOnInfoWindowClickListener(clusterManager);

                // Set marker click listener
                clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<CustomMarker>() {
                    @Override
                    public boolean onClusterItemClick(CustomMarker customMarker) {
                        // Set bottom sheet details (marker.getSnippet() is the ID of the location object)
                        setBottomSheetDetails(ObjectManager.getLocation(customMarker.getId()));
                        // Set bottom sheet state
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        return false;
                    }
                });

                // Make Async API call for locations
                findLocationsFromCity(_map);

            } catch (SecurityException e){}
        }
    }

    public void zoomCamera(GoogleMap map, float zoom){ map.moveCamera(CameraUpdateFactory.zoomTo(zoom)); }

    public void findLocationsFromCity(GoogleMap map){
        LatLng latLong = map.getCameraPosition().target;

        try {
            // Use geocoder to get cities near the current location
            Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latLong.latitude, latLong.longitude, 100);

            int new_cities = 0;
            for (int i = 0; i < addresses.size(); i++) {
                String locality = addresses.get(i).getLocality();
                String region = addresses.get(i).getAdminArea();

                // Save the locality and region, and get the locations in the city (locality) and state (region)
                if (locality != null && region != null) {

                    // New cities get API calls
                    if (!localities.contains(locality + "," + region)) {
                        new_cities++;

                        localities.add(locality + "," + region);
                        getLocations(locality, region);
                    }
                }
            }

            // Notify user nothing was found and show refresh button
            if (new_cities==0){
                Toast.makeText(MainActivity.this, getString(R.string.template_found_nothing), Toast.LENGTH_SHORT).show();

                // Hide progress bar
                progressLoading.setVisibility( View.GONE );

                // Enable refresh button
                button_refresh.show();
                refreshing_locations = false;
            }


        } catch (IOException ex){ Log.e("LocationError", ex.toString()); }
    }


    // BreweryDB API
    public void getLocations(final String locality, String region){
        // Make Async API call for locations
        AsyncRequest LocationRequest = new AsyncRequest();
        LocationRequest.execute(HttpRequest.buildLocationUrl(this, locality, region), new CallBack(){
            @Override public void call(String data) {
                // Get objects from JSON string
                int found_locations = ObjectManager.countLocations();
                ArrayList<Location> new_locations = HttpRequest.formatLocationResponse(data);
                found_locations = ObjectManager.countLocations() - found_locations; // Take difference in locations before and after request

                // Plot locations on map
                for (Location loc : new_locations){
                    // Create LatLng point from location
                    LatLng point = new LatLng(loc.latitude, loc.longitude);

                    // Add marker to cluster manager
                    clusterManager.addItem(new CustomMarker(point, loc.getName(), loc.locationTypeDisplay, loc.id));
                }
                clusterManager.cluster();

                // Hide progress bar
                progressLoading.setVisibility( View.GONE );

                // Enable refresh button
                button_refresh.show();
                refreshing_locations = false;

                // Notify user of found locations
                if (found_locations > 0){
                    Toast.makeText(MainActivity.this, String.format(getString(R.string.template_found_location), String.valueOf(found_locations), locality), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, String.format(getString(R.string.template_found_nothing_in), locality), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getBeers(){
        // Make Async API call for beers
        AsyncRequest BeerRequest = new AsyncRequest();
        BeerRequest.execute(HttpRequest.buildBeerUrl(this), new CallBack(){
            @Override public void call(String data) {
                // Get objects from JSON string
                HttpRequest.formatBeerResponse(data);

                // Hide progress bar
                progressLoading.setVisibility(View.GONE);

                // Enable refresh button
                button_refresh.show();
                refreshing_beers = false;

                // Refresh adapter
                beersView.adapter_beers.notifyDataSetChanged();
            }
        });
    }


    // Bottom sheet
    public void setBottomSheetDetails(Location location){
        // Name and type
        bottomsheet_toolbar.setTitle(location.getName());
        String subtitle = String.format(getString(R.string.template_type), location.locationTypeDisplay);
        if (location.yearOpened != 0)
            subtitle += " " + String.format(getString(R.string.template_opened), String.valueOf(location.yearOpened));
        if (location.getBreweryEstablished() != 0)
            subtitle += " " + String.format(getString(R.string.template_est), String.valueOf(location.getBreweryEstablished()));
        bottomsheet_toolbar.setSubtitle(subtitle);

        // Description
        String description = location.getBreweryDescription();
        if (description == null || description.equals("")) {
            bottomsheet_layout_description.setVisibility(View.GONE);
        }
        else { bottomsheet_description.setText(description); }

        // Location
        String address = String.format(getString(R.string.template_location), location.streetAddress, location.city, location.region, String.valueOf(location.postalcode));
        if (address == null || address.equals("")) { bottomsheet_layout_location.setVisibility(View.GONE); }
        else { bottomsheet_location.setText(address); }

        // Phone
        String phone = location.phone;
        if (phone == null || phone.equals("")) { bottomsheet_layout_phone.setVisibility(View.GONE); }
        else { bottomsheet_phone.setText(phone); }

        // Website
        String website = location.getWebsite();
        if (website == null || website.equals("")) { bottomsheet_layout_website.setVisibility(View.GONE); }
        else { bottomsheet_website.setText(website); }

        // Hours of Operation
        String hours = location.getHoursOfOperationFormatted();
        if (hours == null || hours.equals("")) { bottomsheet_layout_hours.setVisibility(View.GONE); }
        else { bottomsheet_hours.setText(hours); }
    }

}
