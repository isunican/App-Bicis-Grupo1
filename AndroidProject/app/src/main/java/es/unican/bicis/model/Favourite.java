package es.unican.bicis.model;

import androidx.annotation.NonNull;
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

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "network_id")
    @SerializedName("network_id")
    public String networkId;

    public Favourite(Network network) {
        if (network != null && network.getId() != null) {
            this.networkId = network.getId();
        } else {
            this.networkId = "";
        }
    }

    public Favourite(@NonNull String networkId) {
        this.networkId = networkId;
    }

    public Favourite() {
        this.networkId = "";
    }
}