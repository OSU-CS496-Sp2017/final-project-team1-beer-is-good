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

import java.util.List;
import java.util.Map;

/**
 * Created by hessro on 4/25/17.
 */

public class BeerAdapter extends RecyclerView.Adapter<BeerAdapter.ForecastItemViewHolder> {

    private List<Beer> mData;

    private Context parent;

    public BeerAdapter(Context c){
        parent = c;
    }


    public void addBeer(Beer beer){
        mData.add(beer);
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
        Beer wm;

        LinearLayout base;

        TextView datetime;

        TextView weather_desc;
        TextView weather_desc_detail;
        ImageView weather_icon;

        TextView weather_temp;
        TextView weather_temp_min;
        TextView weather_temp_max;


        public ForecastItemViewHolder(View itemView) {
            super(itemView);

            base = (LinearLayout) itemView.findViewById(R.id.row_layout_weather_base);
            datetime = (TextView)itemView.findViewById(R.id.row_layout_datetime);
            weather_desc = (TextView)itemView.findViewById(R.id.row_layout_weather_title);
            weather_desc_detail = (TextView)itemView.findViewById(R.id.row_layout_weather_subtitle);
            weather_icon = (ImageView) itemView.findViewById(R.id.row_layout_weather_icon);
            weather_temp = (TextView)itemView.findViewById(R.id.row_layout_temp);
            weather_temp_min = (TextView)itemView.findViewById(R.id.row_layout_temp_subtitle_min);
            weather_temp_max = (TextView)itemView.findViewById(R.id.row_layout_temp_subtitle_max);

            base.setOnClickListener(this);
        }

        public void bind(Beer wm) {
            this.wm = wm;
            //mForecastTextView.setText(forecast);

            datetime.setText(Helper.convertDateTime(parent, wm.date_time_calc));

            //weather_desc.setText(wm.weather.get(0).weather);
            //weather_desc_detail.setText(wm.weather.get(0).weather_description);

            String iconDrawable = "ic_weather_cloudy";
            for (Map.Entry<String, String> entry : Helper.weather_icons.entrySet()){
                //if (wm.weather.get(0).weather_icon.contains(entry.getKey())){
                //    iconDrawable = entry.getValue();
                //    break;
                //}
            }

            weather_icon.setImageResource(parent.getResources().getIdentifier(iconDrawable, "drawable", parent.getPackageName()));

            weather_temp.setText(String.valueOf(Math.round(wm.temp)));
            weather_temp_min.setText(String.valueOf(Math.round(wm.temp_min)));
            weather_temp_max.setText(String.valueOf(Math.round(wm.temp_max)));
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(parent, DetailsActivity.class);
            intent.putExtra("wm", wm);
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
