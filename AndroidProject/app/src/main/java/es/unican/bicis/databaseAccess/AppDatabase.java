package es.unican.bicis.databaseAccess;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import es.unican.bicis.model.Incident;

/**
 * Esta clase representa la base de datos de la aplicación con sus tablas (entidades).
 */
@Database(entities = {Incident.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // DAO para las incidencias
    public abstract IIncidentDAO incidenciaDao();

    // Variable volátil para el patrón Singleton
    private static volatile AppDatabase INSTANCE;

    /**
     * Método estático para obtener la instancia de la base de datos.
     * @param context Contexto de la aplicación.
     * @return Instancia de la base de datos.
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            // Bloque sincronizado para evitar que varios hilos creen la BD al mismo tiempo
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "bicis_database")
                            .fallbackToDestructiveMigration(true)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}