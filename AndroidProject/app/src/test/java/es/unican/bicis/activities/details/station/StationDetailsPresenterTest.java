package es.unican.bicis.activities.details.station;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.database.sqlite.SQLiteException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import es.unican.bicis.databaseAccess.AppDatabase;
import es.unican.bicis.databaseAccess.IIncidentDAO;
import es.unican.bicis.model.Incident;
import es.unican.bicis.model.IncidentType;
import es.unican.bicis.model.Station;

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
        sut = new StationDetailsPresenter();
        sut.init(mockStationDetailsView, station, mockAppDatabase);

        when(mockAppDatabase.incidenciaDao()).thenReturn(mockIIncidentDAO);
    }

    @After
    public void tearDown() {
        if (sut != null) {
            sut.onDestroy();
        }
    }

    @Test
    public void registrarIncidenciaTest_PU01 () {

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

        sut.registrarIncidencia(IncidentType.OTHER, "");

        verify(mockIIncidentDAO, after(1000).never()).insertarIncidencia(any(Incident.class));
        verify(mockStationDetailsView, after(1000).never()).showDatabaseError();
        verify(mockStationDetailsView).showEmptyIssueError();
    }

    @Test
    public void registrarIncidenciaTest_PU05 () {

        sut.registrarIncidencia(IncidentType.OTHER, null);

        verify(mockIIncidentDAO, after(1000).never()).insertarIncidencia(any(Incident.class));
        verify(mockStationDetailsView, after(1000).never()).showDatabaseError();
        verify(mockStationDetailsView).showEmptyIssueError();
    }

    @Test
    public void registrarIncidenciaTest_PU06 () {

        sut.registrarIncidencia(null, "Bici");

        verify(mockIIncidentDAO, after(1000).never()).insertarIncidencia(any(Incident.class));
        verify(mockStationDetailsView, after(1000).never()).showDatabaseError();
        verify(mockStationDetailsView, never()).showEmptyIssueError();
    }

    @Test
    public void registrarIncidenciaTest_PU07 () {

        when(mockIIncidentDAO.insertarIncidencia(any(Incident.class))).thenThrow(new SQLiteException());
        sut.registrarIncidencia(IncidentType.BIKE, null);

        verify(mockIIncidentDAO, timeout(1000)).insertarIncidencia(any(Incident.class));
        verify(mockStationDetailsView, timeout(1000)).showDatabaseError();
        verify(mockStationDetailsView, never()).showEmptyIssueError();
    }

}