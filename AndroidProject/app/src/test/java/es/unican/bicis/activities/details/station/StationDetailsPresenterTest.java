package es.unican.bicis.activities.details.station;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.database.sqlite.SQLiteException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.unican.bicis.databaseAccess.AppDatabase;
import es.unican.bicis.databaseAccess.IIncidentDAO;
import es.unican.bicis.model.Incident;
import es.unican.bicis.model.IncidentType;
import es.unican.bicis.model.Station;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29)
@LooperMode(LooperMode.Mode.INSTRUMENTATION_TEST)
public class StationDetailsPresenterTest {

    private StationDetailsPresenter sut;

    private static Station station;
    @Mock
    private AppDatabase mockAppDatabase;
    @Mock
    private IStationDetailsContract.View mockStationDetailsView;
    @Mock
    private IIncidentDAO mockIIncidentDAO;

    @BeforeClass
    public static void setUpStation() {
        station = new Station();
        station.setId("0a7638aa73949d3c8742f13f56cedff0");
        station.setName("Perinés");
        station.setLatitude(43.459994);
        station.setLongitude(-3.820141);
        station.setTimestamp("2026-09-24T11:11:30.548463+00:00Z");
        station.setFreeBikes(5);
        station.setEmptySlots(7);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockAppDatabase.incidenciaDao()).thenReturn(mockIIncidentDAO);

