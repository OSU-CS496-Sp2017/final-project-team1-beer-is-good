package cs496team1.beerisgood;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Zach on 5/23/2017.
 */

public class CustomMarker implements ClusterItem {
    private LatLng _position;
    private String _title;
    private String _snippet;
    private String _id;

    public CustomMarker(LatLng position, String title, String snippet, String id){
        _position = position;
        _title = title;
        _snippet = snippet;
        _id = id;
    }

    public LatLng getPosition(){ return _position; }
    public String getTitle(){ return _title; }
    public String getSnippet(){ return _snippet; }

    public String getId(){ return _id; }
}
