package cs496team1.beerisgood;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Zach on 4/28/2017.
 */

public class Beer implements Serializable {

    public Beer(){}
    public Beer(String err){
        date_time_calc = err;
    }


    public long date_forecast;
    public String date_time_calc;

    public float temp;
    public float temp_min;
    public float temp_max;

    public float pressure;
    public float sea_level;
    public float grnd_level;

    public float humidity;

    //public ArrayList<WeatherDesc> weather = new ArrayList<>();

    public int cloudiness;

    public float wind_speed;
    public float wind_dir;

    public float rain_3h_volume;

    public float snow_3h_volume;

}
