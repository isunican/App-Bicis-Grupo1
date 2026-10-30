package es.unican.bicis.activities.details.station;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.unican.bicis.databaseAccess.AppDatabase;
import es.unican.bicis.model.Incident;
import es.unican.bicis.model.IncidentType;
import es.unican.bicis.model.Station;

public class StationDetailsPresenter implements IStationsDetailsContract.Presenter {

    private AppDatabase db;
    private ExecutorService executorService;

    // Variables para guardar la vista y el modelo (estación)
    private IStationsDetailsContract.View view;
    private Station station;

    public StationDetailsPresenter(AppDatabase db) {
        this.db = db;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * @see IStationsDetailsContract.Presenter#init(IStationsDetailsContract.View, Station)
     */
    @Override
    public void init(IStationsDetailsContract.View view, Station station) {
        this.view = view;
        this.station = station;
        // Le decimos a la vista que ya puede inicializar sus componentes visuales
        this.view.init();
    }

    /**
     * @see IStationsDetailsContract.Presenter#registrarIncidenciaEstacionRota(String)
     */
    @Override
    public void registrarIncidenciaEstacionRota(String estacionId) {
        Incident nuevaIncidencia = new Incident(estacionId, IncidentType.STATION, "EstacionRota");
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Intentamos guardar en la base de datos
                    db.incidenciaDao().insertarIncidencia(nuevaIncidencia);
                } catch (Exception e) {
                    // Si ocurre CUALQUIER error de base de datos, avisamos a la vista
                    if (view != null) {
                        view.showDatabaseError();
                    }
                }
            }
        });
    }

    /**
     * @see IStationsDetailsContract.Presenter#registrarIncidenciaBiciRota(String)
     */
    @Override
    public void registrarIncidenciaBiciRota(String estacionId) {
        Incident nuevaIncidencia = new Incident(estacionId, IncidentType.BIKE, "BiciRota");
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Intentamos guardar en la base de datos
                    db.incidenciaDao().insertarIncidencia(nuevaIncidencia);
                } catch (Exception e) {
                    // Si ocurre CUALQUIER error de base de datos, avisamos a la vista
                    if (view != null) {
                        view.showDatabaseError();
                    }
                }
            }
        });
    }

    /**
     * @see IStationsDetailsContract.Presenter#registrarIncidenciaOtro(String, String)
     */
    @Override
    public void registrarIncidenciaOtro(String textoPersonalizado, String estacionId) {
        if (textoPersonalizado.trim().isEmpty()) {
            // Llamamos a la vista para que muestre el error
            view.showEmptyIssueError();
            return;
        }
        Incident nuevaIncidencia = new Incident(estacionId, IncidentType.OTHER, textoPersonalizado);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Intentamos guardar en la base de datos
                    db.incidenciaDao().insertarIncidencia(nuevaIncidencia);
                } catch (Exception e) {
                    // Si ocurre CUALQUIER error de base de datos, avisamos a la vista
                    if (view != null) {
                        view.showDatabaseError();
                    }
                }
            }
        });
    }

    /**
     * @see IStationsDetailsContract.Presenter#onDestroy()
     */
    @Override
    public void onDestroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}