package cs496team1.beerisgood;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Zach on 5/21/2017.
 */

public class FragmentBeers extends Fragment {

    RecyclerView recyclerView_beers;
    BeerAdapter adapter_beers;

    LinearLayout progressLoading;

    //FloatingActionButton button_refresh;

    int currentPage = 1;
    int maxPages = 1;

    public FragmentBeers() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make BreweryDB API beers call
        getBeers(1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beers, container, false);

        // Get views
        recyclerView_beers = (RecyclerView) view.findViewById(R.id.recyclerview_beer);
        progressLoading = (LinearLayout) view.findViewById(R.id.progress_database_loading);
        //button_refresh = (FloatingActionButton) view.findViewById(R.id.FAB_reload);


        //Linear Layout Manager
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView_beers.setLayoutManager(llm);

        adapter_beers = new BeerAdapter(getActivity());
        recyclerView_beers.setAdapter(adapter_beers);


        //Hide floating action button when recyclerView is scrolled
        recyclerView_beers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Load the next page of beers if necessary
                if (!recyclerView_beers.canScrollVertically(1)){ // Can't scroll down anymore
                    if (currentPage+1 < maxPages) {
                        // Loading indicator
                        progressLoading.setVisibility(View.VISIBLE);

                        // Add a page indicator?

                        // Make API request for more beer
                        getBeers(currentPage + 1);
                    }
                }


                //if (dy > 10 && button_refresh.isShown()) { button_refresh.hide(); }
                //else if (dy < 0 && !button_refresh.isShown()){button_refresh.show(); }
            }
        });

        /* Removed because the FAB is not actually necessary (or is it?)
        // Refresh button listener
        button_refresh.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                //getBeers(1);
            }
        });
        */


        return view;
    }


    public void getBeers(int page){
        // Make Async API call for beers
        AsyncRequest BeerRequest = new AsyncRequest();
        BeerRequest.execute(HttpRequest.buildBeerUrl(getActivity(), page), new CallBack(){
            @Override public void call(String data) {
                // Get objects from JSON string
                ArrayList<Integer> pageData = HttpRequest.formatBeerResponse(data);
                currentPage = pageData.get(0);
                maxPages = pageData.get(1);

                // Hide progress bar
                progressLoading.setVisibility(View.GONE);

                // Enable refresh button
                //button_refresh.show();

                // Refresh adapter
                adapter_beers.notifyDataSetChanged();
            }
        });
    }

}