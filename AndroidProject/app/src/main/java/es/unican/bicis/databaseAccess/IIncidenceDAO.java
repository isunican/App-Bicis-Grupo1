package es.unican.bicis.databaseAccess;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import es.unican.bicis.model.Incidence;

@Dao
public interface IIncidenceDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertarIncidencia(Incidence incidencia);
}