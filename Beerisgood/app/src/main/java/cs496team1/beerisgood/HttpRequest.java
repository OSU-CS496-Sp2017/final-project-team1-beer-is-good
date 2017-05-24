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

    public static String buildBeerUrl(Context c, int page){
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


    public static ArrayList<Integer> formatBeerResponse(String response){
        //ArrayList<Beer> beers = new ArrayList<>();
        int current_page = 0;
        int max_pages = 0;

        try {
            // Convert to JSON
            JSONObject JSON_base = new JSONObject(response);

            // Current page and number of pages
            current_page = tryInt(getJson(JSON_base, "currentPage"));
            max_pages = tryInt(getJson(JSON_base, "numberOfPages"));

            // List item of response
            JSONArray JSON_list = getJsonArray(JSON_base, "data");

            for (int i = 0; i < JSON_list.length(); i++) {
                // Individual list item
                JSONObject JSON_beer_item = getJsonObj(JSON_list, i);
                Beer beer = getBeer(JSON_beer_item);
                ObjectManager.addBeer(beer);
                //beers.add(beer);
            }
        } catch (Exception e) {}

        // Return
        ArrayList<Integer> arr = new ArrayList<>();
        arr.add(current_page);
        arr.add(max_pages);
        return arr;
    }

    public static ArrayList<Location> formatLocationResponse(String response){
        ArrayList<Location> locations = new ArrayList<>();
        try {
            // Convert to JSON
            JSONObject JSON_base = new JSONObject(response);

            // List item of response
            JSONArray JSON_list = getJsonArray(JSON_base, "data");

            for (int i = 0; i < JSON_list.length(); i++) {
                // Individual list item
                JSONObject JSON_loc_item = getJsonObj(JSON_list, i);
                Location loc = getLocation(JSON_loc_item);
                ObjectManager.addLocation(loc);
                locations.add(loc);
            }

        } catch (Exception e) {}

        return locations;
    }


    public static Location getLocation(JSONObject JSON_loc_item){
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
        Brewery brewery = getBrewery(JSON_brewery);
        loc.breweryID = brewery.id;

        // Add brewery to objectManager
        ObjectManager.addBrewery(brewery);

        return loc;
    }
    public static Beer getBeer(JSONObject JSON_beer_item){
        // Make data container
        Beer beer = new Beer();

        // Read elements from JSON
        beer.id = getJson(JSON_beer_item, "id");
        beer.name = getJson(JSON_beer_item, "nameDisplay");
        beer.description = getJson(JSON_beer_item, "description");

        beer.IBU = tryInt(getJson(JSON_beer_item, "ibu"));
        beer.ABV = tryFloat(getJson(JSON_beer_item, "abv"));
        beer.originalGravity = tryFloat(getJson(JSON_beer_item, "originalGravity"));
        beer.isOrganic = tryBool(getJson(JSON_beer_item, "isOrganic"));

        // Glass to serve it in
        JSONObject JSON_glass = getJsonObj(JSON_beer_item, "golass");
        beer.glass = getJson(JSON_glass, "name");


        // Availability
        JSONObject JSON_available = getJsonObj(JSON_beer_item, "available");
        beer.availablility = getJson(JSON_available, "name");
        beer.availablilityDescription = getJson(JSON_available, "description");

        // Style and category
        JSONObject JSON_style = getJsonObj(JSON_beer_item, "style");
        JSONObject JSON_category = getJsonObj(JSON_style, "category");
        beer.category = getJson(JSON_category, "name");
        beer.typeName = getJson(JSON_style, "name");
        beer.typeDescription = getJson(JSON_style, "description");

        beer.servingTemperatureDisplay = getJson(JSON_beer_item, "servingTemperatureDisplay");

        beer.statusDisplay = getJson(JSON_beer_item, "statusDisplay");

        // Associated locations
        JSONArray JSON_breweries = getJsonArray(JSON_beer_item, "breweries");

        for (int i = 0; i < JSON_breweries.length(); i++) {
            // Individual list item
            JSONObject JSON_brewery = getJsonObj(JSON_breweries, i);

            // Get brewery
            Brewery brewery = getBrewery(JSON_brewery);
            ObjectManager.addBrewery(brewery);

            JSONArray JSON_locations = getJsonArray(JSON_brewery, "locations");
            for (int j = 0; j < JSON_locations.length(); j++) {
                // Individual list item
                JSONObject JSON_location = getJsonObj(JSON_locations, j);

                // Get location
                Location location = getLocation(JSON_location);
                ObjectManager.addLocation(location);

                beer.locationsThatServe.add(location.id);
            }

            beer.breweriesThatBrew.add(brewery.id);
        }

        return beer;
    }
    public static Brewery getBrewery(JSONObject JSON_brewery_item){
        // Make data container
        Brewery brewery = new Brewery();

        // Read elements from JSON
        brewery.id = getJson(JSON_brewery_item, "id");
        brewery.name = getJson(JSON_brewery_item, "name");;
        brewery.description = getJson(JSON_brewery_item, "description");
        brewery.website = getJson(JSON_brewery_item, "website");
        brewery.established = tryInt(getJson(JSON_brewery_item, "established"));
        brewery.isOrganic = tryBool(getJson(JSON_brewery_item, "isOrganic"));
        brewery.statusDisplay = getJson(JSON_brewery_item, "statusDisplay");

        return brewery;
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
        // Short-circuit for Y/N
        if (in.toLowerCase().equals("y")){ return true; }
        if (in.toLowerCase().equals("n")){ return false; }
        return Boolean.parseBoolean(in);
    }

}

