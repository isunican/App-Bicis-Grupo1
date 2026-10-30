package es.unican.bicis.databaseAccess;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import es.unican.bicis.model.Incident;

@Dao
public interface IIncidentDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertarIncidencia(Incident incidencia);
}