package cs496team1.beerisgood;

/**
 * Created by Zach on 5/17/2017.
 */

public class Location {

    public Location(){}

    public String id;
    public String name;

    public String streetAddress;
    public String city;
    public String region;
    public int postalcode;

    public String phone;
    public String website;

    public String hoursOfOperation;

    public float latitude;
    public float longitude;

    public String isPrimary;
    public String isClosed;

    public String locationTypeDisplay;

    public int yearOpened;

    //public String statusAPI;


    // Associated Brewery
    public String breweryID;


    // Formatting methods
    public String getName(){ // A lot of locations are listed as "main brewery", and their associated brewery is listed as the actual location. This method switches the two names when appropriate.
        String brewery_name;
        Brewery b = ObjectManager.getBrewery(breweryID);
        if (b!=null){
            brewery_name = b.name;

            if (!name.equals("")){
                if (name.toLowerCase().contains("main")){ return brewery_name; }
            }
        }

        return name;
    }
    public String getHoursOfOperationFormatted(){
        return hoursOfOperation.replaceAll("\\\\r", ""); // Literally \r
    }
    public String getWebsite(){
        String brewery_website;
        Brewery b = ObjectManager.getBrewery(breweryID);
        if (b!=null){
            brewery_website = b.website;

            if (!brewery_website.equals("")){ return brewery_website; }
        }

        return website;
    }

    public String getBreweryDescription(){
        String brewery_description;
        Brewery b = ObjectManager.getBrewery(breweryID);
        if (b!=null){
            return b.description;
        }
        return null;
    }

    public int getBreweryEstablished(){
        int brewery_established;
        Brewery b = ObjectManager.getBrewery(breweryID);
        if (b!=null){
            return b.established;
        }
        return 0;
    }

}
