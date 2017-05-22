package cs496team1.beerisgood;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zach on 5/21/2017.
 */

public class ObjectManager {

    static ArrayList<Location> _locations;
    static ArrayList<Beer> _beers;

    static {
        _locations = new ArrayList<>();
        _beers = new ArrayList<>();
    }


    // Getting
    public static ArrayList<Beer> getBeers() { return _beers; }
    public static ArrayList<Location> getLocations() { return _locations; }

    public static Location getLocation(String id) {
        for (int i = 0; i < countLocations(); i++){
            if (_locations.get(i).id.equals(id)){
                return _locations.get(i);
            }
        }
        return null;
    }
    public static Beer getBeer(String id) {
        for (int i = 0; i < countBeers(); i++){
            if (_beers.get(i).id.equals(id)){
                return _beers.get(i);
            }
        }
        return null;
    }

    // Addition and removal
    public static void addBeer(Beer beer){ _beers.add(beer); }
    public static void addBeers(List<Beer> beers){ _beers.addAll(beers); }
    public static void addLocation(Location location){ _locations.add(location); }
    public static void addLocations(List<Location> locations){ _locations.addAll(locations); }

    public static void removeBeer(Beer beer){ _beers.remove(beer); }
    public static void removeLocation(Location location){ _locations.remove(location); }


    // Counts
    public static int countBeers(){ return _beers.size(); }
    public static int countLocations(){ return _locations.size(); }

    // Sorting and filtering

}
