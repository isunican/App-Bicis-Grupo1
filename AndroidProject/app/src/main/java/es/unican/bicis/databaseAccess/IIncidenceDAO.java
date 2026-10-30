package es.unican.bicis.databaseAccess;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import es.unican.bicis.model.Incident;
import java.util.List;



@Dao
public interface IIncidenceDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertarIncidencia(Incident incidencia);
    //void insertarIncidencia(Incident incidencia);

    @Query("SELECT * FROM incidencias WHERE idEstacion = :id")
    List<Incident> obtenerIncidenciasPorEstacion(String id);
}