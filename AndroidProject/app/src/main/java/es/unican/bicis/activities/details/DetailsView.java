package es.unican.bicis.activities.details;

import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.IntentCompat;

import org.parceler.Parcels;

import es.unican.bicis.R;
import es.unican.bicis.model.Location;
import es.unican.bicis.model.Network;

/**
 * View that shows the details of one bike sharing network. Since this view does not have business logic,
 * it can be implemented as an activity directly, without the MVP pattern.
 */
public class DetailsView extends AppCompatActivity {

    /** Key for the intent that contains the network */
    public static final String INTENT_NETWORK = "INTENT_NETWORK";

    /**
     * @see AppCompatActivity#onCreate(Bundle)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);

        // The default theme does not include a toolbar.
        // In this app the toolbar is explicitly declared in the layout
        // Set this toolbar as the activity ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        assert bar != null;  // to avoid warning in the line below
        bar.setDisplayHomeAsUpEnabled(true);  // show back button in action bar

        // Link to view elements
        TextView tvName = findViewById(R.id.tvName);
        TextView tvCity = findViewById(R.id.tvCity);
        TextView tvEbikes = findViewById(R.id.tvEbikes);
        TextView tvCompanysNum = findViewById(R.id.tvCompanysNum);
        ListView lvCompanys = findViewById(R.id.lvCompanys);

        // Get Network from the intent that triggered this activity
        Parcelable wrapped = IntentCompat.getParcelableExtra(getIntent(), INTENT_NETWORK, Parcelable.class);
        Network network = null;
        if (wrapped != null) {
            network = Parcels.unwrap(wrapped);
        }

        // Set Texts
        if (network != null) {
            String name = network.getName();
            if (name != null) {
                tvName.setText(name);
            } else {
                tvName.setText(R.string.noData);
            }

            Boolean ebikes = network.isEbikes();
            if (ebikes != null) {
                if (ebikes) {
                    tvEbikes.setText(R.string.yes);
                } else {
                    tvEbikes.setText(R.string.no);
                }
            } else {
                tvEbikes.setText(R.string.noData);
            }

            String[] companys = network.getCompany();
            if (companys != null) {
                tvCompanysNum.setText(String.valueOf(companys.length));
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        companys
                );
                lvCompanys.setAdapter(adapter);
            } else {
                tvCompanysNum.setText(R.string.noData);
            }

            Location location = network.getLocation();
            if (location != null) {
                String city = location.getCity();
                String country = location.getCountry();
                if (city == null) {
                    city = getString(R.string.noData);
                }
                if (country == null) {
                    country = getString(R.string.noData);
                }
                tvCity.setText(String.format("%s (%s)", city, country));
            } else {
                tvCity.setText(R.string.noData);
            }

            boolean hasCompanys = companys != null && companys.length > 0;
            if (!hasCompanys) {
                Toast.makeText(this, R.string.noDetailInfo, Toast.LENGTH_LONG).show();
            }
        }
    }
}