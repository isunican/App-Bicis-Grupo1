package es.unican.bicis.databaseAccess;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import es.unican.bicis.model.Incidence;

@Dao
public interface IIncidenceDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarIncidencia(Incidence incidencia);

    @Query("SELECT * FROM incidencias WHERE idEstacion = :id")
    List<Incidence> obtenerIncidenciasPorEstacion(String id);
}