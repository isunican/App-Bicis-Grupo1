package es.unican.bicis.model;
import com.google.gson.annotations.SerializedName;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.parceler.Parcel;

import lombok.Getter;
import lombok.Setter;

/**
 * An incident reported on a station of a network.
 */
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

    /**
     * Constructor for an incident.
     * @param estacionId id of the station.
     * @param typeId type of the incident.
     * @param type description of the incident.
     */
    public Incident(String estacionId, IncidentType typeId, String type) {
        this.idEstacion = estacionId;
        this.typeId = typeId;
        this.type = type;
    }

    /**
     * Empty constructor for the incident used by Room.
     */
    public Incident(){}
}