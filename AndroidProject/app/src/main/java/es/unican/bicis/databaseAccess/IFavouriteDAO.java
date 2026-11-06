package es.unican.bicis.databaseAccess;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import es.unican.bicis.model.Favourite;

/**
 * DAO para acceder a la tabla de redes favoritas en la base de datos.
 */
@Dao
public interface IFavouriteDAO {

    /**
     * Inserta una red favorita en la base de datos.
     * @param favorito La red favorita a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarFavorito(Favourite favorito);

    /**
     * Consulta las redes favoritas de la base de datos.
     * @return Una lista de redes favoritas.
     */
    @Query("SELECT * FROM favoritos")
    List<Favourite> obtenerFavoritos();

    /**
     * Obtiene los IDs de las redes favoritas de la base de datos.
     * @return Una lista de IDs de redes favoritas.
     */
    @Query("SELECT network_id FROM favoritos")
    List<String> obtenerNetworkIdsFavoritos();
}