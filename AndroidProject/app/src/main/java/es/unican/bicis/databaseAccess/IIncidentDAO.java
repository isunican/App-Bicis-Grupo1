package es.unican.bicis.databaseAccess;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import es.unican.bicis.model.Incident;
import java.util.List;

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

    @Query("SELECT " +
            "MIN(id) AS id, " +
            "idEstacion, " +
            "typeId, " +
            "MIN(type) AS type, " +
            "CASE WHEN COUNT(*) > 1 THEN COUNT(*) ELSE 1 END AS amount " +
            "FROM incidencias " +
            "WHERE idEstacion = :id " +
            "GROUP BY CASE WHEN typeId = 'OTHER' THEN id ELSE typeId END")
    List<Incident> obtenerIncidenciasPorEstacion(String id);
}