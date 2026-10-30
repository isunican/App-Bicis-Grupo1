package es.unican.bicis.activities.details.station;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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

/**
 * Vista que muestra los detalles de una estación.
 */
public class StationDetailsView extends AppCompatActivity implements IStationDetailsContract.View {

    /** Clave para el intent que contiene la estación */
    public static final String INTENT_STATION = "INTENT_STATION";

    private IStationDetailsContract.Presenter presenter;
    private Station station = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details_view);

        // 1. Obtener la estación del Intent
        Parcelable wrapped = IntentCompat.getParcelableExtra(getIntent(), INTENT_STATION, Parcelable.class);
        if (wrapped != null) {
            station = Parcels.unwrap(wrapped);
        }

        // 2. Inicializar base de datos y presenter
        AppDatabase db = AppDatabase.getDatabase(this);
        presenter = new StationDetailsPresenter(db);

        // 3. Iniciar el presenter
        presenter.init(this, station);
    }

    @Override
    public void init() {
        // --- CONFIGURACIÓN DE UI (Toolbar y Notch) ---
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

        // --- CARGAR Y MOSTRAR LOS VALORES DE LA ESTACIÓN ---
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
            tvUsedSlots.setText(usedSlots == null || usedSlots < 0 ? getString(R.string.noData) : String.valueOf(usedSlots));

            Integer freeSlots = station.getEmptySlots();
            tvFreeSlots.setText(freeSlots == null || freeSlots < 0 ? getString(R.string.noData) : String.valueOf(freeSlots));

            String id = station.getId();
            tvId.setText(id != null ? id : getString(R.string.noData));

            String timestamp = station.getTimestamp();
            tvTime.setText(timestamp != null ? TimestampUtils.formatTimestamp(timestamp) : getString(R.string.noData));
        } else {
            Toast.makeText(this, R.string.loadErrorStation, Toast.LENGTH_LONG).show();
        }

        // --- CONFIGURAR SPINNER Y ALERT DIALOG ---
        ImageButton btBotonIncidencia = findViewById(R.id.btBotonIncidencia);
        Spinner spIncidencias = findViewById(R.id.spIncidencias);

        if (spIncidencias != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this, R.array.incidencias_options, android.R.layout.simple_spinner_item);
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

                    if (position == 0) return;

                    if (station != null && station.getId() != null) {
                        String estacionId = station.getId();

                        if (position == 1) {
                            presenter.registrarIncidenciaEstacionRota(estacionId);
                            spIncidencias.setSelection(0);

                        } else if (position == 2) {
                            presenter.registrarIncidenciaBiciRota(estacionId);
                            spIncidencias.setSelection(0);

                        } else {
                            // Opción 3: Otro
                            AlertDialog.Builder builder = new AlertDialog.Builder(StationDetailsView.this);
                            builder.setTitle(getString(R.string.other_issue_title));
                            builder.setMessage(getString(R.string.other_issue_message));

                            final EditText input = new EditText(StationDetailsView.this);
                            builder.setView(input);

                            builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String textoPersonalizado = input.getText().toString();
                                    presenter.registrarIncidenciaOtro(textoPersonalizado, estacionId);

                                    if (!textoPersonalizado.trim().isEmpty()) {
                                        Toast.makeText(StationDetailsView.this, getString(R.string.incidencia_registrada) + ": " + textoPersonalizado, Toast.LENGTH_SHORT).show();
                                    }
                                    spIncidencias.setSelection(0);
                                }
                            });

                            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    spIncidencias.setSelection(0);
                                }
                            });

                            builder.setOnCancelListener(dialog -> spIncidencias.setSelection(0));
                            builder.show();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    @Override
    public void showLoadError() {
        Toast.makeText(this, R.string.loadErrorIncidences, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showIncidences(List<Incident> incidents) {
        ListView list = findViewById(R.id.lvIncidences);
        IncidentArrayAdapter adapter = new IncidentArrayAdapter(this, incidents);
        list.setAdapter(adapter);
    }

    @Override
    public void showEmptyIssueError() {
        Toast.makeText(this, getString(R.string.empty_issue_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDatabaseError() {
        Toast.makeText(this, R.string.databaseError, Toast.LENGTH_SHORT).show();
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