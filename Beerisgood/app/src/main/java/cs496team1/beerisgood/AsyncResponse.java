package cs496team1.beerisgood;

/**
 * Created by Zach on 5/21/2017.
 */

public interface AsyncResponse {
    void processFinish(String output, boolean updatePrefs);
    void setProgressVisibility(int visible);
}
