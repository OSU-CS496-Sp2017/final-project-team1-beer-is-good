package cs496team1.beerisgood;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivitySettings extends AppCompatActivity {

    //Views
    Toolbar toolbar;

    TextView settingscard_title;

    LinearLayout settingsitem_example1;
    LinearLayout settingsitem_example2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get views
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        settingscard_title = (TextView) findViewById(R.id.settingscard_title);
        settingsitem_example1 = (LinearLayout) findViewById(R.id.settingsitem_example1);
        settingsitem_example2 = (LinearLayout) findViewById(R.id.settingsitem_example2);


        //Set up toolbar
        toolbar.setTitle(R.string.title_settings);
        //toolbar.setSubtitle(R.string.assignment_no);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { onBackPressed(); }
        });



        // Settings item click listeners
        settingsitem_example1.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Toast.makeText(ActivitySettings.this, "Hello Settings Item 1", Toast.LENGTH_SHORT).show();
            }
        });
        settingsitem_example2.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Toast.makeText(ActivitySettings.this, "Hello Settings Item 2", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
