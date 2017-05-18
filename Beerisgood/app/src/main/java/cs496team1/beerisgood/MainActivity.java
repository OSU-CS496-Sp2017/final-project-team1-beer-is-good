package cs496team1.beerisgood;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import java.util.Random;

import android.support.design.widget.BottomNavigationView;



public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    //Views
    Toolbar toolbar;
    RecyclerView recyclerView_beers;
    BeerAdapter adapter_beers;

    LinearLayout progressLoading;
    FloatingActionButton button_refresh;

    FrameLayout frameLayout_mapholder;

    BottomNavigationView nav_menu;
    
    // Google maps API
    SupportMapFragment mapView;
    GoogleMap map;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get views
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView_beers = (RecyclerView)findViewById(R.id.recyclerview_beer);
        progressLoading = (LinearLayout)findViewById(R.id.progress_database_loading);

        nav_menu = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        frameLayout_mapholder = (FrameLayout) findViewById(R.id.framelayout_mapholder);

        button_refresh = (FloatingActionButton) findViewById(R.id.FAB_reload);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapView.getMapAsync(this);

        
        //Set up toolbar
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.assignment_no);
        toolbar.inflateMenu(R.menu.submenu_main);
        setSupportActionBar(toolbar);


        //Linear Layout Manager
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView_beers.setLayoutManager(llm);

        adapter_beers = new BeerAdapter(this);
        recyclerView_beers.setAdapter(adapter_beers);

        //Hide floating action button when recyclerView is scrolled
        recyclerView_beers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 10 && button_refresh.isShown()) { button_refresh.hide(); }
                else if (dy < 0 && !button_refresh.isShown()){button_refresh.show(); }
            }
        });

        
        // Set up refresh button
        button_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProgressVisibility( (progressLoading.getVisibility()==View.VISIBLE ? View.GONE : View.VISIBLE) );
            }
        });
        
        
        // Set up bottom navigation panel
        nav_menu.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    item.setChecked(true);

                    // Individual items
                    switch (item.getItemId()) {
                        case R.id.action_beer:
                            frameLayout_mapholder.setVisibility(View.GONE);
                            recyclerView_beers.setVisibility(View.VISIBLE);
                            break;
                        case R.id.action_breweries:
                            frameLayout_mapholder.setVisibility(View.VISIBLE);
                            recyclerView_beers.setVisibility(View.GONE);
                            break;
                    }

                    return false;
                }
            }
        );


        // Populate recyclerview with dummy data
        Beer beer1 = new Beer();
        beer1.name = "Bud Light (ew)";
        beer1.description = "Piss beer";
        beer1.year = 2017;
        beer1.IBU = 80;
        beer1.ABV = 4.0f;
        beer1.originalGravity = 1.062f;
        beer1.servingTemperatureDisplay = "Room Temp";
        beer1.glass = "Can";
        beer1.foodPairings = "Fried chicken";

        Beer beer2 = new Beer();
        beer2.name = "Local Craft beer";
        beer2.description = "Expensive";
        beer2.year = 2003;
        beer2.IBU = 10;
        beer2.ABV = 9.1f;
        beer2.originalGravity = 0.89f;
        beer2.servingTemperatureDisplay = "Cold";
        beer2.servingTemperatureDisplay = "Cold";
        beer2.glass = "Pint glass";
        beer2.foodPairings = "Burger and fries";
        adapter_beers.addBeer(beer1);
        adapter_beers.addBeer(beer2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.submenu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            /* case R.id.toolbar_action_: //Open location
                String intentStr = String.format(getString(R.string.intent_location), "44.567015", "-123.278551", "44.567015", "-123.278551", "Corvallis", "OR");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(intentStr));
                startActivity(intent);

                return true;
            */
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
                    try {
                        map.setMyLocationEnabled(true);
                    } catch (SecurityException e){}

                    zoomCamera(10.0f);
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
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
        map = googleMap;

        // Request location permission
        if ( ! Helper.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION) && Build.VERSION.SDK_INT >= 23 ){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            try {
                map.setMyLocationEnabled(true);
            } catch (SecurityException e){}

            zoomCamera(10.0f);
        }

        // Make random points
        Random rand = new Random();
        for (int i = 0; i < 10; i++){
            // Add a marker and move the camera
            LatLng point = new LatLng(44.564055 + (rand.nextFloat() - 0.5)/10, -123.277011 + (rand.nextFloat() - 0.5)/10);
            map.addMarker(new MarkerOptions().position(point).title("Test point " + String.valueOf(i)));
            map.moveCamera(CameraUpdateFactory.newLatLng(point));
        }
    }

    public void zoomCamera(float zoom){
        map.moveCamera(CameraUpdateFactory.zoomTo(zoom));
    }


    public void setProgressVisibility(int visibility) {
        progressLoading.setVisibility(visibility);
    }

    // Update adapter data
    public void addBeer(List<Beer> data){
        for (Beer b : data){
            adapter_beers.addBeer(b);
        }
    }
    public void addBeer(Beer beer){
        adapter_beers.addBeer(beer);
    }
}
