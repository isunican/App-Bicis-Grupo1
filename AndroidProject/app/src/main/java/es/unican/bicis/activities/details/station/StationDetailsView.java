package es.unican.bicis.activities.details.station;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
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
import es.unican.bicis.databaseAccess.AppDatabase;
import es.unican.bicis.model.Incident;
import es.unican.bicis.model.Station;

public class StationDetailsView extends AppCompatActivity implements IStationDetailsContract.View {

    public static final String INTENT_STATION = "INTENT_STATION";

    private StationDetailsPresenter presenter;
    private Station station;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setDisplayHomeAsUpEnabled(true);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ConstraintLayout mainLayout = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            toolbar.setPadding(toolbar.getPaddingLeft(), insets.top, toolbar.getPaddingRight(), toolbar.getPaddingBottom());

            ViewGroup.LayoutParams params = toolbar.getLayoutParams();
            int baseHeight = getResources().getDimensionPixelSize(androidx.appcompat.R.dimen.abc_action_bar_default_height_material);
            params.height = baseHeight + insets.top;
            toolbar.setLayoutParams(params);

            v.setPadding(insets.left, 0, insets.right, insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        Parcelable wrapped = IntentCompat.getParcelableExtra(getIntent(), INTENT_STATION, Parcelable.class);
        if (wrapped != null) {
            station = Parcels.unwrap(wrapped);
        }

        AppDatabase db = AppDatabase.getDatabase(this);
        presenter = new StationDetailsPresenter(db);
        presenter.init(this, station);
    }

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
            tvStationName.setText(name != null ? name : getString(R.string.noData));

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
            tvId.setText(id != null ? id : getString(R.string.noData));

            String timestamp = station.getTimestamp();
            if (timestamp != null) {
                tvTime.setText(TimestampUtils.formatTimestamp(timestamp));
            } else {
                tvTime.setText(R.string.noData);
            }

            setupIncidenciasSpinner();
        } else {
            Toast.makeText(this, R.string.loadErrorStation, Toast.LENGTH_LONG).show();
        }
    }

    private void setupIncidenciasSpinner() {
        ImageButton btBotonIncidencia = findViewById(R.id.btBotonIncidencia);
        Spinner spIncidencias = findViewById(R.id.spIncidencias);

        if (spIncidencias != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.incidencias_options,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spIncidencias.setAdapter(adapter);

            if (btBotonIncidencia != null) {
                btBotonIncidencia.setOnClickListener(v -> spIncidencias.performClick());
            }

            spIncidencias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                private boolean isInitial = true;

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (isInitial) {
                        isInitial = false;
                        return;
                    }

                    String option = parent.getItemAtPosition(position).toString();

                    if (station != null && station.getId() != null) {
                        String estacionId = station.getId();

                        if (position == 0) {
                            presenter.registrarIncidenciaEstacionRota(estacionId);
                        } else if (position == 1) {
                            presenter.registrarIncidenciaBiciRota(estacionId);
                        } else {
                            presenter.registrarIncidenciaOtro(option, estacionId);
                        }
                    }

                    Toast.makeText(StationDetailsView.this,
                            getString(R.string.incidencia_registrada) + ": " + option,
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Sin acción
                }
            });
        }
    }

    @Override
    public void showIncidences(List<Incident> incidences) {
        ListView list = findViewById(R.id.lvIncidences);
        IncidentArrayAdapter adapter = new IncidentArrayAdapter(this, incidences);
        list.setAdapter(adapter);
    }

    @Override
    public void showLoadError() {
        Toast.makeText(this, R.string.loadErrorIncidences, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

}