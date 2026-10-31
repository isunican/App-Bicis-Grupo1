package es.unican.bicis.databaseAccess;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import es.unican.bicis.model.Incident;

/**
 * DAO para acceder a la tabla de incidencias en la base de datos.
 */
@Dao
public interface IIncidentDAO {

    /**
     * Inserta una incidencia en la base de datos.
     * @param incidencia La incidencia a insertar.
     * @return El ID de la incidencia insertada.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertarIncidencia(Incident incidencia);
}