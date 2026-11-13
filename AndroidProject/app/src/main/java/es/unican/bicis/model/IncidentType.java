package es.unican.bicis.model;

import lombok.Getter;

/**
 * Enumeration that represents the type of incident.
 */
@Getter
public enum IncidentType {

    /**
     * The bike is defective.
     */
    BIKE("Bicicleta defectuosa"),

    /**
     * The station is damaged.
     */
    STATION("Estación dañada"),

    /**
     * Custom incident.
     */
    OTHER("");

    private final String descripcion;

    /**
     * Constructor for the incident type.
     * @param descripcion description of the incident.
     */
    IncidentType(String descripcion) {
        this.descripcion = descripcion;
    }

}