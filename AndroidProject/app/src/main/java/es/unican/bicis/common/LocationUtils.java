package es.unican.bicis.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import lombok.Getter;

/**
 * Maneja la obtención de la ubicación del dispositivo.
 */
public class LocationUtils {

    @Getter
    private static LocationUtils instance;

    private final Context context;
    private Location currentLocation;

    /**
     * Obtiene la última ubicación conocida del dispositivo de forma estática.
     * @return la ubicación actual o null si no se ha obtenido aún
     */
    public static Location getCurrentLocation() {
        if (instance == null) {
            return null;
        } else {
            return instance.currentLocation;
        }
    }

    /**
     * Constructor privado para evitar instanciar la clase (singleton).
     * @param context el contexto de la aplicación
     */
    private LocationUtils(Context context) {
        this.context = context;
    }

    /**
     * Inicializa la clase singleton.
     */
    public static void init(Context context) {
        if (instance == null) {
            instance = new LocationUtils(context.getApplicationContext());
        }
    }

    /**
     * Obtiene la ubicación actual del dispositivo y la guarda.
     */
    @SuppressLint("MissingPermission") // Comprobado previamente en la app
    public static void updateLocation(Runnable onLocationReady) {
        if (instance != null) {
            LocationManager locationManager = (LocationManager) instance.context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                // Leemos del GPS_PROVIDER que es el que modifica MockLocationManager
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                instance.currentLocation = location;
                if (location != null && onLocationReady != null) {
                    onLocationReady.run();
                }
            }
        }
    }

    /**
     * Calcula la distancia entre la localización actual y una de destino dada.
     * @param latitud la latitud de la coordenada destino
     * @param longitud la longitud de la coordenada destino
     * @return la distancia en Km entre la localización actual y la destino
     */
    public static float calcularDistanciaA(double latitud, double longitud) {
        if (instance == null || instance.currentLocation == null) {
            return -1; // O manejar el error si aún no hay ubicación actual
        }

        Location destino = new Location("destino");
        destino.setLatitude(latitud);
        destino.setLongitude(longitud);

        return instance.currentLocation.distanceTo(destino) / 1000;
    }
}