package es.unican.bicis.activities.details.red;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
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