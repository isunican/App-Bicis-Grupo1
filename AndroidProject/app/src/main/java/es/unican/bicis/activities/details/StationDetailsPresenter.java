package es.unican.bicis.activities.details;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.unican.bicis.databaseAccess.AppDatabase;
import es.unican.bicis.model.Incidence;
import es.unican.bicis.model.IncidenceType;
import es.unican.bicis.model.Station;

/**
 * Presentador de la actividad StationDetails. Controla {@link StationDetailsView}
 * gestionando las incidencias directamente contra la base de datos local (Room).
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

        if (this.station == null || this.station.getId() == null) {
            this.view.showLoadError();
            return;
        }

        load();
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
                List<Incidence> incidencias = db.incidenciaDao().obtenerIncidenciasPorEstacion(station.getId());
                if (incidencias == null) {
                    incidencias = new ArrayList<>();
                }

                final List<Incidence> listaFinal = incidencias;
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

    public void registrarIncidenciaEstacionRota(String estacionId) {
        Incidence nuevaIncidencia = new Incidence(estacionId, IncidenceType.STATION, "EstacionRota");
        insertarEnBaseDeDatos(nuevaIncidencia);
    }

    public void registrarIncidenciaBiciRota(String estacionId) {
        Incidence nuevaIncidencia = new Incidence(estacionId, IncidenceType.BIKE, "BiciRota");
        insertarEnBaseDeDatos(nuevaIncidencia);
    }

    public void registrarIncidenciaOtro(String textoPersonalizado, String estacionId) {
        Incidence nuevaIncidencia = new Incidence(estacionId, IncidenceType.OTHER, textoPersonalizado);
        insertarEnBaseDeDatos(nuevaIncidencia);
    }

    /**
     * Inserta la incidencia en segundo plano y refresca la lista.
     */
    private void insertarEnBaseDeDatos(Incidence incidencia) {
        if (db != null && executorService != null && !executorService.isShutdown()) {
            executorService.execute(() -> {
                db.incidenciaDao().insertarIncidencia(incidencia);
                load(); // Recarga las incidencias en la vista tras guardar
            });
        }
    }

    public void onDestroy() {
        this.view = null;
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}