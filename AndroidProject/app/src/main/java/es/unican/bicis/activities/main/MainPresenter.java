package es.unican.bicis.activities.main;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.unican.bicis.common.Utils;
import es.unican.bicis.databaseAccess.IFavouriteDAO;
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

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

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
            executor.execute(() -> {
                IFavouriteDAO dao = view != null ? view.getFavouriteDAO() : null;
                if (dao != null) {
                    dao.insertarFavorito(new Favourite(network));
                    Set<String> favoriteIds = new HashSet<>(dao.obtenerNetworkIdsFavoritos());

                    List<Network> networksToSort = lastLoadedNetworks;
                    List<Network> sortedNetworks;
                    if (networksToSort != null) {
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
                        sortedNetworks = favoritas;
                    } else {
                        sortedNetworks = new ArrayList<>();
                    }

                    List<Network> finalNetworks = sortedNetworks;

                    mainHandler.post(() -> {
                        if (view != null) {
                            view.showMarkedAsFavorite(network);
                            view.updateFavorites(favoriteIds);
                            if (!finalNetworks.isEmpty()) {
                                view.showNetworks(finalNetworks);
                            }
                        }
                    });
                }
            });
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
                executor.execute(() -> {
                    IFavouriteDAO favouriteDAO = view != null ? view.getFavouriteDAO() : null;
                    Set<String> redesFavId = new HashSet<>();
                    List<Network> sortedNetworks;

                    if (networks != null) {
                        if (favouriteDAO != null) {
                            redesFavId.addAll(favouriteDAO.obtenerNetworkIdsFavoritos());
                        }

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
                        sortedNetworks = favoritas;
                    } else {
                        sortedNetworks = new ArrayList<>();
                    }

                    List<Network> finalNetworks = sortedNetworks;

                    mainHandler.post(() -> {
                        if (view != null) {
                            view.updateFavorites(redesFavId);
                            view.showNetworks(finalNetworks);
                            view.showLoadCorrect(finalNetworks.size());
                        }
                    });
                });
            }

            @Override
            public void onFailure(Throwable e) {
                mainHandler.post(() -> {
                    if (view != null) {
                        view.showLoadError();
                    }
                });
            }
        };

        repository.requestNetworks(callBack);
    }
}