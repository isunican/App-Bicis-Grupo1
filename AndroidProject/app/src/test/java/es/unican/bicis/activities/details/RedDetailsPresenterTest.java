package es.unican.bicis.activities.details;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.unican.bicis.common.LocationUtils;
import es.unican.bicis.common.Utils;
import es.unican.bicis.model.Network;
import es.unican.bicis.model.Station;
import es.unican.bicis.repository.INetworksRepository;
import es.unican.bicis.repository.NetworkDetailsCallback;

@RunWith(MockitoJUnitRunner.Silent.class)
public class RedDetailsPresenterTest {

    @Mock
    private IRedDetailsContract.View mockView;

    @Mock
    private INetworksRepository mockRepository;

    @Mock
    private Network mockNetwork;

    @Mock
    private Context mockContext;

    @InjectMocks
    private RedDetailsPresenter presenter;

    private MockedStatic<Utils> mockedUtils;
    private MockedStatic<LocationUtils> mockedLocationUtils;

    private static final String NETWORK_ID = "tuebici";

    @Before
    public void setUp() {
        // MockitoJUnitRunner inicializa automáticamente los mocks marcados con @Mock e @InjectMocks
        when(mockView.getContext()).thenReturn(mockContext);
        when(mockView.getNetworksRepository()).thenReturn(mockRepository);
        when(mockNetwork.getId()).thenReturn(NETWORK_ID);
    }

    @After
    public void tearDown() {
        if (mockedUtils != null) {
            mockedUtils.close();
            mockedUtils = null;
        }
        if (mockedLocationUtils != null) {
            mockedLocationUtils.close();
            mockedLocationUtils = null;
        }
    }

    // Pruebas de carga de red y estaciones (feature/532226-VerEstacionesDeRed)

    @Test
    public void testLoadSuccessWithStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(new Station());
        stations.add(new Station());
        when(mockNetwork.getStations()).thenReturn(stations);

        doAnswer(invocation -> {
            NetworkDetailsCallback callback = invocation.getArgument(0);
            callback.onSuccess(mockNetwork);
            return null;
        }).when(mockRepository).requestNetwork(any(NetworkDetailsCallback.class), eq(NETWORK_ID));

        presenter.load();

