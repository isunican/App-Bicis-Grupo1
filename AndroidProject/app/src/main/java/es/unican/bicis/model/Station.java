package es.unican.bicis.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import lombok.Getter;
import lombok.Setter;

/**
 * A station from a bike sharing network (a city-level bike rental system), as defined in the
 * <a href="https://api.citybik.es/v2/">CityBikes API</a>.
 *
 * The #SerializedName annotation is a GSON annotation that defines the name of the property
 * as defined in the json response.
 *
 * Getters are automatically generated at compile time by Lombok.
 */
@Parcel
@Getter
@Setter
public class Station {
    @SerializedName("id")   protected String id;
    @SerializedName("name")    protected String name;
    @SerializedName("latitude")    protected double latitude;
    @SerializedName("longitude")    protected double longitude;
    @SerializedName("free_bikes")    protected int freeBikes;
    @SerializedName("empty_slots")   protected int emptySlots;
    @SerializedName("timestamp")    protected String timestamp;
}