        sut = new StationDetailsPresenter();
    }

    @After
    public void tearDown() {
        if (sut != null) {
            sut.onDestroy();
        }
    }

    @Test
    public void registrarIncidenciaTest_PU01 () {

        sut.init(mockStationDetailsView, station, mockAppDatabase);
        sut.registrarIncidencia(IncidentType.BIKE, null);

        ArgumentCaptor<Incident> incidentCaptor = ArgumentCaptor.forClass(Incident.class);
        verify(mockIIncidentDAO, timeout(1000)).insertarIncidencia(incidentCaptor.capture());

        Incident newIncident = incidentCaptor.getValue();
        assertEquals(station.getId(), newIncident.getIdEstacion());
        assertEquals(IncidentType.BIKE, newIncident.getTypeId());
        assertEquals(IncidentType.BIKE.getDescripcion(), newIncident.getType());
        assertEquals(0, newIncident.getAmount());

        verify(mockStationDetailsView, after(1000).never()).showDatabaseError();
        verify(mockStationDetailsView, never()).showEmptyIssueError();
    }

    @Test
    public void registrarIncidenciaTest_PU02 () {

        sut.init(mockStationDetailsView, station, mockAppDatabase);
        sut.registrarIncidencia(IncidentType.OTHER, "Luz fundida");

        ArgumentCaptor<Incident> incidentCaptor = ArgumentCaptor.forClass(Incident.class);
        verify(mockIIncidentDAO, timeout(1000)).insertarIncidencia(incidentCaptor.capture());

        Incident newIncident = incidentCaptor.getValue();
        assertEquals(station.getId(), newIncident.getIdEstacion());
        assertEquals(IncidentType.OTHER, newIncident.getTypeId());
        assertEquals("Luz fundida", newIncident.getType());
        assertEquals(0, newIncident.getAmount());

        verify(mockStationDetailsView, after(1000).never()).showDatabaseError();
        verify(mockStationDetailsView, never()).showEmptyIssueError();
    }

    @Test
    public void registrarIncidenciaTest_PU03 () {

        sut.init(mockStationDetailsView, station, mockAppDatabase);
        sut.registrarIncidencia(IncidentType.BIKE, "Bici");

        ArgumentCaptor<Incident> incidentCaptor = ArgumentCaptor.forClass(Incident.class);
        verify(mockIIncidentDAO, timeout(1000)).insertarIncidencia(incidentCaptor.capture());

        Incident newIncident = incidentCaptor.getValue();
        assertEquals(station.getId(), newIncident.getIdEstacion());
        assertEquals(IncidentType.BIKE, newIncident.getTypeId());
        assertEquals(IncidentType.BIKE.getDescripcion(), newIncident.getType());
        assertEquals(0, newIncident.getAmount());

        verify(mockStationDetailsView, after(1000).never()).showDatabaseError();
        verify(mockStationDetailsView, never()).showEmptyIssueError();
    }

    @Test
    public void registrarIncidenciaTest_PU04 () {

        sut.init(mockStationDetailsView, station, mockAppDatabase);
        sut.registrarIncidencia(IncidentType.OTHER, "");

        verify(mockIIncidentDAO, after(1000).never()).insertarIncidencia(any(Incident.class));
        verify(mockStationDetailsView, after(1000).never()).showDatabaseError();
        verify(mockStationDetailsView).showEmptyIssueError();
    }

    @Test
    public void registrarIncidenciaTest_PU05 () {

        sut.init(mockStationDetailsView, station, mockAppDatabase);
        sut.registrarIncidencia(IncidentType.OTHER, null);

        verify(mockIIncidentDAO, after(1000).never()).insertarIncidencia(any(Incident.class));
        verify(mockStationDetailsView, after(1000).never()).showDatabaseError();
        verify(mockStationDetailsView).showEmptyIssueError();
    }

    @Test
    public void registrarIncidenciaTest_PU06 () {

        sut.init(mockStationDetailsView, station, mockAppDatabase);
        sut.registrarIncidencia(null, "Bici");

        verify(mockIIncidentDAO, after(1000).never()).insertarIncidencia(any(Incident.class));
        verify(mockStationDetailsView, after(1000).never()).showDatabaseError();
        verify(mockStationDetailsView, never()).showEmptyIssueError();
    }

    @Test
    public void registrarIncidenciaTest_PU07 () {

        doThrow(new SQLiteException()).when(mockIIncidentDAO).insertarIncidencia(any(Incident.class));

        sut.init(mockStationDetailsView, station, mockAppDatabase);
        sut.registrarIncidencia(IncidentType.BIKE, null);

        verify(mockIIncidentDAO, timeout(2000)).insertarIncidencia(any(Incident.class));
        verify(mockStationDetailsView, timeout(2000)).showDatabaseError();
        verify(mockStationDetailsView, never()).showEmptyIssueError();
    }

    @Test
    public void loadIncidentsStationSuccess() {
        // Creates list of incidents
        List<Incident> incidents = Arrays.asList(
                new Incident("0a7638aa73949d3c8742f13f56cedff0", IncidentType.BIKE, "BiciRota"),
                new Incident("0a7638aa73949d3c8742f13f56cedff0", IncidentType.STATION, "EstacionRota"),
                new Incident("0a7638aa73949d3c8742f13f56cedff0", IncidentType.OTHER, "Other incident")
        );

        // Replaces dao mock behavior, returns a list when called with the station id
        when(mockIIncidentDAO.obtenerIncidenciasPorEstacion("0a7638aa73949d3c8742f13f56cedff0")).thenReturn(incidents);

        // Calls init() from presenter, method that calls load()
        sut.init(mockStationDetailsView, station, mockAppDatabase);

        verify(mockAppDatabase, timeout(2000)).incidenciaDao();
        verify(mockIIncidentDAO, timeout(2000)).obtenerIncidenciasPorEstacion("0a7638aa73949d3c8742f13f56cedff0");
        verify(mockStationDetailsView, timeout(2000)).showIncidences(incidents);
        verify(mockStationDetailsView, never()).showLoadError();
    }

    @Test
    public void loadIncidentsStationEmpty() {
        List<Incident> emptyList = new ArrayList<>();
        // Replaces dao mock behavior, returns a list when called with the station id
        when(mockIIncidentDAO.obtenerIncidenciasPorEstacion("0a7638aa73949d3c8742f13f56cedff0")).thenReturn(null);

        // Calls init() from presenter, method that calls load()
        sut.init(mockStationDetailsView, station, mockAppDatabase);

        verify(mockAppDatabase, timeout(2000)).incidenciaDao();
        verify(mockIIncidentDAO, timeout(2000)).obtenerIncidenciasPorEstacion("0a7638aa73949d3c8742f13f56cedff0");
        verify(mockStationDetailsView, timeout(2000)).showIncidences(emptyList); // If dao returns null, an empty list is used
        verify(mockStationDetailsView, never()).showLoadError();
    }

    @Test
    public void loadIncidentsStationError() {
        // Replaces dao mock behavior, throws an exception when called
        when(mockIIncidentDAO.obtenerIncidenciasPorEstacion("0a7638aa73949d3c8742f13f56cedff0")).thenThrow(new SQLiteException());

        // Calls init() from presenter, method that calls load()
        sut.init(mockStationDetailsView, station, mockAppDatabase);

        verify(mockAppDatabase, timeout(2000)).incidenciaDao();
        verify(mockIIncidentDAO, timeout(2000)).obtenerIncidenciasPorEstacion("0a7638aa73949d3c8742f13f56cedff0");
        verify(mockStationDetailsView, timeout(2000)).showLoadError();
        verify(mockStationDetailsView, never()).showIncidences(ArgumentMatchers.anyList());
    }
}