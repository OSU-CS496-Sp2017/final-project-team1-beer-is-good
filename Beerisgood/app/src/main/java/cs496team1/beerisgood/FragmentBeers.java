package cs496team1.beerisgood;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Zach on 5/21/2017.
 */

public class FragmentBeers extends Fragment {

    RecyclerView recyclerView_beers;
    BeerAdapter adapter_beers;

    FloatingActionButton button_refresh;


    public FragmentBeers() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beers, container, false);

        // Get views
        recyclerView_beers = (RecyclerView) view.findViewById(R.id.recyclerview_beer);
        button_refresh = (FloatingActionButton) getActivity().findViewById(R.id.FAB_reload);


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
                if (dy > 10 && button_refresh.isShown()) { button_refresh.hide(); }
                else if (dy < 0 && !button_refresh.isShown()){button_refresh.show(); }
            }
        });


        return view;
    }

}