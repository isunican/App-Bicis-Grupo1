package es.unican.bicis.activities.details.station;

import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.unican.bicis.databaseAccess.AppDatabase;
import es.unican.bicis.model.Incident;
import es.unican.bicis.model.IncidentType;
import es.unican.bicis.model.Station;

/**
 * Presentador de la actividad StationDetails. Gestiona la lógica de la estación
 * y el acceso a Room para las incidencias.
 */
public class StationDetailsPresenter implements IStationDetailsContract.Presenter {

    private IStationDetailsContract.View view;
    private Station station;
    private final AppDatabase db;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public StationDetailsPresenter(AppDatabase db) {
        this.db = db;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void init(IStationDetailsContract.View view, Station station) {
        this.view = view;
        this.station = station;
        this.view.init();

        // Cargamos las incidencias existentes de la estación al iniciar
        if (station != null && station.getId() != null) {
            cargarIncidencias(station.getId());
        }
    }

    @Override
    public void registrarIncidenciaEstacionRota(String estacionId) {
        Incident nuevaIncidencia = new Incident(estacionId, IncidentType.STATION, "EstacionRota");
        guardarIncidencia(nuevaIncidencia, estacionId);
    }

    @Override
    public void registrarIncidenciaBiciRota(String estacionId) {
        Incident nuevaIncidencia = new Incident(estacionId, IncidentType.BIKE, "BiciRota");
        guardarIncidencia(nuevaIncidencia, estacionId);
    }

    @Override
    public void registrarIncidenciaOtro(String textoPersonalizado, String estacionId) {
        if (textoPersonalizado == null || textoPersonalizado.trim().isEmpty()) {
            view.showEmptyIssueError();
            return;
        }
        Incident nuevaIncidencia = new Incident(estacionId, IncidentType.OTHER, textoPersonalizado.trim());
        guardarIncidencia(nuevaIncidencia, estacionId);
    }

    /**
     * Inserta la incidencia en segundo plano, comunica los errores en el hilo principal
     * y refresca la lista si la inserción es correcta.
     */
    private void guardarIncidencia(Incident incidencia, String estacionId) {
        executorService.execute(() -> {
            try {
                db.incidenciaDao().insertarIncidencia(incidencia);
                // Si se guarda correctamente, refrescamos la lista
                cargarIncidencias(estacionId);
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (view != null) {
                        view.showDatabaseError();
                    }
                });
            }
        });
    }

    /**
     * Consulta las incidencias en segundo plano y las envía a la vista en el hilo principal.
     */
    private void cargarIncidencias(String estacionId) {
        executorService.execute(() -> {
            try {
                List<Incident> incidencias = db.incidenciaDao().obtenerIncidenciasPorEstacion(estacionId);
                mainHandler.post(() -> {
                    if (view != null) {
                        view.showIncidences(incidencias);
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

    @Override
    public void onDestroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}