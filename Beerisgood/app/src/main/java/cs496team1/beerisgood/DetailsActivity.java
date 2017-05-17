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

    TextView tv_weather;
    TextView tv_weather_description;
    ImageView iv_weather_icon;
    TextView tv_temp;
    TextView tv_temp_min;
    TextView tv_temp_max;
    TextView tv_humidity;
    TextView tv_sealevel;
    TextView tv_groundlevel;
    TextView tv_cloudiness;
    TextView tv_windspeed;
    TextView tv_winddir;
    TextView tv_rainvolume;



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

            tv_weather = (TextView) findViewById(R.id.row_layout_weather_title);
            tv_weather_description = ( TextView ) findViewById(R.id.row_layout_weather_subtitle);
            iv_weather_icon = ( ImageView) findViewById(R.id.row_layout_weather_icon);
            tv_temp = ( TextView ) findViewById(R.id.row_layout_temp);
            tv_temp_min = ( TextView ) findViewById(R.id.row_layout_temp_subtitle_min);
            tv_temp_max = ( TextView ) findViewById(R.id.row_layout_temp_subtitle_max);
            tv_humidity = ( TextView ) findViewById(R.id.row_layout_humidity);
            tv_sealevel = ( TextView ) findViewById(R.id.row_layout_sealevel);
            tv_groundlevel = ( TextView ) findViewById(R.id.row_layout_grndlevel);
            tv_cloudiness = ( TextView ) findViewById(R.id.row_layout_cloudiness);
            tv_windspeed = ( TextView ) findViewById(R.id.row_layout_windspeed);
            tv_winddir = ( TextView ) findViewById(R.id.row_layout_winddir);
            tv_rainvolume = ( TextView ) findViewById(R.id.row_layout_rain3h);


            //Set up toolbar
            setSupportActionBar(toolbar);
            toolbar.setTitle(R.string.title_details);
            toolbar.setSubtitle(Helper.convertDateTime(this, beer.date_time_calc));
            toolbar.inflateMenu(R.menu.submenu_share);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { onBackPressed(); }
            });

            // Pull data from weatherMeasurment
            //tv_weather.setText(beer.weather.get(0).weather);
            //tv_weather_description.setText(beer.weather.get(0).weather_description);
            tv_temp.setText(String.valueOf(beer.temp));
            tv_temp_min.setText(String.valueOf(beer.temp_min));
            tv_temp_max.setText(String.valueOf(beer.temp_max));
            tv_humidity.setText(String.valueOf(beer.humidity));
            tv_sealevel.setText(String.valueOf(beer.sea_level));
            tv_groundlevel.setText(String.valueOf(beer.grnd_level));
            tv_cloudiness.setText(String.valueOf(beer.cloudiness));
            tv_windspeed.setText(String.valueOf(beer.wind_speed));
            tv_winddir.setText(String.valueOf(beer.wind_dir));
            tv_rainvolume.setText(String.valueOf(beer.rain_3h_volume));

            // Set icon
            String iconDrawable = "ic_weather_cloudy";
            for (Map.Entry<String, String> entry : Helper.weather_icons.entrySet()){
                //if (beer.weather.get(0).weather_icon.contains(entry.getKey())){
                //    iconDrawable = entry.getValue();
                //    break;
                //}
            }

            iv_weather_icon.setImageResource(getResources().getIdentifier(iconDrawable, "drawable", getPackageName()));

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
