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

    public String statusAPI;


    // Associated Brewery
    public String brewery_id;
    public String brewery_name;
    public String brewery_description;
    public String brewery_website;
    public int brewery_established;
    public boolean brewery_isOrganic;
    public String brewery_statusDisplay;




    //public String icon;
    //public String label_large;
}
