package cs496team1.beerisgood;

/**
 * Created by Zach on 5/23/2017.
 */

public class Brewery {

    public Brewery(){}

    public String id;
    public String name;
    public String description;
    public String website;
    public int established;
    public boolean isOrganic;
    public String statusDisplay;

    // Formatting methods
    public String getType(){
        String[] types = {"Brewery", "Cidery", "Meadery"};
        if (!name.equals("")){
            for (String item : types) {
                if (name.toLowerCase().contains(item.toLowerCase())){ return item; }
            }
            return name;
        }
        return name;
    }

}
