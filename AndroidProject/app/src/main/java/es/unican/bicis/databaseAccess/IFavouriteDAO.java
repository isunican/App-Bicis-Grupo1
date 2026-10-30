package es.unican.bicis.databaseAccess;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import es.unican.bicis.model.Favourite;

@Dao
public interface IFavouriteDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarFavorito(Favourite favorito);

    @Query("SELECT * FROM favoritos")
    List<Favourite> obtenerFavoritos();

    @Query("SELECT network_id FROM favoritos")
    List<String> obtenerNetworkIdsFavoritos();
}