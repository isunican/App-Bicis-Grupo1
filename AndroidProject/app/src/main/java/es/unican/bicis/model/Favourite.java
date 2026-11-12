package es.unican.bicis.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Getter;
import lombok.Setter;

/**
 * Clase que representa una red favorita en la base de datos, almacenando su id.
 */
@Entity(tableName = "favoritos")
@Parcel
@Getter
@Setter
public class Favourite {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "network_id")
    @SerializedName("network_id")
    public String networkId;

    /**
     * Constructor de la clase Favourite.
     * @param network La red correspondiente a la red favorita.
     */
    public Favourite(Network network) {
        if (network != null && network.getId() != null) {
            this.networkId = network.getId();
        } else {
            this.networkId = "";
        }
    }

    /**
     * Constructor de la clase Favourite.
     * @param networkId El id de la red correspondiente a la red favorita.
     */
    public Favourite(@NonNull String networkId) {
        this.networkId = networkId;
    }

    /**
     * Constructor vacío de la clase Favourite.
     */
    public Favourite() {
        this.networkId = "";
    }
}