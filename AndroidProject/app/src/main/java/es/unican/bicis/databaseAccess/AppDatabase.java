package es.unican.bicis.databaseAccess;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import es.unican.bicis.model.Incident;
import es.unican.bicis.model.Favourite;

// 1. Define las entidades (tablas) y la versión de la BD.
// Si tienes más entidades (ej. Station), ponlas separadas por comas: {Incidencia.class, Station.class}
@Database(entities = {Favourite.class, Incident.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // 2. Declara un método abstracto por cada DAO que tengas
    public abstract IFavouriteDAO favouriteDAO();
    public abstract IIncidentDAO incidenciaDao();

    // 3. Variable volátil para el patrón Singleton
    private static volatile AppDatabase INSTANCE;

    // 4. Método estático para obtener la instancia de la base de datos
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            // Bloque sincronizado para evitar que varios hilos creen la BD al mismo tiempo
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "bicis_database") // Nombre del archivo físico de la BD
                            .fallbackToDestructiveMigration(true) // Destructive migration strategy on schema changes
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}