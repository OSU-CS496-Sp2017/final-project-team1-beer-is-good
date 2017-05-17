package cs496team1.beerisgood;

import android.Manifest;
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
        button_refresh = (FloatingActionButton) findViewById(R.id.FAB_reload);
        nav_menu = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        frameLayout_mapholder = (FrameLayout) findViewById(R.id.framelayout_mapholder);

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
        beer1.IBU = 80.0f;
        beer1.ABV = 4.0f;
        Beer beer2 = new Beer();
        beer2.name = "Local Craft beer";
        beer2.description = "Expensive";
        beer2.IBU = 10.0f;
        beer2.ABV = 9.1f;
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

        // Add a marker and move the camera
        LatLng point1 = new LatLng(44.564055, -123.277011);
        map.addMarker(new MarkerOptions().position(point1).title("Test point"));
        map.moveCamera(CameraUpdateFactory.newLatLng(point1));
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
