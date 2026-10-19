package es.unican.bicis.activities.details;

import java.util.List;

import es.unican.bicis.common.Utils;
import es.unican.bicis.model.Network;
import es.unican.bicis.model.Station;
import es.unican.bicis.repository.INetworksRepository;
import es.unican.bicis.repository.NetworkDetailsCallback;
import es.unican.bicis.repository.NetworksCallback;

/**
 * The presenter of the RedDetails activity of the application. It controls {@link RedDetailsView}
 */
public class RedDetailsPresenter implements IRedDetailsContract.Presenter {

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
     * @see IRedDetailsContract.Presenter#onStationClicked(Station)
     * @param station the station that has been clicked
     */
    //@Override
    //public void onNetworkClicked(Network network) {
        //view.showNetworkDetails(network);
    //}

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
                if (network.getStations().size() == 0) {
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