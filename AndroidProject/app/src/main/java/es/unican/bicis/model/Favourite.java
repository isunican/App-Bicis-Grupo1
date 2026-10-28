package es.unican.bicis.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Getter;
import lombok.Setter;

@Entity(tableName = "favoritos")
@Parcel
@Getter
@Setter
public class Favourite {
    @PrimaryKey (autoGenerate = true)

    @ColumnInfo(name = "id")
    @SerializedName("id")                 public int id;

    @ColumnInfo(name = "network_id")
    @SerializedName("network_id")          public String networkId;



    public Favourite(Network network) {
        if (network != null) {
            this.networkId = network.getId();
        }
    }

    public Favourite(String networkId) {
        this.networkId = networkId;
    }

    public Favourite() {}
}