        verify(mockView).showStations(stations);
        verify(mockView).showLoadCorrect(2);
        verify(mockView, never()).showNoStations();
        verify(mockView, never()).showLoadError();
    }

    @Test
    public void testLoadSuccessNoStations() {
        List<Station> emptyStations = Collections.emptyList();
        when(mockNetwork.getStations()).thenReturn(emptyStations);

        doAnswer(invocation -> {
            NetworkDetailsCallback callback = invocation.getArgument(0);
            callback.onSuccess(mockNetwork);
            return null;
        }).when(mockRepository).requestNetwork(any(NetworkDetailsCallback.class), eq(NETWORK_ID));

        presenter.load();

        verify(mockView).showNoStations();
        verify(mockView, never()).showStations(anyList());
        verify(mockView, never()).showLoadCorrect(anyInt());
        verify(mockView, never()).showLoadError();
    }

    @Test
    public void testLoadError() {
        doAnswer(invocation -> {
            NetworkDetailsCallback callback = invocation.getArgument(0);
            callback.onFailure(new Throwable());
            return null;
        }).when(mockRepository).requestNetwork(any(NetworkDetailsCallback.class), eq(NETWORK_ID));

        presenter.load();

        verify(mockView).showLoadError();
        verify(mockView, never()).showStations(anyList());
        verify(mockView, never()).showLoadCorrect(anyInt());
        verify(mockView, never()).showNoStations();
    }

    // Pruebas de filtrado por distancia y ubicación (HEAD)

    private void prepararEntornoFiltrado() {
        mockedUtils = mockStatic(Utils.class);
        mockedLocationUtils = mockStatic(LocationUtils.class);

        mockedLocationUtils.when(() -> LocationUtils.updateLocation(any())).thenAnswer(invocation -> {
            Runnable callback = invocation.getArgument(0);
            if (callback != null) {
                callback.run();
            }
            return null;
        });

        mockedUtils.when(() -> Utils.isNetworkAvailable(mockContext)).thenReturn(false);
        presenter.init(mockView, mockNetwork);
    }

    @Test
    public void testPU01_RangoSuperiorSeleccionado() {
        prepararEntornoFiltrado();
        mockedUtils.when(() -> Utils.isLocationEnabled(mockContext)).thenReturn(true);

        Station stCerca = crearEstacionMock("Estacion 1", 10.0, 10.0);
        Station stLejos1 = crearEstacionMock("Estacion 2", 20.0, 20.0);
        Station stLejos2 = crearEstacionMock("Estacion 3", 30.0, 30.0);

        when(mockNetwork.getStations()).thenReturn(Arrays.asList(stLejos2, stCerca, stLejos1));

        mockedLocationUtils.when(() -> LocationUtils.calcularDistanciaA(10.0, 10.0)).thenReturn(0.5f);
        mockedLocationUtils.when(() -> LocationUtils.calcularDistanciaA(20.0, 20.0)).thenReturn(2.5f);
        mockedLocationUtils.when(() -> LocationUtils.calcularDistanciaA(30.0, 30.0)).thenReturn(1.5f);

        presenter.onFilterClicked("1", DistanceFilter.MORE_THAN);

        ArgumentCaptor<List<Station>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).showStations(captor.capture());

        List<Station> filtradas = captor.getValue();
        assertEquals(2, filtradas.size());
        assertEquals("Estacion 3", filtradas.get(0).getName());
        assertEquals("Estacion 2", filtradas.get(1).getName());
        verify(mockView, never()).showNoStationsInRangeError();
    }

    @Test
    public void testPU02_RangoInferiorSeleccionado() {
        prepararEntornoFiltrado();
        mockedUtils.when(() -> Utils.isLocationEnabled(mockContext)).thenReturn(true);

        Station stCerca = crearEstacionMock("Estacion A", 40.0, 40.0);
        Station stMedia = crearEstacionMock("Estacion B", 50.0, 50.0);
        Station stLejos = crearEstacionMock("Estacion C", 60.0, 60.0);

        when(mockNetwork.getStations()).thenReturn(Arrays.asList(stMedia, stLejos, stCerca));

        mockedLocationUtils.when(() -> LocationUtils.calcularDistanciaA(40.0, 40.0)).thenReturn(2.0f);
        mockedLocationUtils.when(() -> LocationUtils.calcularDistanciaA(50.0, 50.0)).thenReturn(4.5f);
        mockedLocationUtils.when(() -> LocationUtils.calcularDistanciaA(60.0, 60.0)).thenReturn(6.0f);

        presenter.onFilterClicked("5", DistanceFilter.LESS_THAN);

        ArgumentCaptor<List<Station>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).showStations(captor.capture());

        List<Station> filtradas = captor.getValue();
        assertEquals(2, filtradas.size());
        assertEquals("Estacion A", filtradas.get(0).getName());
        assertEquals("Estacion B", filtradas.get(1).getName());
    }

    @Test
    public void testPU03_DistanciaInvalida() {
        prepararEntornoFiltrado();

        presenter.onFilterClicked("-10", DistanceFilter.MORE_THAN);

        verify(mockView, never()).showStations(any());
        verify(mockView, never()).showLocationError();

        presenter.onFilterClicked("5.5", DistanceFilter.MORE_THAN);
        verify(mockView, never()).showStations(any());
    }

    @Test
    public void testPU04_UbicacionDesactivada() {
        prepararEntornoFiltrado();
        mockedUtils.when(() -> Utils.isLocationEnabled(mockContext)).thenReturn(false);

        presenter.onFilterClicked("20", DistanceFilter.MORE_THAN);

        verify(mockView).showLocationError();
        verify(mockView, never()).showStations(any());
    }

    @Test
    public void testPU05_SinEstacionesEnRango() {
        prepararEntornoFiltrado();
        mockedUtils.when(() -> Utils.isLocationEnabled(mockContext)).thenReturn(true);

        Station stLejos = crearEstacionMock("Estacion Lejana", 10.0, 10.0);
        when(mockNetwork.getStations()).thenReturn(Collections.singletonList(stLejos));

        mockedLocationUtils.when(() -> LocationUtils.calcularDistanciaA(anyDouble(), anyDouble())).thenReturn(5.0f);

        presenter.onFilterClicked("1", DistanceFilter.LESS_THAN);

        verify(mockView).showNoStationsInRangeError();

        ArgumentCaptor<List<Station>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockView).showStations(captor.capture());
        assertTrue(captor.getValue().isEmpty());
    }

    private Station crearEstacionMock(String nombre, double lat, double lon) {
        Station st = mock(Station.class);
        when(st.getName()).thenReturn(nombre);
        when(st.getLatitude()).thenReturn(lat);
        when(st.getLongitude()).thenReturn(lon);
        return st;
    }

    // Test metodo Init

    @Test
    public void UT_01_init_ConRedYUbicacion_IniciaYCargaEstaciones() {
        // Arrange
        mockedUtils = mockStatic(Utils.class);
        Network network = new Network();
        network.setId("bicing-barcelona");

        mockedUtils.when(() -> Utils.isNetworkAvailable(mockContext)).thenReturn(true);
        mockedUtils.when(() -> Utils.isLocationEnabled(mockContext)).thenReturn(true);

        // Act
        presenter.init(mockView, network);

        // Assert
        verify(mockView).init();
        verify(mockView, never()).showLocationError();
        verify(mockView, never()).showRedError();

        // Verificamos que se ejecutó load() de forma interna comprobando
        // si se invocó la petición al repositorio
        verify(mockView).getNetworksRepository();
        verify(mockRepository).requestNetwork(any(NetworkDetailsCallback.class), eq("bicing-barcelona"));
    }

    @Test
    public void UT_02_init_ConRedSinUbicacion_MuestraAvisoUbicacionYContinua() {
        // Arrange
        mockedUtils = mockStatic(Utils.class);
        Network network = new Network();
        network.setId("bicimad");

        mockedUtils.when(() -> Utils.isNetworkAvailable(mockContext)).thenReturn(true);
        mockedUtils.when(() -> Utils.isLocationEnabled(mockContext)).thenReturn(false); // Ubicación desactivada

        // Act
        presenter.init(mockView, network);

        // Assert
        verify(mockView).init();
        verify(mockView).showLocationError(); // Debe notificar a la vista del error de GPS
        verify(mockView, never()).showRedError();

        // Aunque la ubicación falle, el código original manda cargar la red igualmente
        verify(mockRepository).requestNetwork(any(NetworkDetailsCallback.class), eq("bicimad"));
    }

    @Test
    public void UT_03_init_SinRed_MuestraErrorDeRedYDetieneCarga() {
        // Arrange
        mockedUtils = mockStatic(Utils.class);
        Network network = new Network();

        mockedUtils.when(() -> Utils.isNetworkAvailable(mockContext)).thenReturn(false); // Sin internet

        // Act
        presenter.init(mockView, network);

        // Assert
        verify(mockView).init();
        verify(mockView).showRedError(); // Debe notificar error de conexión
        verify(mockView, never()).showLocationError(); // El if anidado no llega a ejecutarse

        // Comprobamos de manera estricta que load() NUNCA se ha ejecutado
        verify(mockView, never()).getNetworksRepository();
        verify(mockRepository, never()).requestNetwork(any(), anyString());
    }
}