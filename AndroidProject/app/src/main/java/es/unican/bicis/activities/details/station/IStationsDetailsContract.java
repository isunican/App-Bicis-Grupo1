package es.unican.bicis.activities.details.station;

import android.content.Context;
import es.unican.bicis.model.Station;

/**
 * The Presenter-View contract for the StationDetails activity.
 * The StationDetails activity shows the details of a specific bike station
 * and allows the user to register incidents.
 */
public interface IStationsDetailsContract {

    /**
     * Methods that must be implemented in the StationDetails Presenter.
     * Only the View should call these methods.
     */
    public interface Presenter {

        /**
         * Links the presenter with its view.
         * Only the View should call this method
         *
         * @param view view to be linked
         * @param station station of the view
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
     * Methods that must be implemented in the StationDetail View.
     * Only the Presenter should call these methods.
     */
    public interface View {

        /**
         * Initialize the view. Typically this should initialize all the listeners in the view.
         * Only the Presenter should call this method
         */
        void init();

        /**
         * The view is requested to display a notification indicating that there is not database connection.
         * Only the Presenter should call this method
         */
        void showDatabaseError();


        /**
         * The view is requested to return the context of the application.
         * Only the Presenter should call this method
         *
         * @return the application context
         */
        Context getContext();

        /**
         * The view is requested to display a notification indicating
         * that the text with the description of other issue is empty.
         * Only the Presenter should call this method
         */
        void showEmptyIssueError();
    }
}