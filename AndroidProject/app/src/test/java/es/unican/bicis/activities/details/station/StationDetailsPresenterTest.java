package es.unican.bicis.activities.details.station;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;


import es.unican.bicis.databaseAccess.AppDatabase;
import es.unican.bicis.databaseAccess.IIncidentDAO;
import es.unican.bicis.model.Incident;
import es.unican.bicis.model.IncidentType;
import es.unican.bicis.model.Station;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29)
public class StationDetailsPresenterTest {
    private StationDetailsPresenter sutPresenter;
    private IStationDetailsContract.View mockView;
    private AppDatabase mockDb;
    private IIncidentDAO mockDao;
    private Station mockStation;

    @Before
    public void setUp() {
        //Initializes mocks
        mockView =  mock(IStationDetailsContract.View.class);
        mockDb = mock(AppDatabase.class);
        mockDao = mock(IIncidentDAO.class);
        mockStation = mock(Station.class);

        //Replaces initial mocks behavior
        when(mockDb.incidenciaDao()).thenReturn(mockDao);
        when(mockStation.getId()).thenReturn("id-station-tests");

        //Initializes presenter
        sutPresenter = new StationDetailsPresenter();
    }

    @Test
    public void loadIncidentsStationSuccess() throws InterruptedException {
        // Creates list of incidents
        List<Incident> incidents = Arrays.asList(
                new Incident("id-station-tests", IncidentType.BIKE, "BiciRota"),
                new Incident("id-station-tests", IncidentType.STATION, "EstacionRota"),
                new Incident("id-station-tests", IncidentType.OTHER, "Other incident")
        );

        // Replaces dao mock behavior, returns a list when called with the station id
        when(mockDao.obtenerIncidenciasPorEstacion("id-station-tests")).thenReturn(incidents);

        // Calls init() from presenter, method that calls load()
        sutPresenter.init(mockView, mockStation, mockDb);

        verify(mockDb).incidenciaDao();
        verify(mockDao).obtenerIncidenciasPorEstacion("id-station-tests");
        verify(mockView).showIncidences(incidents);
        verify(mockView, never()).showLoadError();
    }
}
