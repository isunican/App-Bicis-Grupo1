package es.unican.bicis.activities.details;

import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.parceler.Parcels;

import es.unican.bicis.R;
import es.unican.bicis.model.Network;
import es.unican.bicis.model.Station;

/**
 * View that shows the details of a bike station from a selected network.
 * Since this view does not have business logic, it can be implemented as an activity directly
 */
public class StationDetailsView extends AppCompatActivity {

    /** Key for the intent that contains the station */
    public static final String INTENT_STATION = "INTENT_STATION";

    /**
     * @see AppCompatActivity#onCreate(Bundle)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // The default theme does not include a toolbar.
        // In this app the toolbar is explicitly declared in the layout
        // Set this toolbar as the activity ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        assert bar != null;  // to avoid warning in the line below
        bar.setDisplayHomeAsUpEnabled(true);  // show back button in action bar
        TextView tvStationName = findViewById(R.id.tvStName);
        TextView tvLatitude = findViewById(R.id.tvLatDisplay);
        TextView tvLongitude = findViewById(R.id.tvLongDisplay);
        TextView tvUsedSlots = findViewById(R.id.tvUsedSlotsDisplay);
        TextView tvFreeSlots = findViewById(R.id.tvFreeSlotsDisplay);
        TextView tvId = findViewById(R.id.tvIdDisplay);
        TextView tvTime = findViewById(R.id.tvTimeDisplay);
        TextView tvUid = findViewById(R.id.tvUidDisplay);

        // Get Station from the intent that triggered this activity
        Parcelable wrapped = IntentCompat.getParcelableExtra(getIntent(), INTENT_STATION, Parcelable.class);
        Station station = null;
        if (wrapped != null) {
            station = Parcels.unwrap(wrapped);
        }

        //Set texts on interface
        if (station != null) {
            String name = station.getName();
            if (name != null) {
                tvStationName.setText(name);
            } else {
                tvStationName.setText(R.string.noData);
            }
            double latitude = station.getLatitude();
            double longitude = station.getLongitude();
            tvLatitude.setText(String.valueOf(latitude));
            tvLongitude.setText(String.valueOf(longitude));
            int usedSlots = station.getFreeBikes();
            int freeSlots = station.getEmptySlots();
            tvUsedSlots.setText(String.valueOf(usedSlots));
            tvFreeSlots.setText(String.valueOf(freeSlots));
            String id = station.getId();
            if (id != null) {
                tvId.setText(id);
            } else {
                tvId.setText(R.string.noData);
            }
            String timestamp = station.getTimestamp();
            if (timestamp != null) {
                tvTime.setText(timestamp);
            } else {
                tvTime.setText(R.string.noData);
            }
            int uid = station.getUid();
            tvUid.setText(String.valueOf(uid));
        } else {
            Toast.makeText(this, R.string.loadErrorStation, Toast.LENGTH_LONG).show();
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_station_details_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}