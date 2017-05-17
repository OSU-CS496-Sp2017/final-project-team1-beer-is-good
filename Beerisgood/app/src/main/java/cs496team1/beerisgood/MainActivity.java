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
import android.widget.LinearLayout;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    //Views
    Toolbar toolbar;
    private RecyclerView mForecastListRV;
    private BeerAdapter mForecastAdapter;

    private LinearLayout progressLoading;
    FloatingActionButton button_refresh;

    SupportMapFragment mapView;
    GoogleMap map;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get views
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mForecastListRV = (RecyclerView)findViewById(R.id.rv_forecast_list);
        progressLoading = (LinearLayout)findViewById(R.id.progress_database_loading);
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
        mForecastListRV.setLayoutManager(llm);

        mForecastAdapter = new BeerAdapter(this);
        mForecastListRV.setAdapter(mForecastAdapter);

        //Hide floating action button when recyclerView is scrolled
        mForecastListRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    public void setProgressVisibility(int visibility) {
        progressLoading.setVisibility(visibility);
    }

    // Update adapter data
    public void addBeer(List<Beer> data){
        for (Beer b : data){
            mForecastAdapter.addBeer(b);
        }
    }
    public void addBeer(Beer beer){
        mForecastAdapter.addBeer(beer);
    }
}
