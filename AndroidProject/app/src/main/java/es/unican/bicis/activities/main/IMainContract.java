package es.unican.bicis.activities.main;

import android.content.Context;

import java.util.List;
import java.util.Set;

import es.unican.bicis.databaseAccess.IFavouriteDAO;
import es.unican.bicis.model.Network;
import es.unican.bicis.repository.INetworksRepository;

/**
 * The Presenter-View contract for the Main activity.
 * The Main activity shows a list of bike sharing networks.
 */
public interface IMainContract {

    /**
     * Methods that must be implemented in the Main Presenter.
     * Only the View should call these methods.
     */
    public interface Presenter {

        /**
         * Links the presenter with its view.
         * Only the View should call this method
         * @param view
         */
        public void init(View view);

        /**
         * The presenter is informed that a network has been clicked
         * Only the View should call this method
         * @param network the network that has been clicked
         */
        public void onNetworkClicked(Network network);

        /**
         * The presenter is informed that the Info item in the menu has been clicked
         * Only the View should call this method
         */
        public void onMenuInfoClicked();

        /**
         * The presenter is informed that the favorite option for a network has been clicked
         * Only the View should call this method
         * @param network the network
         */
        public void onFavoriteClicked(Network network);

    }

    /**
     * Methods that must be implemented in the Main View.
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
         * Returns the DAO to access favorite networks in Room.
         * Only the Presenter should call this method
         * @return the IFavouriteDAO
         */
        public IFavouriteDAO getFavouriteDAO();

        /**
         * The view is requested to display the given list of bike networks.
         * Only the Presenter should call this method
         * @param networks the list of networks
         */
        public void showNetworks(List<Network> networks);

        /**
         * The view is requested to display a notification indicating  that the networks
         * were loaded correctly.
         * Only the Presenter should call this method
         * @param networks
         */
        public void showLoadCorrect(int networks);

        /**
         * The view is requested to display a notificacion indicating that the networks
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
         * The view is requested to return the context of the application.
         * Only the Presenter should call this method
         */
        public Context getContext();

        /**
         * The view is requested to display the detailed view of the given network.
         * Only the Presenter should call this method
         * @param network the network
         */
        public void showNetworkDetails(Network network);

        /**
         * The view is requested to open the info activity.
         * Only the Presenter should call this method
         */
        public void showInfoActivity();

        /**
         * The view is requested to display a notification indicating that the network
         * was marked as favorite.
         * Only the Presenter should call this method
         * @param network the network
         */
        public void showMarkedAsFavorite(Network network);


        /**
         * The view is requested to update the list of favorite network IDs and refresh the list UI.
         * Only the Presenter should call this method
         * @param favoriteNetworkIds set of favorite network IDs
         */
        public void updateFavorites(Set<String> favoriteNetworkIds);

    }
}