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
import es.unican.bicis.common.TimestampUtils;
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
        setContentView(R.layout.activity_station_details_view);

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
            Double latitude = station.getLatitude();
            if (latitude == null) {
                tvLatitude.setText(R.string.noData);
            } else if (latitude > 90 || latitude < -90) {
                tvLatitude.setText(R.string.noData);
            } else {
                tvLatitude.setText(String.valueOf(latitude));
            }
            Double longitude = station.getLongitude();
            if (longitude == null) {
                tvLongitude.setText(R.string.noData);
            } else if (longitude > 180 || longitude < -180) {
                tvLongitude.setText(R.string.noData);
            } else {
                tvLongitude.setText(String.valueOf(longitude));
            }
            Integer usedSlots = station.getFreeBikes();
            if (usedSlots == null) {
                tvUsedSlots.setText(R.string.noData);
            } else if (usedSlots < 0) {
                tvUsedSlots.setText(R.string.noData);
            } else {
                tvUsedSlots.setText(String.valueOf(usedSlots));
            }
            Integer freeSlots = station.getEmptySlots();
            if (freeSlots == null) {
                tvFreeSlots.setText(R.string.noData);
            } else if (freeSlots < 0) {
                tvFreeSlots.setText(R.string.noData);
            } else {
                tvFreeSlots.setText(String.valueOf(freeSlots));
            }
            String id = station.getId();
            if (id != null) {
                tvId.setText(id);
            } else {
                tvId.setText(R.string.noData);
            }
            String timestamp = station.getTimestamp();
            if (timestamp != null) {
                timestamp = TimestampUtils.formatTimestamp(timestamp);
                tvTime.setText(timestamp);
            } else {
                tvTime.setText(R.string.noData);
            }
        } else {
            Toast.makeText(this, R.string.loadErrorStation, Toast.LENGTH_LONG).show();
        }
    }
}