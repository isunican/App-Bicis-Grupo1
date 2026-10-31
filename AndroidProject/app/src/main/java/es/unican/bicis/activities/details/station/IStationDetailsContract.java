package es.unican.bicis.activities.details.station;

import android.content.Context;

import es.unican.bicis.databaseAccess.AppDatabase;
import es.unican.bicis.model.Station;

/**
 * The Presenter-View contract for the StationDetails activity.
 * The StationDetails activity shows the details of a specific bike station
 * and allows the user to register incidents.
 */
public interface IStationDetailsContract {

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
         * @param db the app database
         */
        public void init(View view, Station station, AppDatabase db);

        /**
         * Registers a "Damaged station" incident in the database.
         * If there is a database connection error, it notifies the view to show an error.
         */
        public void registrarIncidenciaEstacionRota();

        /**
         * Registers a "Defective bike" incident in the database.
         * If there is a database connection error, it notifies the view to show an error.
         */
        public void registrarIncidenciaBiciRota();

        /**
         * Registers an "Other issue" incident with a custom text in the database.
         * If the text is empty or null, it notifies the view to show an error.
         * If there is a database connection error, it notifies the view to show an error.
         *
         * @param textoPersonalizado the custom description of the incident
         */
        public void registrarIncidenciaOtro(String textoPersonalizado);

        /**
         * Cleans up resources (like background threads) when the view is destroyed.
         */
        public void onDestroy();
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
        public void init();

        /**
         * The view is requested to display a notification indicating that there is not database connection.
         * Only the Presenter should call this method
         */
        public void showDatabaseError();

        /**
         * The view is requested to return the context of the application.
         * Only the Presenter should call this method
         *
         * @return the application context
         */
        public Context getContext();

        /**
         * The view is requested to display a notification indicating
         * that the text with the description of other issue is empty.
         * Only the Presenter should call this method
         */
        public void showEmptyIssueError();

        /**
         * Cleans up resources (like background threads) when the view is destroyed.
         */
        public void onDestroy();
    }
}