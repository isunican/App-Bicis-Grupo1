package es.unican.bicis.activities.details.station;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.unican.bicis.R;
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

    /**
     * @see IStationDetailsContract.Presenter#init(IStationDetailsContract.View, Station, AppDatabase)
     */
    @Override
    public void init(IStationDetailsContract.View view, Station station, AppDatabase db) {
        this.view = view;
        this.station = station;
        this.db = db;
        this.executorService = Executors.newSingleThreadExecutor();
        // Le decimos a la vista que ya puede inicializar sus componentes visuales
        this.view.init();
    }

    /**
     * @see IStationDetailsContract.Presenter#registrarIncidenciaEstacionRota()
     */
    @Override
    public void registrarIncidenciaEstacionRota() {
        Incident nuevaIncidencia = new Incident(station.getId(), IncidentType.STATION, view.getContext().getString(R.string.estcRotaIncidentTxt));
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
     * @see IStationDetailsContract.Presenter#registrarIncidenciaBiciRota()
     */
    @Override
    public void registrarIncidenciaBiciRota() {
        Incident nuevaIncidencia = new Incident(station.getId(), IncidentType.BIKE, view.getContext().getString(R.string.biciRotaIncidentTxt));
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
     * @see IStationDetailsContract.Presenter#registrarIncidenciaOtro(String)
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
     * @see IStationDetailsContract.Presenter#onDestroy()
     */
    @Override
    public void onDestroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}