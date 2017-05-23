package cs496team1.beerisgood;

/**
 * Created by Zach on 5/21/2017.
 */

import android.content.Context;
import android.util.Log;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Zach on 4/27/2017.
 */

public class HttpRequest {

    public static String buildBeerUrl(Context c){
        // Gather strings
        String api_base = c.getString(R.string.brewerydb_base_url);
        String api_key_param = c.getString(R.string.brewerydb_base_key);
        String api_key = c.getString(R.string.brewerydb_api_key);
        String api_beer = c.getString(R.string.brewerydb_base_beer);
        String api_additional = c.getString(R.string.brewerydb_beer_additional);

        return api_base + api_beer + "?" + api_additional + api_key_param + "=" + api_key;
    }

    public static String buildLocationUrl(Context c, String locality, String region){
        // Gather strings
        String api_base = c.getString(R.string.brewerydb_base_url);
        String api_key_param = c.getString(R.string.brewerydb_base_key);
        String api_key = c.getString(R.string.brewerydb_api_key);
        String api_location = c.getString(R.string.brewerydb_base_location);
        //String api_additional = c.getString(R.string.brewerydb_brewery_additional);
        String api_additional = "locality=" + locality + "&region=" + region + "&";

        return api_base + api_location + "?" + api_additional + api_key_param + "=" + api_key;
    }


    public static String run(String url) throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        //Headers responseHeaders = response.headers();
        //for (int i = 0; i < responseHeaders.size(); i++) {
            //Log.v("RESPONSEHead", responseHeaders.name(i) + ": " + responseHeaders.value(i));
        //}

