package cs496team1.beerisgood;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Zach on 4/28/2017.
 */

public class Beer implements Serializable {

    public Beer(){}

    public String id;
    public String name;
    public String description;
    public int year;

    public int IBU;
    public float ABV;
    public float originalGravity;

    //public String glasswareId;
    public String glass;

    //styleId
    //style

    public boolean isOrganic;

    public String label;

    public String servingTemperatureDisplay;

    //public String status;
    public String statusDisplay;

    //availableId
    public String available;

    //beerVariationId
    public Beer beerVariation; // (parent)

    public String foodPairings;

}
