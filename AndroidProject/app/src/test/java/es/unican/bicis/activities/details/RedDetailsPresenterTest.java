package es.unican.bicis.activities.details;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import es.unican.bicis.common.Utils;
import es.unican.bicis.model.Network;
import es.unican.bicis.repository.INetworksRepository;
import es.unican.bicis.repository.NetworkDetailsCallback;

public class RedDetailsPresenterTest {

    @Mock
    private IRedDetailsContract.View mockView;

    @Mock
    private Context mockContext;

    @Mock
    private INetworksRepository mockRepository;

    private RedDetailsPresenter presenter;
    private MockedStatic<Utils> utilsMockedStatic;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        presenter = new RedDetailsPresenter();

        // Configuramos la vista para que devuelva nuestro mockContext y mockRepository
        // Esto es vital porque load() lo requiere para pedir los datos
        when(mockView.getContext()).thenReturn(mockContext);
        when(mockView.getNetworksRepository()).thenReturn(mockRepository);

        // Inicializamos el interceptor de métodos estáticos para Utils
        utilsMockedStatic = mockStatic(Utils.class);
    }

    @After
    public void tearDown() {
        // Es obligatorio cerrar el MockedStatic después de cada test
        // para que no interfiera con otras pruebas unitarias
        if (utilsMockedStatic != null) {
            utilsMockedStatic.close();
        }
    }

    @Test
    public void UT_01_init_ConRedYUbicacion_IniciaYCargaEstaciones() {
        // Arrange
        Network network = new Network();
        network.setId("bicing-barcelona");

        utilsMockedStatic.when(() -> Utils.isNetworkAvailable(mockContext)).thenReturn(true);
        utilsMockedStatic.when(() -> Utils.isLocationEnabled(mockContext)).thenReturn(true);

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
        Network network = new Network();
        network.setId("bicimad");

        utilsMockedStatic.when(() -> Utils.isNetworkAvailable(mockContext)).thenReturn(true);
        utilsMockedStatic.when(() -> Utils.isLocationEnabled(mockContext)).thenReturn(false); // Ubicación desactivada

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
        Network network = new Network();

        utilsMockedStatic.when(() -> Utils.isNetworkAvailable(mockContext)).thenReturn(false); // Sin internet

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