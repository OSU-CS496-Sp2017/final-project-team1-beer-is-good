package cs496team1.beerisgood;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;


public class FragmentLocations extends Fragment implements OnMapReadyCallback {

    LinearLayout progressLoading;
    FloatingActionButton button_search;


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


    // Map fragment
    ClusterManager<CustomMarker> clusterManager;
    ArrayList<CustomMarker> recentMarkers; //Markers that were recently tapped by the user (deselect them!)

    // Google maps API
    SupportMapFragment fragmentMap;
    GoogleMap _map;
    ArrayList<String> localities;


    // Default constructor
    public FragmentLocations() {}


    /*
    public static FragmentLocations newInstance(String param1, String param2) {
        FragmentLocations fragment = new FragmentLocations();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Map markers
        recentMarkers = new ArrayList<>();

        // Localities and regions
        localities = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_locations, container, false);

        // Get views
        fragmentMap = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        progressLoading = (LinearLayout)view.findViewById(R.id.progress_database_loading);

        button_search = (FloatingActionButton) view.findViewById(R.id.FAB_search);

        bottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomsheet_nested_scrollview = view.findViewById(R.id.bottomsheet_nested_scrollview);

        bottomsheet_toolbar = (Toolbar) view.findViewById(R.id.bottomsheet_toolbar);
        bottomsheet_description = (TextView) view.findViewById(R.id.bottomsheet_description);
        bottomsheet_location = (TextView) view.findViewById(R.id.bottomsheet_location);
        bottomsheet_phone = (TextView) view.findViewById(R.id.bottomsheet_phone);
        bottomsheet_website = (TextView) view.findViewById(R.id.bottomsheet_website);
        bottomsheet_hours = (TextView) view.findViewById(R.id.bottomsheet_hours);

        bottomsheet_layout_description = (LinearLayout) view.findViewById(R.id.bottomsheet_layout_description);
        bottomsheet_layout_location = (LinearLayout) view.findViewById(R.id.bottomsheet_layout_location);
        bottomsheet_layout_phone = (LinearLayout) view.findViewById(R.id.bottomsheet_layout_phone);
        bottomsheet_layout_website = (LinearLayout) view.findViewById(R.id.bottomsheet_layout_website);
        bottomsheet_layout_hours = (LinearLayout) view.findViewById(R.id.bottomsheet_layout_hours);


        // View properties

        // Set up refresh button
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                // Hide bottom sheet
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                // Show progress bar
                progressLoading.setVisibility( View.VISIBLE );

                // Hide this button
                button_search.hide();

                // Make location API calls
                findLocationsFromCity(_map);
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
                        button_search.hide();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        button_search.hide();
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        button_search.show();
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


        // Make Google Maps API call
        fragmentMap.getMapAsync(this);


        return view;
    }



    // Google maps API
    @Override
    public void onMapReady(GoogleMap googleMap) {
        _map = googleMap;
        clusterManager = new ClusterManager<>(getActivity(), googleMap);

        // Request location permission
        if ( ! Helper.isPermissionGranted(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) && Build.VERSION.SDK_INT >= 23 ){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Check if data has been loaded (for device rotation)
            if (!ObjectManager.has_loaded_location_initially()) {
                ObjectManager.set_has_loaded_location_initially();

                try {
                    // Allow the user to focus on their location
                    _map.setMyLocationEnabled(true);

                    // Move camera to 'myLocation'
                    LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    android.location.Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location!=null) {
                        _map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    }

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

            } else {
                plotLocations(ObjectManager.getLocations());
            }

        }
    }

    public void zoomCamera(GoogleMap map, float zoom){ map.moveCamera(CameraUpdateFactory.zoomTo(zoom)); }

    public void findLocationsFromCity(GoogleMap map){
        LatLng latLong = map.getCameraPosition().target;

        try {
            // Use geocoder to get cities near the current location
            Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
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
                Toast.makeText(getActivity(), getString(R.string.template_found_nothing), Toast.LENGTH_SHORT).show();

                // Hide progress bar
                progressLoading.setVisibility( View.GONE );

                // Enable refresh button
                button_search.show();
            }


        } catch (IOException ex){ Log.e("LocationError", ex.toString()); }
    }


    // BreweryDB API
    public void getLocations(final String locality, String region){
        // Make Async API call for locations
        AsyncRequest LocationRequest = new AsyncRequest();
        LocationRequest.execute(HttpRequest.buildLocationUrl(getActivity(), locality, region), new CallBack(){
            @Override public void call(String data) {
                // Get objects from JSON string
                int found_locations = ObjectManager.countLocations();
                ArrayList<Location> new_locations = HttpRequest.formatLocationResponse(data);
                found_locations = ObjectManager.countLocations() - found_locations; // Take difference in locations before and after request

               // Plot locations
                plotLocations(new_locations);

                // Hide progress bar
                progressLoading.setVisibility( View.GONE );

                // Enable refresh button
                button_search.show();

                // Notify user of found locations
                if (found_locations > 0){
                    Toast.makeText(getActivity(), String.format(getString(R.string.template_found_location), String.valueOf(found_locations), locality), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), String.format(getString(R.string.template_found_nothing_in), locality), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void plotLocations(Collection<Location> locations){
        // Plot locations on map
        for (Location loc : locations){
            // Create LatLng point from location
            LatLng point = new LatLng(loc.latitude, loc.longitude);

            // Add marker to cluster manager
            clusterManager.addItem(new CustomMarker(point, loc.getName(), loc.locationTypeDisplay, loc.id));
        }
        clusterManager.cluster();
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
        else {
            bottomsheet_description.setText(description);
            bottomsheet_layout_description.setVisibility(View.VISIBLE);
        }

        // Location
        String address = String.format(getString(R.string.template_location), location.streetAddress, location.city, location.region, String.valueOf(location.postalcode));
        if (address == null || address.equals("")) { bottomsheet_layout_location.setVisibility(View.GONE); }
        else {
            bottomsheet_location.setText(address);
            bottomsheet_layout_location.setVisibility(View.VISIBLE);
        }

        // Phone
        String phone = location.phone;
        if (phone == null || phone.equals("")) { bottomsheet_layout_phone.setVisibility(View.GONE); }
        else {
            bottomsheet_phone.setText(phone);
            bottomsheet_layout_phone.setVisibility(View.VISIBLE);
        }

        // Website
        String website = location.getWebsite();
        if (website == null || website.equals("")) { bottomsheet_layout_website.setVisibility(View.GONE); }
        else {
            bottomsheet_website.setText(website);
            bottomsheet_layout_website.setVisibility(View.VISIBLE);
        }

        // Hours of Operation
        String hours = location.getHoursOfOperationFormatted();
        if (hours == null || hours.equals("")) { bottomsheet_layout_hours.setVisibility(View.GONE); }
        else {
            bottomsheet_hours.setText(hours);
            bottomsheet_layout_hours.setVisibility(View.VISIBLE);
        }
    }


}
