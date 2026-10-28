package es.unican.bicis.activities.details;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.unican.bicis.databaseAccess.AppDatabase;
import es.unican.bicis.model.Incidence;
import es.unican.bicis.model.IncidenceType;

// Asegúrate de importar tu AppDatabase, Incidencia, etc.

public class StationDetailsPresenter {

    // Necesitamos la base de datos y el executor para el hilo secundario
    private AppDatabase db;
    private ExecutorService executorService;

    // Constructor: le pasamos la base de datos cuando lo instanciemos en la Activity
    public StationDetailsPresenter(AppDatabase db) {
        this.db = db;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Este método será llamado desde la Vista cuando el usuario elija una opción en el Spinner.
     */
    public void registrarIncidenciaEstacionRota(String estacionId) {
        // TODO comprobar el texto del tipo pulsado y convertirlo a IncidenceType
        // Asegúrate de que en tu clase Incidencia.java, 'estacionId' sea de tipo String.
        Incidence nuevaIncidencia = new Incidence(estacionId, IncidenceType.STATION, "EstacionRota");

        // 2. Guardar en Room usando el hilo secundario
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                db.IIncidenceDAO().insertarIncidencia(nuevaIncidencia);
            }
        });
    }

    public void registrarIncidenciaBiciRota(String estacionId) {
        // TODO comprobar el texto del tipo pulsado y convertirlo a IncidenceType
        // Asegúrate de que en tu clase Incidencia.java, 'estacionId' sea de tipo String.
        Incidence nuevaIncidencia = new Incidence(estacionId, IncidenceType.BIKE, "BiciRota");

        // 2. Guardar en Room usando el hilo secundario
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                db.IIncidenceDAO().insertarIncidencia(nuevaIncidencia);
            }
        });
    }

    public void registrarIncidenciaOtro(String textoPersonalizado, String estacionId) {
        // TODO comprobar el texto del tipo pulsado y convertirlo a IncidenceType
        // Asegúrate de que en tu clase Incidencia.java, 'estacionId' sea de tipo String.
        Incidence nuevaIncidencia = new Incidence(estacionId, IncidenceType.OTHER, textoPersonalizado);

        // 2. Guardar en Room usando el hilo secundario
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                db.IIncidenceDAO().insertarIncidencia(nuevaIncidencia);
            }
        });
    }

    // Es buena práctica apagar el hilo cuando la vista se destruya
    public void onDestroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}