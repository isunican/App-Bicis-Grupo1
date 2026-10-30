package es.unican.bicis.activities.details;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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

import es.unican.bicis.R;
import es.unican.bicis.common.TimestampUtils;
import es.unican.bicis.databaseAccess.AppDatabase;
import es.unican.bicis.model.Station;

/**
 * View that shows the details of a bike station from a selected network.
 * Since this view does not have business logic, it can be implemented as an activity directly
 */
public class StationDetailsView extends AppCompatActivity {

    /** Key for the intent that contains the station */
    public static final String INTENT_STATION = "INTENT_STATION";

    // 1. Declarar el presentador
    private StationDetailsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details_view);

        // 2. Inicializar la base de datos y el presentador
        AppDatabase db = AppDatabase.getDatabase(this);
        presenter = new StationDetailsPresenter(db);

        // The default theme does not include a toolbar.
        // In this app the toolbar is explicitly declared in the layout
        // Set this toolbar as the activity ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        assert bar != null;  // to avoid warning in the line below
        bar.setDisplayHomeAsUpEnabled(true);  // show back button in action bar

        // Evitar solape con el notch
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

        ImageButton btBotonIncidencia = findViewById(R.id.btBotonIncidencia);
        Spinner spIncidencias = findViewById(R.id.spIncidencias);

        // Hacemos que la variable station sea "final" para poder usarla dentro del listener del Spinner
        final Station currentStation = station;

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

                    if (position == 0) {
                        return;
                    }

                    String option = parent.getItemAtPosition(position).toString();

                    if (currentStation != null && currentStation.getId() != null) {
                        String estacionId = currentStation.getId();

                        // Llamamos al método del presenter según la opción elegida
                        if (position == 1) {
                            // Suponiendo que la posición 1 es "Estación Rota"
                            presenter.registrarIncidenciaEstacionRota(estacionId);
                        } else if (position == 2) {
                            // Suponiendo que la posición 2 es "Bici Rota"
                            presenter.registrarIncidenciaBiciRota(estacionId);
                        } else {
                            // Opción 3 o superior: "Otro"
                            AlertDialog.Builder builder = new AlertDialog.Builder(StationDetailsView.this);
                            builder.setTitle("Otra incidencia");
                            builder.setMessage("Describe el problema de la estación:");

                            // Crear el campo de texto
                            final EditText input = new EditText(StationDetailsView.this);
                            builder.setView(input);

                            // Botón Confirmar
                            builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String textoPersonalizado = input.getText().toString();

                                    // Mandamos el texto al presenter
                                    presenter.registrarIncidenciaOtro(textoPersonalizado, estacionId);

                                    // Mostramos el Toast con lo que ha escrito
                                    Toast.makeText(StationDetailsView.this,
                                            getString(R.string.incidencia_registrada) + ": " + textoPersonalizado,
                                            Toast.LENGTH_SHORT).show();

                                    spIncidencias.setSelection(0); // Resetear
                                }
                            });

                            // Botón Cancelar
                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    spIncidencias.setSelection(0); // Resetear sin guardar
                                }
                            });

                            // Si el usuario toca fuera de la ventana para cerrarla, también reseteamos el Spinner
                            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    spIncidencias.setSelection(0);
                                }
                            });

                            builder.show();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Do nothing
                }
            });

        }
    }

    // 4. Limpiar el presentador al salir de la activity para evitar fugas de memoria
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}