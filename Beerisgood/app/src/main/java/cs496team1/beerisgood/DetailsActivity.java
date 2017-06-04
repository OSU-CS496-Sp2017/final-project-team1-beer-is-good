package cs496team1.beerisgood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    // Data
    Beer beer;

    // Views
    Toolbar toolbar;

    TextView description;
    TextView originalGravity;
    TextView IBU;
    TextView ABV;
    TextView glass;
    TextView isOrganic;
    ImageView label;
    TextView servingTemperatureDisplay;

    TextView availablility;
    TextView availablilityDescription;

    TextView category;
    TextView typeName;
    TextView typeDescription;

    TextView statusDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        // Get intent data
        Intent intent = getIntent();

        beer = (Beer) intent.getSerializableExtra("beer");

        // If data received
        if (beer != null){
            // Get views
            toolbar = (Toolbar) findViewById(R.id.toolbar);

            description = (TextView) findViewById(R.id.details_description);
            originalGravity = (TextView) findViewById(R.id.details_og);
            IBU = (TextView) findViewById(R.id.details_ibu);
            ABV = (TextView) findViewById(R.id.details_abv);
            glass = (TextView) findViewById(R.id.details_glass);
            isOrganic = (TextView) findViewById(R.id.details_organic);
            label = (ImageView) findViewById(R.id.details_image);
            servingTemperatureDisplay = (TextView) findViewById(R.id.details_serve);

            availablility = (TextView) findViewById(R.id.details_availability);
            availablilityDescription = (TextView) findViewById(R.id.details_availabilityDescription);

            category = (TextView) findViewById(R.id.details_category);
            typeName = (TextView) findViewById(R.id.details_typeName);
            typeDescription = (TextView) findViewById(R.id.details_typeDescription);

            //statusDisplay = (TextView) findViewById(R.id.details_statusDisplay);

            //Set up toolbar
            toolbar.setTitle(beer.name);
            //toolbar.setSubtitle(beer.name);
            toolbar.inflateMenu(R.menu.submenu_share);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { onBackPressed(); }
            });

            // Pull data from beer object
            description.setText(beer.description);

            originalGravity.setText(String.valueOf(beer.originalGravity));
            IBU.setText(String.valueOf(beer.IBU));
            ABV.setText(String.valueOf(beer.ABV));
            glass.setText(beer.glass);
            isOrganic.setText(String.valueOf(beer.isOrganic));
            //label.setImageDrawable(beer.description);
            servingTemperatureDisplay.setText(beer.servingTemperatureDisplay);

            availablility.setText(beer.availablility);
            availablilityDescription.setText(beer.availablilityDescription);

            category.setText(beer.category);
            typeName.setText(beer.typeName);
            typeDescription.setText(beer.typeDescription);

            //statusDisplay.setText(beer.statusDisplay);


        } else {
            Log.e("APP", "Could not load details activity");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.submenu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.toolbar_action_share: //Open sharing menu
                // Build string
                String str = "Beer";

                // Launch share intent
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, str);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                return true;
        }

        return true;
    }


}
