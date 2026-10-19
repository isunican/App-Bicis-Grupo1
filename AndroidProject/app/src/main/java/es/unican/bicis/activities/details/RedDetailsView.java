package es.unican.bicis.activities.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.IntentCompat;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.unican.bicis.R;
import es.unican.bicis.common.LocationUtils;
import es.unican.bicis.model.Location;
import es.unican.bicis.model.Network;
import es.unican.bicis.model.Station;
import es.unican.bicis.repository.INetworksRepository;

/**
 * View that shows the details of one bike sharing network. Since this view does not have business logic,
 * it can be implemented as an activity directly, without the MVP pattern.
 */
@AndroidEntryPoint
public class RedDetailsView extends AppCompatActivity implements IRedDetailsContract.View {

    /** The presenter of this view */
    private IRedDetailsContract.Presenter presenter;

    /** Key for the intent that contains the network */
    public static final String INTENT_NETWORK = "INTENT_NETWORK";

    /** The repository to access the data. This is automatically injected by Hilt in this class */
    @Inject
    INetworksRepository repository;

    /** Network of the view */
    Network network = null;

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

        // Get Network from the intent that triggered this activity
        Parcelable wrapped = IntentCompat.getParcelableExtra(getIntent(), INTENT_NETWORK, Parcelable.class);
        if (wrapped != null) {
            network = Parcels.unwrap(wrapped);
        }

        LocationUtils.updateLocation();

        presenter = new RedDetailsPresenter();
        presenter.init(this, network);
    }

    /**
     * @see AppCompatActivity#onResume()
     */
    @Override
    protected void onResume() {
        LocationUtils.updateLocation();
        super.onResume();
    }

    /**
     * @see IRedDetailsContract.View#init()
     */
    @Override
    public void init() {
        // initialize on click listeners (when clicking on a station in the list)
        ListView list = findViewById(R.id.lvStations);
        list.setOnItemClickListener((parent, view, position, id) -> {
            Station station = (Station) parent.getItemAtPosition(position);
            presenter.onStationClicked(station);
        });

        // Link to view elements
        TextView tvName = findViewById(R.id.tvName);
        TextView tvCity = findViewById(R.id.tvCity);
        TextView tvEbikes = findViewById(R.id.tvEbikes);
        TextView tvCompanysNum = findViewById(R.id.tvCompanysNum);

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
        } else {
            Toast.makeText(this, R.string.loadError, Toast.LENGTH_LONG).show();
        }

        // Define buttons and set listeners
        ImageButton btnFilterMoreThanKm = findViewById(R.id.btnFilterMoreThanKm);
        ImageButton btnFilterLessThanKm = findViewById(R.id.btnFilterLessThanKm);

        btnFilterMoreThanKm.setOnClickListener(v -> onClickFilterMoreThanDistance());
        btnFilterLessThanKm.setOnClickListener(v -> onClickFilterLessThanDistance());
    }

    /**
     * Called when the user clicks on the button to filter by a distance more
     * than the one entered
     */
    public void onClickFilterMoreThanDistance() {
        String distance = ((TextView) findViewById(R.id.etKmFilter)).getText().toString();
        presenter.onFilterClicked(distance, DistanceFilter.MORE_THAN);
    }

    /**
     * Called when the user clicks on the button to filter by a distance less
     * than the one entered
     */
    public void onClickFilterLessThanDistance() {
        String distance = ((TextView) findViewById(R.id.etKmFilter)).getText().toString();
        presenter.onFilterClicked(distance, DistanceFilter.LESS_THAN);
    }

    /**
     * @see IRedDetailsContract.View#getNetworksRepository()
     * @return the repository to access the data
     */
    @Override
    public INetworksRepository getNetworksRepository() {
        return repository;
    }

    /**
     * @see IRedDetailsContract.View#showStations(List)
     */
    @Override
    public void showStations(List<Station> stations) {
        ListView list = findViewById(R.id.lvStations);
        StationsArrayAdapter adapter = new StationsArrayAdapter(this, stations);
        list.setAdapter(adapter);
    }

    /**
     * @see IRedDetailsContract.View#showNoStations()
     */
    @Override
    public void showNoStations() {
        Toast.makeText(this, R.string.noStations, Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IRedDetailsContract.View#showLoadCorrect(int)
     */
    @Override
    public void showLoadCorrect(int stations) {
        Toast.makeText(this, getString(R.string.loadCorrectStations, stations), Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IRedDetailsContract.View#showLoadError()
     */
    @Override
    public void showLoadError() {
        Toast.makeText(this, R.string.loadErrorStations, Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IRedDetailsContract.View#showRedError()
     */
    @Override
    public void showRedError() {
        Toast.makeText(this, R.string.redError, Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IRedDetailsContract.View#showLocationError()
     */
    @Override
    public void showLocationError() {
        Toast.makeText(this, R.string.locationError, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoStationsInRangeError() {
        Toast.makeText(this, R.string.noStationsInRange, Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IRedDetailsContract.View#showStationDetails(Station)
     */
    public void showStationDetails(Station station) {
        Intent intent = new Intent(this, StationDetailsView.class);
        intent.putExtra(StationDetailsView.INTENT_STATION, Parcels.wrap(station));
        startActivity(intent);
    }

    /**
     * @see IRedDetailsContract.View#getContext()
     */
    @Override
    public Context getContext() {
        return this;
    }
}