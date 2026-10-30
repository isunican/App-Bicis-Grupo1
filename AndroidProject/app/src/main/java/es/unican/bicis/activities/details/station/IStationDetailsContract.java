package es.unican.bicis.activities.details.station;

import android.content.Context;
import java.util.List;

import es.unican.bicis.model.Incident;
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

        /**
         * Registers a "Damaged station" incident in the database.
         *
         * @param estacionId the ID of the station
         */
        void registrarIncidenciaEstacionRota(String estacionId);

        /**
         * Registers a "Defective bike" incident in the database.
         *
         * @param estacionId the ID of the station
         */
        void registrarIncidenciaBiciRota(String estacionId);

        /**
         * Registers an "Other issue" incident with a custom text in the database.
         * If the text is empty, it notifies the view to show an error.
         *
         * @param textoPersonalizado the custom description of the incident
         * @param estacionId the ID of the station
         */
        void registrarIncidenciaOtro(String textoPersonalizado, String estacionId);

        /**
         * Cleans up resources (like background threads) when the view is destroyed.
         */
        void onDestroy();

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
         * The view is requested to display the given list of incidences.
         * Only the Presenter should call this method.
         *
         * @param incidences list of incidences of the station
         */
        void showIncidences(List<Incident> incidences);


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


        /**
         * The view is requested to display a notification indicating that there is not database connection.
         * Only the Presenter should call this method
         */
        void showDatabaseError();


        /**
         * The view is requested to display a notification indicating
         * that the text with the description of other issue is empty.
         * Only the Presenter should call this method
         */
        void showEmptyIssueError();



    }
}