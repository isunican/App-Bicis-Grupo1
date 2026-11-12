package es.unican.bicis.activities.details.station;

import android.content.Context;
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
import es.unican.bicis.model.IncidentType;
import es.unican.bicis.model.Incident;
import es.unican.bicis.model.Station;

/**
 * View that shows the details of one station.
 */
public class StationDetailsView extends AppCompatActivity implements IStationDetailsContract.View {

    /** Key for the intent that contains the station */
    public static final String INTENT_STATION = "INTENT_STATION";

    /** The presenter of this view */
    private IStationDetailsContract.Presenter presenter;

    /** Station of the view */
    private Station station = null;

    /**
     * @see AppCompatActivity#onCreate(Bundle)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details_view);

        // Obtener la estación del Intent
        Parcelable wrapped = IntentCompat.getParcelableExtra(getIntent(), INTENT_STATION, Parcelable.class);
        if (wrapped != null) {
            station = Parcels.unwrap(wrapped);
        }

        // Inicializar base de datos y presenter
        AppDatabase db = AppDatabase.getDatabase(this);
        presenter = new StationDetailsPresenter();
        presenter.init(this, station, db);
    }

    /**
     * @see IStationDetailsContract.View#init()
     */
    @Override
    public void init() {
        configurarUI();
        //Cargar y mostrar los valores de los datos de la estacion
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

        configurarSpinnerYAlertDialog();

    }

    /**
     * Private method for configuring the UI
     */
    private void configurarUI() {
        // CONFIGURACIÓN DE UI (Toolbar y Notch)
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

    }

    /**
     * Private method for configuring the Spinner and Alert Dialog
     */
    private void configurarSpinnerYAlertDialog() {
        // CONFIGURAR SPINNER Y ALERT DIALOG
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

                        if (position == 1) {
                            presenter.registrarIncidencia(IncidentType.STATION, null);

                        } else if (position == 2) {
                            presenter.registrarIncidencia(IncidentType.BIKE, null);

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(StationDetailsView.this);
                            builder.setTitle(getString(R.string.other_issue_title));
                            builder.setMessage(getString(R.string.other_issue_message));

                            final EditText input = new EditText(StationDetailsView.this);
                            builder.setView(input);

                            builder.setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                String textoPersonalizado = input.getText().toString();
                                presenter.registrarIncidencia(IncidentType.OTHER, textoPersonalizado);
                            });

                            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());

                            builder.setOnCancelListener(dialog -> spIncidencias.setSelection(0));
                            builder.show();
                        }
                        spIncidencias.setSelection(0);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Sino se selecciona nada no se hace nada
                }
            });
        }
    }
    /**
     * @see IStationDetailsContract.View#showLoadError()
     */
    @Override
    public void showLoadError() {
        Toast.makeText(this, R.string.loadErrorIncidences, Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IStationDetailsContract.View#showIncidences(List)
     */
    @Override
    public void showIncidences(List<Incident> incidents) {
        ListView list = findViewById(R.id.lvIncidences);
        IncidentArrayAdapter adapter = new IncidentArrayAdapter(this, incidents, this::showEditIncidentDialog);
        list.setAdapter(adapter);
    }

    /**
     * @see IStationDetailsContract.View#showEmptyIssueError()
     */
    @Override
    public void showEmptyIssueError() {
        Toast.makeText(this, getString(R.string.empty_issue_error), Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IStationDetailsContract.View#showDatabaseError()
     */
    @Override
    public void showDatabaseError() {
        Toast.makeText(this, R.string.databaseError, Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IStationDetailsContract.View#getContext()
     */
    @Override
    public Context getContext() {
        return this;
    }

    /**
     * @see IStationDetailsContract.View#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    /**
     * @see IStationDetailsContract.View#showEditIncidentDialog(Incident)
     */
    @Override
    public void showEditIncidentDialog(Incident incident) {
        if (incident == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflar el layout personalizado
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_incident, null);
        builder.setView(dialogView);

        Spinner spEditType = dialogView.findViewById(R.id.spEditType);
        EditText etEditDescription = dialogView.findViewById(R.id.etEditDescription);

        // Cargar los items del spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.edit_incicents_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEditType.setAdapter(adapter);

        // Seleccionar el tipo actual de la incidencia
        IncidentType typeId = incident.getTypeId();
        switch (typeId) {
            case STATION:
                spEditType.setSelection(0);
                break;
            case BIKE:
                spEditType.setSelection(1);
                break;
            case OTHER:
                spEditType.setSelection(2);
                break;
        }

        // Rellenar el texto de la descripción
        etEditDescription.setText(incident.getType());

        // Mostrar/Ocultar o habilitar la descripción si no es tipo "Otro"
        spEditType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isOtro = (position == 2);
                etEditDescription.setEnabled(isOtro);
                if (!isOtro) {
                    etEditDescription.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Si no se selecciona nada no se hace nada
            }
        });

        // Botón Confirmar
        builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
            int selectedPosition = spEditType.getSelectedItemPosition();
            IncidentType nuevoTipo = null;
            String nuevaDescripcion = null;

            switch (selectedPosition) {
                case 0:
                    nuevoTipo = IncidentType.STATION;
                    nuevaDescripcion = IncidentType.STATION.getDescripcion();
                    break;
                case 1:
                    nuevoTipo = IncidentType.BIKE;
                    nuevaDescripcion = IncidentType.BIKE.getDescripcion();
                    break;
                case 2:
                    nuevoTipo = IncidentType.OTHER;
                    nuevaDescripcion = etEditDescription.getText().toString().trim();
                    if (nuevaDescripcion.isEmpty()) {
                        // Mostrar error si el tipo es other y la descripción esta vacía
                        showEmptyIssueError();
                        return;
                    }
                    break;
            }

            // Actualizar la instancia de la incidencia y notificar al Presenter
            incident.setTypeId(nuevoTipo);
            incident.setType(nuevaDescripcion);

            presenter.modificarIncidencia(incident);
        });

        // Botón Cancelar
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}