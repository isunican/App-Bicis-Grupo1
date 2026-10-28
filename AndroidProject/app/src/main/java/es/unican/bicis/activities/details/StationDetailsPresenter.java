package es.unican.bicis.activities.details;

import java.util.ArrayList;
import java.util.List;

import es.unican.bicis.common.Utils;
import es.unican.bicis.model.Incidence;
import es.unican.bicis.model.IncidenceType;
import es.unican.bicis.model.Station;


/**
 * The presenter of the StationDetails activity. It controls {@link StationDetailsView}.
 */
public class StationDetailsPresenter implements IStationDetailsContract.Presenter {

    /** The view that is controlled by this presenter */
    private IStationDetailsContract.View view;

    /** Station displayed in the view */
    private Station station;

    /**
     * @see IStationDetailsContract.Presenter#init(IStationDetailsContract.View, Station)
     * @param view the view to control
     * @param station the station to display
     */
    @Override
    public void init(IStationDetailsContract.View view, Station station) {
        this.view = view;
        this.station = station;
        this.view.init();

        if (this.station == null) {
            this.view.showLoadError();
            return;
        }

        if (Utils.isNetworkAvailable(view.getContext())) {
            load();
        } else {
            view.showRedError();
        }
    }

    /**
     * Loads the incidences for the selected station and requests the view to display them.
     * If an error occurs retrieving the incidences, requests the view to show an error message.
     * If there are no incidences, requests the view to show a message stating that there are none.
     */
    protected void load() {
        // --- TEMPORARY MOCK (uncomment to test list display without persistence) ---
        List<Incidence> mockList = new ArrayList<>();

        Incidence mockIncidence = new Incidence();
        mockIncidence.setId("0");
        mockIncidence.setTypeId(IncidenceType.STATION);
        mockIncidence.setType("Station");
        mockIncidence.setAmount(1);

        Incidence mockIncidence2 = new Incidence();
        mockIncidence2.setId("1");
        mockIncidence2.setTypeId(IncidenceType.BIKE);
        mockIncidence2.setType("Bike");
        mockIncidence2.setAmount(2);

        Incidence mockIncidence3 = new Incidence();
        mockIncidence3.setId("2");
        mockIncidence3.setTypeId(IncidenceType.OTHER);
        mockIncidence3.setType("Cosas ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc");
        mockIncidence3.setAmount(1001);
        Incidence mockIncidence4 = new Incidence();
        mockIncidence4.setId("3");
        mockIncidence4.setTypeId(IncidenceType.OTHER);
        mockIncidence4.setType("Hola");
        mockIncidence4.setAmount(10010);

        mockList.add(mockIncidence);
        mockList.add(mockIncidence2);
        mockList.add(mockIncidence3);
        mockList.add(mockIncidence4);
        view.showIncidences(mockList);


        // --- FINAL REPOSITORY CALL (uncomment when persistence layer is ready) ---
        /*

        IIncidencesRepository repository = view.getIncidencesRepository();

        repository.requestIncidences(new IncidencesCallback() {
            @Override
            public void onSuccess(List<Incidence> incidences) {
                if (incidences == null || incidences.isEmpty()) {
                    //Nos dijo que no mostraramos nada especial
                } else {
                    view.showIncidences(incidences);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                view.showLoadError();
            }
        }, station.getId());
        */
    }
}