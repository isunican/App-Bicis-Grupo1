package es.unican.bicis.activities.details.station;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.unican.bicis.databaseAccess.AppDatabase;
import es.unican.bicis.model.Incident;
import es.unican.bicis.model.IncidentType;
import es.unican.bicis.model.Station;

/**
 * The presenter of the StationDetails activity of the application. It controls {@link StationDetailsView}
 */
public class StationDetailsPresenter implements IStationDetailsContract.Presenter {

    /** App Database */
    private AppDatabase db;

    /** ExecutorService for background tasks */
    private ExecutorService executorService;

    /** The view that is controlled by this presenter */
    private IStationDetailsContract.View view;

    /** Statation of the view */
    private Station station;

    /** Handler for background tasks */
    private Handler mainHandler;

    /**
     * @see IStationDetailsContract.Presenter#init(IStationDetailsContract.View, Station, AppDatabase)
     */
    @Override
    public void init(IStationDetailsContract.View view, Station station, AppDatabase db) {
        this.view = view;
        this.station = station;
        this.db = db;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        // Le decimos a la vista que ya puede inicializar sus componentes visuales
        this.view.init();

        // Cargamos las incidencias existentes de la estación al iniciar
        if (station != null && station.getId() != null) {
            load();
        }
    }

    /**
     * @see IStationDetailsContract.Presenter#registrarIncidencia(IncidentType, String)
     */
    @Override
    public void registrarIncidencia(IncidentType type, String texto) {
        if (type == null) {
            return;
        }

        if (type == IncidentType.OTHER && (texto == null || texto.trim().isEmpty())) {
            // Llamamos a la vista para que muestre el error
            view.showEmptyIssueError();
            return;
        }

        String txt = type.getDescripcion();
        if (type == IncidentType.OTHER) {
            txt = texto;
        }

        Incident nuevaIncidencia = new Incident(station.getId(), type, txt);

        executorService.execute(() -> {
                try {
                    // Intentamos guardar en la base de datos
                    db.incidenciaDao().insertarIncidencia(nuevaIncidencia);
                    load();
                } catch (Exception e) {
                    // Si ocurre CUALQUIER error de base de datos, avisamos a la vista para que muestre el error
                    mainHandler.post(() -> {
                        if (view != null) {
                            view.showDatabaseError();
                        }
                    });
                }
            }
        );
    }

    /**
     * Consulta en segundo plano las incidencias de la estación en Room
     * y actualiza la vista en el hilo de interfaz.
     */
    protected void load() {
        if (db == null || station == null || executorService.isShutdown()) {
            return;
        }

        executorService.execute(() -> {
            try {
                List<Incident> incidencias = db.incidenciaDao().obtenerIncidenciasPorEstacion(station.getId());
                if (incidencias == null) {
                    incidencias = new ArrayList<>();
                }

                final List<Incident> listaFinal = incidencias;
                mainHandler.post(() -> {
                    if (view != null) {
                        view.showIncidences(listaFinal);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (view != null) {
                        view.showLoadError();
                    }
                });
            }
        });
    }

    /**
     * @see IStationDetailsContract.Presenter#onDestroy()
     */
    @Override
    public void onDestroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * @see IStationDetailsContract.Presenter#modificarIncidencia(Incident)
     */
    @Override
    public void modificarIncidencia(Incident incident) {
        if (incident == null) {
            return;
        }

        if (incident.getTypeId() == IncidentType.OTHER &&
                (incident.getType() == null || incident.getType().trim().isEmpty())) {
            // Llamamos a la vista para que muestre el error si el tipo es other y la descripción esta vacía
            view.showEmptyIssueError();
            return;
        }

        executorService.execute(() -> {
                try {
                    // Intentamos modificar la incidencia en la base de datos
                    db.incidenciaDao().actualizarIncidencia(incident);
                    load();
                } catch (Exception e) {
                    // Si ocurre CUALQUIER error de base de datos, avisamos a la vista para que muestre el error
                    mainHandler.post(() -> {
                        if (view != null) {
                            view.showDatabaseError();
                        }
                    });
                }
            }
        );
    }
}