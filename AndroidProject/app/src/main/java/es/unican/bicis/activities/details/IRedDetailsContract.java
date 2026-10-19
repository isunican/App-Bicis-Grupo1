package es.unican.bicis.activities.details;

import android.content.Context;

import java.util.List;

import es.unican.bicis.model.Network;
import es.unican.bicis.model.Station;
import es.unican.bicis.repository.INetworksRepository;

/**
 * The Presenter-View contract for the RedDetails activity.
 * The RedDetails activity shows a list of bike sharing stations.
 */
public interface IRedDetailsContract {

    /**
     * Methods that must be implemented in the RedDetails Presenter.
     * Only the View should call these methods.
     */
    public interface Presenter {

        /**
         * Links the presenter with its view.
         * Only the View should call this method
         *
         * @param view view to be linked
         * @param network network of the view
         */
        public void init(View view, Network network);

        /**
         * The presenter is informed that a station has been clicked
         * Only the View should call this method
         *
         * @param station the station that has been clicked
         */
        //public void onStationClicked(Station station);

        /**
         * The presenter is informed that the user wants to filter the stations by distance.
         *
         * @param distance distance introduced by the user
         * @param filter filter introduced by the user (less than distance or more than distance).
         */
        public void onFilterClicked(String distance, DistanceFilter filter);
    }


    /**
     * Methods that must be implemented in the RedDetail View.
     * Only the Presenter should call these methods.
     */
    public interface View {

        /**
         * Initialize the view. Typically this should initialize all the listeners in the view.
         * Only the Presenter should call this method
         */
        public void init();

        /**
         * Returns a repository that can be called by the Presenter to retrieve bike networks.
         * This method must be located in the view because Android resources must be accessed
         * in order to instantiate a repository (for example Internet Access). This requires
         * dependencies to Android. We want to keep the Presenter free of Android dependencies,
         * therefore the Presenter should be unable to instantiate repositories and must rely on
         * the view to create the repository.
         * Only the Presenter should call this method
         * @return
         */
        public INetworksRepository getNetworksRepository();

        /**
         * The view is requested to display the given list of bike stations.
         * Only the Presenter should call this method
         * @param stations list of the stations of the network
         */
        public void showStations(List<Station> stations);

        /**
         * The view is requested to display that there are no stations.
         * Only the Presenter should call this method
         */
        public void showNoStations();

        /**
         * The view is requested to display a notification indicating  that the stations
         * were loaded correctly.
         * Only the Presenter should call this method
         * @param stations
         */
        public void showLoadCorrect(int stations);

        /**
         * The view is requested to display a notificacion indicating that the stations
         * were not loaded correctly.
         * Only the Presenter should call this method
         */
        public void showLoadError();

        /**
         * The view is requested to display a notificacion indicating that there is not conexion
         * Only the Presenter should call this method
         */
        public void showRedError();

        /**
         * The view is requested to display a notificacion indicating that there is not location
         * Only the Presenter should call this method
         */
        public void showLocationError();

        /**
         * The view is requested to display a notification indicating that there are no stations
         * in the given distance.
         */
        public void showNoStationsInRangeError();

        /**
         * The view is requested to return the context of the application.
         * Only the Presenter should call this method
         */
        public Context getContext();

        /**
         * The view is requested to display the detailed view of the given network.
         * Only the Presenter should call this method
         * @param network the network
         */
        //public void showNetworkDetails(Network network);

    }
}