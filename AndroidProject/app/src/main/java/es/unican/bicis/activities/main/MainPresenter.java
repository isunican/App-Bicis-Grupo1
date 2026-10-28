package es.unican.bicis.activities.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.unican.bicis.common.Utils;
import es.unican.bicis.databaseAcces.IFavouriteDAO;
import es.unican.bicis.model.Favourite;
import es.unican.bicis.model.Network;
import es.unican.bicis.repository.INetworksRepository;
import es.unican.bicis.repository.NetworksCallback;

/**
 * The presenter of the main activity of the application. It controls {@link MainView}
 */
public class MainPresenter implements IMainContract.Presenter {

    /** The view that is controlled by this presenter */
    private IMainContract.View view;
    private List<Network> lastLoadedNetworks;

    /**
     * @see IMainContract.Presenter#init(IMainContract.View)
     * @param view the view to control
     */
    @Override
    public void init(IMainContract.View view) {
        this.view = view;
        this.view.init();
        if (Utils.isNetworkAvailable(view.getContext())) {
            load();
        } else {
            view.showRedError();
        }
    }

    /**
     * @see IMainContract.Presenter#onNetworkClicked(Network)
     * @param network the network that has been clicked
     */
    @Override
    public void onNetworkClicked(Network network) {
        view.showNetworkDetails(network);
    }

    /**
     * @see IMainContract.Presenter#onMenuInfoClicked()
     */
    @Override
    public void onMenuInfoClicked() {
        view.showInfoActivity();
    }

    /**
     * @see IMainContract.Presenter#onFavoriteClicked(Network)
     * @param network the network that was clicked for marking as favorite
     */
    @Override
    public void onFavoriteClicked(Network network) {
        if (network != null && network.getId() != null) {
            IFavouriteDAO dao = view.getFavouriteDAO();
            if (dao != null) {
                dao.insertarFavorito(new Favourite(network));
                view.showMarkedAsFavorite(network);

                List<Network> networksToSort = lastLoadedNetworks;
                if (networksToSort != null) {
                    Set<String> favoriteIds = new HashSet<>(dao.obtenerNetworkIdsFavoritos());

                    List<Network> favoritas = new ArrayList<>();
                    List<Network> resto = new ArrayList<>();

                    for (Network net : networksToSort) {
                        if (net != null && net.getId() != null && favoriteIds.contains(net.getId())) {
                            favoritas.add(net);
                        } else if (net != null) {
                            resto.add(net);
                        }
                    }
                    favoritas.addAll(resto);
                    view.showNetworks(favoritas);
                }
            }
        }
    }

    /**
     * Loads the bike networks from the repository, and sends them to the view
     */
    private void load() {
        INetworksRepository repository = view.getNetworksRepository();

        NetworksCallback callBack = new NetworksCallback() {

            @Override
            public void onSuccess(List<Network> networks) {
                lastLoadedNetworks = networks;
                IFavouriteDAO favouriteDAO = view.getFavouriteDAO();
                if (favouriteDAO != null && networks != null) {
                    Set<String> redesFavId = new HashSet<>(favouriteDAO.obtenerNetworkIdsFavoritos());

                    List<Network> favoritas = new ArrayList<>();
                    List<Network> resto = new ArrayList<>();

                    for (Network net : networks) {
                        if (net != null && net.getId() != null && redesFavId.contains(net.getId())) {
                            favoritas.add(net);
                        } else if (net != null) {
                            resto.add(net);
                        }
                    }
                    favoritas.addAll(resto);
                    networks = favoritas;
                }
                view.showNetworks(networks);
                view.showLoadCorrect(networks != null ? networks.size() : 0);
            }

            @Override
            public void onFailure(Throwable e) {
                view.showLoadError();
            }
        };

        repository.requestNetworks(callBack);
    }
}