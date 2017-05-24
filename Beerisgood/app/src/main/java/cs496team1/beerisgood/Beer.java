package cs496team1.beerisgood;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zach on 4/28/2017.
 */

public class Beer implements Serializable {

    public Beer(){
        locationsThatServe = new ArrayList<>();
        breweriesThatBrew = new ArrayList<>();
    }

    public String id;
    public String name;
    public String description;

    public int IBU;
    public float ABV;
    public float originalGravity;
    public boolean isOrganic;

    public String glass;

    //public String label;

    public String servingTemperatureDisplay;

    public String availablility;
    public String availablilityDescription;

    public String category;
    public String typeName;
    public String typeDescription;

    public String statusDisplay;

    //public Beer beerVariation; // (parent)

    //public String foodPairings;

    public List<String> locationsThatServe;
    public List<String> breweriesThatBrew;

}
