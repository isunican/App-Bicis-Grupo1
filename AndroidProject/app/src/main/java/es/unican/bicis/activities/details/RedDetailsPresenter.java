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
    @Override
    public void onStationClicked(Station station) {
        view.showStationDetails(station);
    }

    /**
     * Loads the bike stations from the repository, and sends them to the view
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