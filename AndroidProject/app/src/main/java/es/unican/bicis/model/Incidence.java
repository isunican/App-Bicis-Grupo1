package es.unican.bicis.model;
import com.google.gson.annotations.SerializedName;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.parceler.Parcel;

import lombok.Getter;
import lombok.Setter;

@Entity(tableName = "incidencias")
@Parcel
@Getter
@Setter
public class Incidence {
    @PrimaryKey (autoGenerate = true)
    @SerializedName("id")                 protected int id;
    @SerializedName("idEstacion")         protected String idEstacion;
    @SerializedName("typeId")             protected IncidenceType typeId;
    @SerializedName("type")               protected String type;
    @SerializedName("amount")             protected int amount;

    public Incidence(String estacionId, IncidenceType typeId, String type) {
        this.idEstacion = estacionId;
        this.typeId = typeId;
        this.type = type;
    }
}