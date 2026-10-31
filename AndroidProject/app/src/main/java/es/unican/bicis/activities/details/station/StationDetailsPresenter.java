package es.unican.bicis.activities.details.station;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.unican.bicis.databaseAccess.AppDatabase;
import es.unican.bicis.model.Incident;
import es.unican.bicis.model.IncidentType;
import es.unican.bicis.model.Station;

/**
 * The presenter of the StationDetails activity of the application. It controls {@link StationDetailsView}
 */
public class StationDetailsPresenter implements IStationsDetailsContract.Presenter {

    /** App Database */
    private AppDatabase db;

    /** ExecutorService for background tasks */
    private ExecutorService executorService;

    /** The view that is controlled by this presenter */
    private IStationsDetailsContract.View view;

    /** Statation of the view */
    private Station station;

    /**
     * @see IStationsDetailsContract.Presenter#init(IStationsDetailsContract.View, Station, AppDatabase)
     */
    @Override
    public void init(IStationsDetailsContract.View view, Station station, AppDatabase db) {
        this.view = view;
        this.station = station;
        this.db = db;
        this.executorService = Executors.newSingleThreadExecutor();
        // Le decimos a la vista que ya puede inicializar sus componentes visuales
        this.view.init();
    }

    /**
     * @see IStationsDetailsContract.Presenter#registrarIncidenciaEstacionRota()
     */
    @Override
    public void registrarIncidenciaEstacionRota() {
        Incident nuevaIncidencia = new Incident(station.getId(), IncidentType.STATION, "EstacionRota");
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
     * @see IStationsDetailsContract.Presenter#registrarIncidenciaBiciRota()
     */
    @Override
    public void registrarIncidenciaBiciRota() {
        Incident nuevaIncidencia = new Incident(station.getId(), IncidentType.BIKE, "BiciRota");
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
     * @see IStationsDetailsContract.Presenter#registrarIncidenciaOtro(String)
     */
    @Override
    public void registrarIncidenciaOtro(String textoPersonalizado) {
        if (textoPersonalizado == null || textoPersonalizado.trim().isEmpty()) {
            // Llamamos a la vista para que muestre el error
            view.showEmptyIssueError();
            return;
        }
        Incident nuevaIncidencia = new Incident(station.getId(), IncidentType.OTHER, textoPersonalizado);

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