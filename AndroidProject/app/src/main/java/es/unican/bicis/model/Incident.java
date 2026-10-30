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
public class Incident {
    @PrimaryKey (autoGenerate = true)
    @SerializedName("id")                 public int id;
    @SerializedName("idEstacion")         public String idEstacion;
    @SerializedName("typeId")             public IncidentType typeId;
    @SerializedName("type")               public String type;
    @SerializedName("amount")             public int amount;

    public Incident(String estacionId, IncidentType typeId, String type) {
        this.idEstacion = estacionId;
        this.typeId = typeId;
        this.type = type;
    }

    public Incident(){}
}