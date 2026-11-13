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

    /** The last networks loaded */
    private List<Network> lastLoadedNetworks;

    /** The executor for used for database access */
    private ExecutorService executor;

    /** Used to execute code on the main thread */
    private Handler mainHandler;

    /**
     * @see IMainContract.Presenter#init(IMainContract.View)
     */
    @Override
    public void init(IMainContract.View view) {
        this.view = view;
        this.view.init();
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        if (Utils.isNetworkAvailable(view.getContext())) {
            load();
        } else {
            view.showRedError();
        }
    }

    /**
     * @see IMainContract.Presenter#onNetworkClicked(Network)
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
     */
    @Override
    public void onFavoriteClicked(Network network) {
        // Cláusula de guarda: salimos rápido si los datos son inválidos
        if (network == null || network.getId() == null) {
            return;
        }

        executor.execute(() -> processFavoriteSelection(network));
    }

    /**
     * Procesa la selección de marcar una red como favorita.
     * @param network la red seleccionada
     */
    private void processFavoriteSelection(Network network) {
        if (view == null) return;

        IFavouriteDAO dao = view.getFavouriteDAO();
        if (dao == null) return;

        dao.insertarFavorito(new Favourite(network));
        Set<String> favoriteIds = new HashSet<>(dao.obtenerNetworkIdsFavoritos());

        List<Network> sortedNetworks = buildSortedNetworkList(lastLoadedNetworks, favoriteIds);

        mainHandler.post(() -> updateUiElements(network, favoriteIds, sortedNetworks));
    }

    /**
     * Construye una nueva lista de redes ordenadas, situando en primer lugar las redes
     * marcadas como favoritas y a continuación el resto de redes.
     *
     * @param networks lista original de redes de bicicletas
     * @param favoriteIds conjunto de identificadores de las redes favoritas
     * @return lista de redes reordenada colocando las favoritas al principio
     */
    private List<Network> buildSortedNetworkList(List<Network> networks, Set<String> favoriteIds) {
        if (networks == null || networks.isEmpty()) {
            return new ArrayList<>();
        }

        List<Network> favoritas = new ArrayList<>();
        List<Network> resto = new ArrayList<>();

        for (Network net : networks) {
            if (net == null) continue;

            if (net.getId() != null && favoriteIds.contains(net.getId())) {
                favoritas.add(net);
            } else {
                resto.add(net);
            }
        }

        favoritas.addAll(resto);
        return favoritas;
    }

    /**
     * Actualiza los elementos de la interfaz de usuario con los nuevos datos.
     * @param network red marcada como favorita
     * @param favoriteIds conjunto de identificadores de las redes favoritas
     * @param sortedNetworks lista de redes reordenada
     */
    private void updateUiElements(Network network, Set<String> favoriteIds, List<Network> sortedNetworks) {
        if (view == null) return;

        view.showMarkedAsFavorite(network);
        view.updateFavorites(favoriteIds);

        if (!sortedNetworks.isEmpty()) {
            view.showNetworks(sortedNetworks);
        }
    }

    /**
     * Loads the bike networks from the repository, and sends them to the view
     */
    private void load() {
        INetworksRepository repository = view.getNetworksRepository();

        NetworksCallback callBack = new NetworksCallback() {

            /**
             * @see NetworksCallback#onSuccess(List)
             */
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

            /**
             * @see NetworksCallback#onFailure(Throwable)
             */
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