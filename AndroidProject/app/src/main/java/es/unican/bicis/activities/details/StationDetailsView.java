package es.unican.bicis.activities.details;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import org.parceler.Parcels;

import java.util.List;

import es.unican.bicis.R;
import es.unican.bicis.common.TimestampUtils;
import es.unican.bicis.model.Incidence;
import es.unican.bicis.model.Station;

// TODO: Uncomment when Hilt and repository injection are ready
// import javax.inject.Inject;
// import dagger.hilt.android.AndroidEntryPoint;
// import es.unican.bicis.repository.IIncidencesRepository;

/**
 * View that shows the details of a bike station from a selected network and its incidences.
 */
// @AndroidEntryPoint // Uncomment when repository injection is configured
public class StationDetailsView extends AppCompatActivity implements IStationDetailsContract.View {

    /** Key for the intent that contains the station */
    public static final String INTENT_STATION = "INTENT_STATION";

    /** The presenter of this view */
    private IStationDetailsContract.Presenter presenter;

    /** Station of the view */
    private Station station;

    // TODO: Uncomment when repository injection is configured
    // @Inject
    // IIncidencesRepository repository;

    /**
     * @see AppCompatActivity#onCreate(Bundle)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details_view);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setDisplayHomeAsUpEnabled(true);

        // Avoid overlap with system notches/bars
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ConstraintLayout mainLayout = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            toolbar.setPadding(toolbar.getPaddingLeft(), insets.top, toolbar.getPaddingRight(), toolbar.getPaddingBottom());

            android.view.ViewGroup.LayoutParams params = toolbar.getLayoutParams();
            int baseHeight = getResources().getDimensionPixelSize(androidx.appcompat.R.dimen.abc_action_bar_default_height_material);
            params.height = baseHeight + insets.top;
            toolbar.setLayoutParams(params);

            v.setPadding(insets.left, 0, insets.right, insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        // Get Station from the intent that triggered this activity
        Parcelable wrapped = IntentCompat.getParcelableExtra(getIntent(), INTENT_STATION, Parcelable.class);
        if (wrapped != null) {
            station = Parcels.unwrap(wrapped);
        }

        presenter = new StationDetailsPresenter();
        presenter.init(this, station);
    }

    /**
     * @see IStationDetailsContract.View#init()
     */
    @Override
    public void init() {
        TextView tvStationName = findViewById(R.id.tvStName);
        TextView tvLatitude = findViewById(R.id.tvLatDisplay);
        TextView tvLongitude = findViewById(R.id.tvLongDisplay);
        TextView tvUsedSlots = findViewById(R.id.tvUsedSlotsDisplay);
        TextView tvFreeSlots = findViewById(R.id.tvFreeSlotsDisplay);
        TextView tvId = findViewById(R.id.tvIdDisplay);
        TextView tvTime = findViewById(R.id.tvTimeDisplay);

        if (station != null) {
            String name = station.getName();
            if (name != null) {
                tvStationName.setText(name);
            } else {
                tvStationName.setText(R.string.noData);
            }

            Double latitude = station.getLatitude();
            if (latitude == null || latitude > 90 || latitude < -90) {
                tvLatitude.setText(R.string.noData);
            } else {
                tvLatitude.setText(String.valueOf(latitude));
            }

            Double longitude = station.getLongitude();
            if (longitude == null || longitude > 180 || longitude < -180) {
                tvLongitude.setText(R.string.noData);
            } else {
                tvLongitude.setText(String.valueOf(longitude));
            }

            Integer usedSlots = station.getFreeBikes();
            if (usedSlots == null || usedSlots < 0) {
                tvUsedSlots.setText(R.string.noData);
            } else {
                tvUsedSlots.setText(String.valueOf(usedSlots));
            }

            Integer freeSlots = station.getEmptySlots();
            if (freeSlots == null || freeSlots < 0) {
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
                tvTime.setText(TimestampUtils.formatTimestamp(timestamp));
            } else {
                tvTime.setText(R.string.noData);
            }
        } else {
            Toast.makeText(this, R.string.loadErrorStation, Toast.LENGTH_LONG).show();
        }
    }

    /*
    @Override
    public IIncidencesRepository getIncidencesRepository() {
        return repository;
    }
    */

    /**
     * @see IStationDetailsContract.View#showIncidences(List)
     */
    @Override
    public void showIncidences(List<Incidence> incidences) {
        ListView list = findViewById(R.id.lvIncidences);
        IncidenceArrayAdapter adapter = new IncidenceArrayAdapter(this, incidences);
        list.setAdapter(adapter);
    }

    /**
     * @see IStationDetailsContract.View#showLoadError()
     */
    @Override
    public void showLoadError() {
        Toast.makeText(this, R.string.loadErrorIncidences, Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IStationDetailsContract.View#showRedError()
     */
    @Override
    public void showRedError() {
        Toast.makeText(this, R.string.redError, Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IStationDetailsContract.View#getContext()
     */
    @Override
    public Context getContext() {
        return this;
    }
}