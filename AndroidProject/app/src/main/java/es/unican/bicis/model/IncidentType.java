package es.unican.bicis.model;

public enum IncidentType {
    BIKE("Bicicleta defectuosa"),
    STATION("Estación dañada"),
    OTHER("");

    private final String descripcion;

    IncidentType(String descripcion) {
        this.descripcion = descripcion;
    }
        }