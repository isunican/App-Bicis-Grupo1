package es.unican.bicis.databaseAccess;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import es.unican.bicis.model.Incident;
import es.unican.bicis.model.Favourite;

// 1. Define las entidades (tablas) y la versión de la BD.
// Si tienes más entidades (ej. Station), ponlas separadas por comas: {Incidencia.class, Station.class}
@Database(entities = {Favourite.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract IFavouriteDAO favouriteDAO();

    // 3. Variable volátil para el patrón Singleton
    // HABIA UN VOLATIL AQUI
    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "bicis_database") // Nombre del archivo físico de la BD
                            // .fallbackToDestructiveMigration() // Descomenta esto en fase de desarrollo si cambias la estructura de las tablas
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}