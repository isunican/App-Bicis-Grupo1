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

    private ExecutorService executor;
    private Handler mainHandler;


    /**
     * @see IMainContract.Presenter#init(IMainContract.View)
     * @param view the view to control
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
        // Cláusula de guarda: salimos rápido si los datos son inválidos
        if (network == null || network.getId() == null) {
            return;
        }

        executor.execute(() -> processFavoriteSelection(network));
    }

    /**
     * @see IMainContract.Presenter#onFilterClicked(String)
     * @param query search string introduced by the user
     */
    @Override
    public void onFilterClicked(String query) {
        if (view == null) return;

        //En caso de que la cadenas sea nula o vacia la mostramos un error
        if (query == null || query.trim().isEmpty()) {
            view.showEmptySearchError();
            return;
        }

        //Eliminamos los espacios en blanco
        String trimmedQuery = query.trim();

        //Si contiene emoticonos mostramos un error
        if (containsEmoji(trimmedQuery)) {
            view.showEmojiSearchError();
            return;
        }

        //Si no hay redes que poder cargar mostramos error
        if (lastLoadedNetworks == null || lastLoadedNetworks.isEmpty()) {
            view.showNoNetworksFoundError();
            return;
        }

        //Convertimos la cadena a minusculas para comparar
        String lowerQuery = trimmedQuery.toLowerCase();

        //Filtramos las redes entre las que encajen perfectamente y parcialmente
        List<Network> exactMatches = new ArrayList<>();
        List<Network> partialMatches = new ArrayList<>();

        //Recorremos todas las redes y las comparamos
        for (Network net : lastLoadedNetworks) {
            // Evitamos redes nulas o sin nombre
            if (net == null || net.getName() == null) continue;

            // Comprobamos si el nombre de la red contiene la cadena
            String name = net.getName();
            if (name.equalsIgnoreCase(trimmedQuery)) {
                exactMatches.add(net);
            } else if (name.toLowerCase().contains(lowerQuery)) {
                partialMatches.add(net);
            }
        }

        //Si no hay coincidencias mostramos error
        if (exactMatches.isEmpty() && partialMatches.isEmpty()) {
            view.showNoNetworksFoundError();
            return;
        }

        // Redes con coincidencia exacta en las primeras posiciones
        List<Network> filteredNetworks = new ArrayList<>(exactMatches);
        filteredNetworks.addAll(partialMatches);

        // Límite de 20 redes
        if (filteredNetworks.size() > 20) {
            filteredNetworks = new ArrayList<>(filteredNetworks.subList(0, 20));
        }

        view.showNetworks(filteredNetworks);
    }

    /**
     * Comprueba si la cadena dada contiene emoticonos.
     * @param text texto a comprobar
     * @return true si contiene emoticonos, false en caso contrario
     */
    private boolean containsEmoji(String text) {
        if (text == null) return false;
        for (int i = 0; i < text.length(); ) {
            int codePoint = text.codePointAt(i);
            if (isEmojiCodePoint(codePoint)) {
                return true;
            }
            i += Character.charCount(codePoint);
        }
        return false;
    }


    /**
     * Comprueba si es un codigo de emoji u otro simbolo no permitido
     * @param codePoint codigo del simbolo
     * @return true si es un simbolo no permitido, false en caso contrario
     */
    private boolean isEmojiCodePoint(int codePoint) {
        if ((codePoint >= 0x1F600 && codePoint <= 0x1F64F) || // Emoticons
            (codePoint >= 0x1F300 && codePoint <= 0x1F5FF) || // Misc Symbols and Pictographs
            (codePoint >= 0x1F680 && codePoint <= 0x1F6FF) || // Transport and Map
            (codePoint >= 0x1F1E0 && codePoint <= 0x1F1FF) || // Flags
            (codePoint >= 0x2600 && codePoint <= 0x26FF) ||   // Misc Symbols
            (codePoint >= 0x2700 && codePoint <= 0x27BF) ||   // Dingbats
            (codePoint >= 0x1F900 && codePoint <= 0x1F9FF) || // Supplemental Symbols
            (codePoint >= 0x1FA00 && codePoint <= 0x1FA6F) || // Symbols Extended-A
            (codePoint >= 0x1FA70 && codePoint <= 0x1FAFF)) { // Symbols Extended-B
            return true;
        }
        int type = Character.getType(codePoint);
        return type == Character.SURROGATE || type == Character.OTHER_SYMBOL;
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