package cs496team1.beerisgood;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hessro on 4/25/17.
 */

public class BeerAdapter extends RecyclerView.Adapter<BeerAdapter.ForecastItemViewHolder> {

    private ArrayList<Beer> mData;

    private Context parent;

    public BeerAdapter(Context c){
        parent = c;
        mData = new ArrayList<>();
    }


    public void addBeer(Beer beer){
        mData.add(beer);
        notifyItemInserted(mData.size()-1);
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }

    @Override
    public ForecastItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_layout_beer, parent, false);
        return new ForecastItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastItemViewHolder holder, int position) {
        holder.bind(mData.get(position));
    }


    class ForecastItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Beer beer;

        LinearLayout base;

        ImageView icon_name;

        TextView beer_name;
        TextView beer_desc;
        TextView beer_ibu;
        TextView beer_abv;



        public ForecastItemViewHolder(View itemView) {
            super(itemView);

            base = (LinearLayout) itemView.findViewById(R.id.row_layout_weather_base);

            icon_name = (ImageView) itemView.findViewById(R.id.row_layout_icon_name);

            beer_name = (TextView)itemView.findViewById(R.id.row_layout_text_name);
            beer_desc = (TextView)itemView.findViewById(R.id.row_layout_text_desc);
            beer_ibu = (TextView)itemView.findViewById(R.id.row_layout_text_ibu);
            beer_abv = (TextView)itemView.findViewById(R.id.row_layout_text_abv);

            base.setOnClickListener(this);
        }

        public void bind(Beer beer) {
            this.beer = beer;


            beer_name.setText(beer.name);
            beer_desc.setText(beer.description);

            beer_ibu.setText(String.valueOf(beer.IBU));
            beer_abv.setText(String.valueOf(beer.ABV));

            //mForecastTextView.setText(forecast);

            //datetime.setText(Helper.convertDateTime(parent, wm.date_time_calc));

            //weather_desc.setText(wm.weather.get(0).weather);
            //weather_desc_detail.setText(wm.weather.get(0).weather_description);

            //String iconDrawable = "ic_weather_cloudy";
            //for (Map.Entry<String, String> entry : Helper.weather_icons.entrySet()){
                //if (wm.weather.get(0).weather_icon.contains(entry.getKey())){
                //    iconDrawable = entry.getValue();
                //    break;
                //}
            //}

            //weather_icon.setImageResource(parent.getResources().getIdentifier(iconDrawable, "drawable", parent.getPackageName()));

            //weather_temp.setText(String.valueOf(Math.round(wm.temp)));
            //weather_temp_min.setText(String.valueOf(Math.round(wm.temp_min)));
            //weather_temp_max.setText(String.valueOf(Math.round(wm.temp_max)));
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(parent, DetailsActivity.class);
            intent.putExtra("beer", beer);
            parent.startActivity(intent);
        }

        /*
        for (WeatherMeasurement wm : weatherMeasurements) {
            Helper.Log(this, "WeatherMeasurement", String.valueOf(weatherMeasurements.indexOf(wm)));
            Helper.Log(this, "Date", String.valueOf(wm.date_forecast));
            Helper.Log(this, "DateCalc", wm.date_time_calc);
            Helper.Log(this, "Temp", String.valueOf(wm.temp));
            Helper.Log(this, "Temp-min", String.valueOf(wm.temp_min));
            Helper.Log(this, "Temp-max", String.valueOf(wm.temp_max));
            for (WeatherDesc wd : wm.weather) {
                Helper.Log(this, "Weather", String.valueOf(wd.weather));
                Helper.Log(this, "WeatherDesc", String.valueOf(wd.weather_description));
            }
            Helper.Log(this, "rain_3h", String.valueOf(wm.rain_3h_volume));
        }
        */
    }
}
