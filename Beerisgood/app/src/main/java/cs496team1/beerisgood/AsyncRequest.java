package cs496team1.beerisgood;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Zach on 5/21/2017.
 */

public class AsyncRequest extends AsyncTask<Object, String, String> {

    private String _response = null;

    private CallBack _callBack = null;

    //public AsyncResponse delegate = null;


    @Override protected void onPreExecute() {
        //delegate.setProgressVisibility(View.VISIBLE);
    }

    @Override protected String doInBackground(Object... params) {
        try {
            // Decode parameters
            String url = (String) params[0];
            _callBack = (CallBack) params[1];

            // Make request
            _response = HttpRequest.run(url);

            //response = "Hi";

            Log.e("APP", "Completed request - " + url);

        } catch (Exception ex){ _response = ex.toString(); }

        return _response;
    }

    @Override protected void onProgressUpdate(String... text) { }

    @Override protected void onPostExecute(String result) {

        // Call back after request is done
        if (_callBack != null) {
            _callBack.call(_response);
        }

        //delegate.setProgressVisibility(View.GONE);

        //Log.e("ASYNCpost", response);
    }
}