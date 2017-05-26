package cs496team1.beerisgood;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class ObjectManager {

    private static HashMap<String, Location> _locations;
    private static HashMap<String, Brewery> _breweries;
    private static HashMap<String, Beer> _beers;

    private static boolean has_loaded_location_initially = false;
    private static boolean has_loaded_beer_initially = false;

    static {
        _locations = new HashMap<>();
        _breweries = new HashMap<>();
        _beers = new HashMap<>();
    }

    public static void set_has_loaded_location_initially(){ has_loaded_location_initially = true; }
    public static void set_has_loaded_beer_initially(){ has_loaded_beer_initially = true; }
    public static boolean has_loaded_location_initially() { return has_loaded_location_initially; }
    public static boolean has_loaded_beer_initially() { return has_loaded_beer_initially; }


    // Getting
    public static String[] getBeerKeys() { return _beers.keySet().toArray(new String[_beers.size()]); }
    public static String[] getLocationKeys() { return _locations.keySet().toArray(new String[_locations.size()]); }
    public static String[] getBreweryKeys() { return _breweries.keySet().toArray(new String[_breweries.size()]); }

    public static Collection<Beer> getBeers() { return _beers.values(); }
    public static Collection<Location> getLocations() { return _locations.values(); }
    public static Collection<Brewery> getBreweries() { return _breweries.values(); }

    public static Location getLocation(String id) { return _locations.get(id); }
    public static Beer getBeer(String id) { return _beers.get(id); }
    public static Brewery getBrewery(String id) { return _breweries.get(id); }

    // Addition and removal
    public static void addBeer(Beer beer){
        if (!containsBeer(beer.id)) {
            _beers.put(beer.id, beer);
        }
    }
    public static void addBeers(List<Beer> beers) {
        for (Beer b : beers)
            addBeer(b);
    }
    public static void addLocation(Location location){
        if (!containsLocation(location.id)) {
            _locations.put(location.id, location);
        }
    }
    public static void addLocations(List<Location> locations){
        for (Location l : locations)
            addLocation(l);
    }
    public static void addBrewery(Brewery brewery){
        if (!containsBrewery(brewery.id)) {
            _breweries.put(brewery.id, brewery);
        }
    }
    public static void addBreweries(List<Brewery> breweries) {
        for (Brewery b : breweries)
            addBrewery(b);
    }

    public static void removeBeer(Beer beer){ _beers.remove(beer.id); }
    public static void removeLocation(Location location){ _locations.remove(location.id); }
    public static void removeBrewery(Brewery brewery){ _breweries.remove(brewery.id); }

    // Contains
    public static boolean containsBeer(String id){ return getBeer(id) != null; }
    public static boolean containsLocation(String id){ return getLocation(id) != null; }
    public static boolean containsBrewery(String id){ return getBrewery(id) != null; }

    // Counts
    public static int countBeers(){ return _beers.size(); }
    public static int countLocations(){ return _locations.size(); }
    public static int countBreweries(){ return _breweries.size(); }

    // Sorting and filtering

}
