package es.unican.bicis.activities.details;

import android.content.Context;
import java.util.List;

import es.unican.bicis.model.Incidence;
import es.unican.bicis.model.Station;
// TODO: Uncomment when repository interface exists
// import es.unican.bicis.repository.IIncidencesRepository;

/**
 * The Presenter-View contract for the StationDetails activity.
 * The StationDetails activity displays details and incidences of a specific bike station.
 */
public interface IStationDetailsContract {

    /**
     * Methods that must be implemented in the StationDetails Presenter.
     * Only the View should call these methods.
     */
    interface Presenter {

        /**
         * Links the presenter with its view.
         * Only the View should call this method.
         *
         * @param view view to be linked
         * @param station station whose details and incidences are managed
         */
        void init(View view, Station station);

        // TODO: Add when clicking on an incidence is supported
        // void onIncidenceClicked(Incidence incidence);
    }

    /**
     * Methods that must be implemented in the StationDetails View.
     * Only the Presenter should call these methods.
     */
    interface View {

        /**
         * Initialize the view. Typically this initializes views and text contents.
         * Only the Presenter should call this method.
         */
        void init();

        /**
         * Returns a repository that can be called by the Presenter to retrieve incidences.
         * Only the Presenter should call this method.
         *
         * @return repository to access incidence data
         */
        // TODO: Uncomment when persistence layer is implemented
        // IIncidencesRepository getIncidencesRepository();

        /**
         * The view is requested to display the given list of incidences.
         * Only the Presenter should call this method.
         *
         * @param incidences list of incidences of the station
         */
        void showIncidences(List<Incidence> incidences);


        /**
         * The view is requested to display a notification indicating that the incidences
         * could not be loaded.
         * Only the Presenter should call this method.
         */
        void showLoadError();


        /**
         * The view is requested to return the context of the application.
         * Only the Presenter should call this method.
         *
         * @return application context
         */
        Context getContext();
    }
}