        return response.body().string();
    }


    /*
    public static ArrayList<Beer> formatBeerResponse(Context c, String response){
        ArrayList<Beer> beers = new ArrayList<>();

        try {
            // Convert to JSON
            JSONObject jsonObj = new JSONObject(response);

            // List item of response
            JSONArray JSON_list = getJsonArray(jsonObj, "list");

            for (int i = 0; i < JSON_list.length(); i++) {
                // JSON weather Measurement
                JSONObject JSON_wm = getJsonObj(JSON_list, i);

                // Make data container
                Beer obj = new Beer();

                // Read elements from JSON
                obj.date_forecast = tryLong(getJson(JSON_wm, "dt"));

                // Main object
                JSONObject JSON_main = getJsonObj(JSON_wm,"main");
                obj.temp = tryFloat(getJson(JSON_main, "temp"));
                obj.temp_min = tryFloat(getJson(JSON_main, "temp_min"));
                obj.temp_max = tryFloat(getJson(JSON_main, "temp_max"));

                obj.pressure = tryFloat(getJson(JSON_main, "pressure"));
                obj.sea_level = tryFloat(getJson(JSON_main, "sea_level"));
                obj.grnd_level = tryFloat(getJson(JSON_main, "grnd_level"));

                obj.humidity = tryFloat(getJson(JSON_main, "humidity"));

                // Weather array
                JSONArray JSON_weather = getJsonArray(JSON_wm, "weather");
                for (int j = 0; j < JSON_weather.length(); j++) {
                    // JSON weather description
                    JSONObject JSON_wd = getJsonObj(JSON_weather, j);

                    // Make data container
                    WeatherDesc wd = new WeatherDesc();

                    // JSON weather Measurement
                    wd.weather_id = tryInt(getJson(JSON_wd, "id"));
                    wd.weather = getJson(JSON_wd, "main");
                    wd.weather_description = getJson(JSON_wd, "description");
                    wd.weather_icon = getJson(JSON_wd, "icon");

                    obj.weather.add(wd);
                }


                // Clouds object
                JSONObject JSON_clouds = getJsonObj(JSON_wm,"clouds");
                obj.cloudiness = tryInt(getJson(JSON_clouds, "all"));

                // Wind object
                JSONObject JSON_wind = getJsonObj(JSON_wm,"wind");
                obj.wind_speed = tryFloat(getJson(JSON_wind, "speed"));
                obj.wind_dir = tryFloat(getJson(JSON_wind, "deg"));

                // Rain object
                JSONObject JSON_rain = getJsonObj(JSON_wm, "rain");
                obj.rain_3h_volume = tryFloat(getJson(JSON_rain, "3h"));
                //wm.snow_3h_volume = tryFloat((JSON_wm, "dt")); // Does not exist in JSON object?

                obj.date_time_calc = getJson(JSON_wm, "dt_txt");

                beers.add(obj);
            }

        } catch (Exception e) {
            Helper.Log(c, "JSON-Exception:", e.toString());
            WeatherMeasurement wm = new WeatherMeasurement();
            wm.date_time_calc = e.toString();
            weather_measurements.clear();
            weather_measurements.add(wm);
        }

        return weather_measurements;
    }
*/
    public static ArrayList<Location> formatLocationResponse(Context c, String response){
        ArrayList<Location> locations = new ArrayList<>();

        try {
            // Convert to JSON
            JSONObject jsonObj = new JSONObject(response);

            // List item of response
            JSONArray JSON_list = getJsonArray(jsonObj, "data");

            for (int i = 0; i < JSON_list.length(); i++) {
                // Individual list item
                JSONObject JSON_loc_item = getJsonObj(JSON_list, i);

                // Make data container
                Location loc = new Location();

                // Read elements from JSON
                loc.id = getJson(JSON_loc_item, "id");
                loc.name = getJson(JSON_loc_item, "name");
                loc.streetAddress = getJson(JSON_loc_item, "streetAddress");
                loc.city = getJson(JSON_loc_item, "locality");
                loc.region = getJson(JSON_loc_item, "region");

                loc.postalcode = tryInt(getJson(JSON_loc_item, "postalCode"));

                loc.phone = getJson(JSON_loc_item, "phone");
                loc.website = getJson(JSON_loc_item, "website").replace("\\", ""); // Remove extra '\' added by BreweryDB

                loc.hoursOfOperation = getJson(JSON_loc_item, "hoursOfOperation");

                loc.latitude = tryFloat(getJson(JSON_loc_item, "latitude"));
                loc.longitude = tryFloat(getJson(JSON_loc_item, "longitude"));

                loc.isPrimary = getJson(JSON_loc_item, "isPrimary");
                loc.isClosed = getJson(JSON_loc_item, "isClosed");

                loc.locationTypeDisplay = getJson(JSON_loc_item, "locationTypeDisplay");

                loc.yearOpened = tryInt(getJson(JSON_loc_item, "yearOpened"));


                // Associated brewery
                JSONObject JSON_brewery = getJsonObj(JSON_loc_item, "brewery");
                    loc.brewery_id = getJson(JSON_loc_item, "breweryId");
                    loc.brewery_name = getJson(JSON_brewery, "name");
                    loc.brewery_description = getJson(JSON_brewery, "description");
                    loc.brewery_website = getJson(JSON_brewery, "website");
                    loc.brewery_established = tryInt(getJson(JSON_brewery, "established"));
                    loc.brewery_isOrganic = tryBool(getJson(JSON_brewery, "isOrganic"));
                    loc.brewery_statusDisplay = getJson(JSON_brewery, "statusDisplay");

                locations.add(loc);
            }

        } catch (Exception e) {}

        return locations;
    }



    public static JSONObject getJsonObj(JSONObject obj, String title){
        if (obj != null && obj.has(title) && !obj.isNull(title)) {
            try {
                return obj.getJSONObject(title);
            } catch (JSONException ex){
                return null;
            }
        } else {
            return null;
        }
    }
    public static JSONObject getJsonObj(JSONArray arr, int index){
        if (arr != null && arr.length() > 0 && !arr.isNull(index)) {
            try {
                return arr.getJSONObject(index);
            } catch (JSONException ex){
                return null;
            }
        } else {
            return null;
        }
    }

    public static JSONArray getJsonArray(JSONObject obj, String title){
        if (obj != null && obj.has(title) && !obj.isNull(title)) {
            try {
                return obj.getJSONArray(title);
            } catch (JSONException ex){
                return null;
            }
        } else {
            return null;
        }
    }

    public static String getJson(JSONObject obj, String title){
        if (obj != null && obj.has(title) && !obj.isNull(title)) {
            try {
                return obj.getString(title);
            } catch (JSONException ex){
                return "";
            }
        } else {
            return "";
        }
    }

    public static float tryFloat(String in){
        try {
            return Float.parseFloat(in);
        } catch (NumberFormatException ex){
            return 0;
        }
    }
    public static int tryInt(String in){
        try {
            return Integer.parseInt(in);
        } catch (NumberFormatException ex){
            return 0;
        }
    }
    public static long tryLong(String in){
        try {
            return Long.parseLong(in);
        } catch (NumberFormatException ex){
            return 0;
        }
    }
    public static boolean tryBool(String in){
        return Boolean.parseBoolean(in);
    }

}

