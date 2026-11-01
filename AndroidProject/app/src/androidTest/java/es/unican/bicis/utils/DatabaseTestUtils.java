package es.unican.bicis.utils;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import java.lang.reflect.Field;

import es.unican.bicis.databaseAccess.AppDatabase;

/**
 * Utility class for managing AppDatabase instances in instrumented UI tests.
 */
public class DatabaseTestUtils {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private DatabaseTestUtils() { }

    /**
     * Replaces the singleton instance of AppDatabase with an in-memory database.
     * @return the created in-memory AppDatabase instance
     */
    public static AppDatabase setInMemoryDatabase() {
        //Creates database in memory
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        try {
            // Access to the private static "INSTANCE" field
            // to bypass the default production database initialization.
            Field field = AppDatabase.class.getDeclaredField("INSTANCE");
            field.setAccessible(true);

            // Closes existing connections before replacing the refenece
            AppDatabase oldDb = (AppDatabase) field.get(null);
            if (oldDb != null && oldDb.isOpen()) {
                oldDb.close();
            }
            //Replaces the reference with the new database
            field.set(null, db);
        } catch (Exception e) {
            throw new RuntimeException("Error setting in-memory database", e);
        }

        return db;
    }

    /**
     * Clears the AppDatabase singleton instance.
     */
    public static void clearDatabase() {
        try {
            Field field = AppDatabase.class.getDeclaredField("INSTANCE");
            field.setAccessible(true);
            AppDatabase db = (AppDatabase) field.get(null);
            if (db != null && db.isOpen()) {
                db.close();
            }
            field.set(null, null);
        } catch (Exception e) {
            //Ignored
        }
    }
}
