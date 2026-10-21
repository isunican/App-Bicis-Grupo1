package es.unican.bicis.activities.details;

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
     * @see IRedDetailsContract.Presenter#onFilterClicked(String, DistanceFilter)
     * @param distanceText distance introduced by the user
     * @param filter filter introduced by the user (less than distance or more than distance).
     */
    @Override
    public void onFilterClicked(String distanceText, DistanceFilter filter) {

        // Checks if distance string contains decimal units, is empty or is a negative number
        if (distanceText.isEmpty() || distanceText.contains(".") || Integer.parseInt(distanceText) < 0) {
            return;
        }

        double distance;
        distance = Double.parseDouble(distanceText);

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
            boolean keep = (filter == DistanceFilter.LESS_THAN)
                    ? distanceToStation < distance // if filter is less than
                    : distanceToStation > distance; // if filter is more than

            if (keep) {
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
    private void load() {
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