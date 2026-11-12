package es.unican.bicis.activities.details.red;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import es.unican.bicis.common.LocationUtils;
import es.unican.bicis.common.Utils;
import es.unican.bicis.model.Network;
import es.unican.bicis.model.Station;
import es.unican.bicis.repository.INetworksRepository;
import es.unican.bicis.repository.NetworkDetailsCallback;
import lombok.Getter;

/**
 * The presenter of the RedDetails activity of the application. It controls {@link RedDetailsView}
 */
public class RedDetailsPresenter implements IRedDetailsContract.Presenter {

    /**
     * Auxiliary class to save distance between current location and a station,
     * used to sort the stations by distance.
     */
    private static class StationDistance {
        @Getter
        private final Station station;

        @Getter
        private final double distance;

        public StationDistance(Station station, double distance) {
            this.station = station;
            this.distance = distance;
        }
    }

    /** The view that is controlled by this presenter */
    private IRedDetailsContract.View view;

    /** Network of the view */
    private Network network;

    /**
     * @see IRedDetailsContract.Presenter#init(IRedDetailsContract.View, Network)
     * @param view the view to control
     * @param network the network of the view
     */
    @Override
    public void init(IRedDetailsContract.View view, Network network) {
        this.view = view;
        this.view.init();
        this.network = network;
        if (Utils.isNetworkAvailable(view.getContext())) {
            if (!Utils.isLocationEnabled(view.getContext())) {
                view.showLocationError();
            }
            load();
        } else {
            view.showRedError();
        }
    }

    /**
     * @see IRedDetailsContract.Presenter#onFilterClicked(String, String)
     * @param distanceMinText minimum distance introduced by the user
     * @param distanceMaxText maximum distance introduced by the user
     */
    @Override
    public void onFilterClicked(String distanceMinText, String distanceMaxText) {

        double distanceMin = 0.0;
        double distanceMax = Double.MAX_VALUE;

        try {
            if (distanceMinText != null && !distanceMinText.trim().isEmpty()) {
                if (distanceMinText.contains(".")) return;
                int min = Integer.parseInt(distanceMinText.trim());
                if (min < 0) return;
                distanceMin = min;
            }

            if (distanceMaxText != null && !distanceMaxText.trim().isEmpty()) {
                if (distanceMaxText.contains(".")) return;
                int max = Integer.parseInt(distanceMaxText.trim());
                if (max < 0) return;
                distanceMax = max;
            }

            // Checks if the range is valid
            if (distanceMin > distanceMax) {
                return;
            }
        } catch (NumberFormatException e) {
            return;
        }

        //Checks if location is enabled
        if (!Utils.isLocationEnabled(view.getContext())) {
            view.showLocationError();
            return;
        }

        //Updates current location
        LocationUtils.updateLocation(() -> {});

        // Obtains stations and applies the filter
        List<Station> stations = network.getStations();
        List<StationDistance> filteredStations = new ArrayList<>();
        for (Station station : stations) {
            double distanceToStation = LocationUtils.calcularDistanciaA(station.getLatitude(), station.getLongitude());

            // Determines to keep the station or not according to the filter
            if (distanceToStation >= distanceMin && distanceToStation <= distanceMax) {
                filteredStations.add(new StationDistance(station, distanceToStation));
            }
        }

        // Sorts the stations by distance
        filteredStations.sort(Comparator.comparingDouble(StationDistance::getDistance));

        // Copies the stations to a new list and sends it to the view
        List<Station> filteredStationsSorted = new ArrayList<>();
        for (StationDistance sd : filteredStations) {
            filteredStationsSorted.add(sd.getStation());
        }

        // Checks if there isn't any station in the list to inform the user
        if (filteredStations.isEmpty()) {
            view.showNoStationsInRangeError();
        }

        view.showStations(filteredStationsSorted);
    }

    /**
     * @see IRedDetailsContract.Presenter#onStationClicked(Station)
     * @param station the station that has been clicked
     */
    @Override
    public void onStationClicked(Station station) {
        view.showStationDetails(station);
    }

    /**
     * Carga las estaciones de la red seleccionada e invoca a la vista para que las muestre.
     * Si se produce un error al recuperar las estaciones de la red, se invoca a la vista para que
     * muestre un mensaje de error en la carga.
     * Si no hay estaciones, invoca a la vista para que muestre un mensaje de que no hay estaciones.
     * .
     */
    protected void load() {
        INetworksRepository repository = view.getNetworksRepository();

        NetworkDetailsCallback callBack = new NetworkDetailsCallback() {

            @Override
            public void onSuccess(Network network) {
                RedDetailsPresenter.this.network = network;
                if (network.getStations() == null || network.getStations().isEmpty()) {
                    view.showNoStations();
                } else {
                    view.showStations(network.getStations());
                    view.showLoadCorrect(network.getStations().size());
                }
            }

            @Override
            public void onFailure(Throwable e) {
                view.showLoadError();
            }
        };
        repository.requestNetwork(callBack, network.getId());
    }
}