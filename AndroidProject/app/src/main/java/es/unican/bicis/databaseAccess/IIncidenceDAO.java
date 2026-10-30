
import es.unican.bicis.model.Incident;

@Dao
public interface IIncidenceDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertarIncidencia(Incident incidencia);
}