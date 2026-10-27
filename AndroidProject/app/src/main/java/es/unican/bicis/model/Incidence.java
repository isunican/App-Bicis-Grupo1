package es.unican.bicis.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Getter;
import lombok.Setter;

@Parcel
@Getter
@Setter
public class Incidence {

    @SerializedName("id")                   protected String id;
    @SerializedName("typeId")                 protected IncidenceType typeId;
    @SerializedName("type")                 protected String type;
    @SerializedName("amount")             protected int amount;
}
