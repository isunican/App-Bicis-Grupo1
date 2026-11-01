package es.unican.bicis.activities.main;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.os.Handler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

import es.unican.bicis.common.Utils;
import es.unican.bicis.databaseAccess.IFavouriteDAO;
import es.unican.bicis.model.Favourite;
import es.unican.bicis.model.Network;
import es.unican.bicis.repository.INetworksRepository;
import es.unican.bicis.repository.NetworksCallback;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MainPresenterTest {
    @Mock
    private IMainContract.View mockView;
    @Mock
    private IFavouriteDAO mockDAO;
    @Mock
    private INetworksRepository mockNetworkRepo;
    @Mock
    private Context mockContext;
    @Mock
    private ExecutorService executor;
    @Mock
    private Handler mainHandler;
    @InjectMocks
    private MainPresenter presenter;
    private MockedStatic<Utils> mockUtils;

    @Before
    public void setUp() {
        when(mockView.getContext()).thenReturn(mockContext);
        when(mockView.getNetworksRepository()).thenReturn(mockNetworkRepo);
        when(mockView.getFavouriteDAO()).thenReturn(mockDAO);
        // Ejecutar el executor de forma síncrona
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executor).execute(any(Runnable.class));

        // Ejecutar el handler de la UI de forma síncrona
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return true;
        }).when(mainHandler).post(any(Runnable.class));
    }

    @After
    public void tearDown() {
        if (mockUtils != null) {
            mockUtils.close();
            mockUtils = null;
        }
    }

    /**
     * Metodo auxiliar para inicializar el presenter con datos
     * y evitar que lastLoadedNetworks sea null cuando se requiera
     * @param mockRedes lista de redes mockeada
     */
    private void cargarRedesSimulada(List<Network> mockRedes) {
        mockUtils = mockStatic(Utils.class);
        mockUtils.when(() -> Utils.isNetworkAvailable(mockContext)).thenReturn(true);

        doAnswer(invocation -> {
            NetworksCallback callback = invocation.getArgument(0);
            callback.onSuccess(mockRedes);
            return null;
        }).when(mockNetworkRepo).requestNetworks(any(NetworksCallback.class));

        presenter.init(mockView);
    }

    @Test
    public void testPU01_RedValida_DaoValida_RedesCargadas() {
        Network biciMad = mock(Network.class);
        Network bicing = mock(Network.class);
        Network tueBici = mock(Network.class);
        Favourite mockFavorito = mock(Favourite.class);
        when(mockFavorito.getNetworkId()).thenReturn("bicimad");
        when(biciMad.getId()).thenReturn("bicimad");
        when(biciMad.getName()).thenReturn("BiciMAD");

        when(bicing.getId()).thenReturn("bicing");
        when(bicing.getName()).thenReturn("Bicing");

        when(tueBici.getId()).thenReturn("tuebici");
        when(tueBici.getName()).thenReturn("TUeBICI");

        //carga de redes simulada
        cargarRedesSimulada(Arrays.asList(bicing, biciMad, tueBici));

        //simular que bicimad se almacena en la dao como favorito
        when(mockDAO.obtenerNetworkIdsFavoritos()).thenReturn(Arrays.asList("bicimad"));

        presenter.onFavoriteClicked(biciMad);

        //se invoca el insertar de la dao
        verify(mockDAO).insertarFavorito(any(Favourite.class));
        //se invoca el refresco de la lista para mostrar favoritos
        verify(mockView).showMarkedAsFavorite(biciMad);

        //para capturar la lista de redes ordenada con el favorito en la segunda invocacion de showNetworks
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);

        //se verifica que showNetworks se llama 2 veces
        //una en la carga inicial simulada y otra en onFavoriteClicked
        verify(mockView, times(2)).showNetworks(captor.capture());

        List<Network> favoritoOrdenado = captor.getValue();

        //verificar las posiciones de las redes en la lista
        assertEquals("bicimad", favoritoOrdenado.get(0).getId());
        assertEquals("bicing", favoritoOrdenado.get(1).getId());
        assertEquals("tuebici", favoritoOrdenado.get(2).getId());
    }

    @Test
    public void testPU02_RedNula_IdNulo() {
        Network redSinId = mock(Network.class);
        when(redSinId.getId()).thenReturn(null);

        //se llama con null y con id null
        presenter.onFavoriteClicked(null);
        presenter.onFavoriteClicked(redSinId);

        //se verifica que nunca se llama a getFavouriteDao porque no pasa del if
        verify(mockView, never()).getFavouriteDAO();
    }

    @Test
    public void testPU03_DaoNoDisponible() {
        Network redValida = mock(Network.class);
        when(redValida.getId()).thenReturn("bicimad");

        //forzar a que la dao sea null
        when(mockView.getFavouriteDAO()).thenReturn(null);

        presenter.onFavoriteClicked(redValida);

        //se verifica que nunca se llama a showmarkedfavorite porque no pasa del if
        verify(mockView, never()).showMarkedAsFavorite(any());
    }

    @Test
    public void testPU04_ListaDeRedesNula() {
        Network redValida = mock(Network.class);
        when(redValida.getId()).thenReturn("bicimad");

        //no se carga la lista de redes ni se simula al no llamar al init debe ser nula
        presenter.onFavoriteClicked(redValida);

        //se verifica que se llama a insertar favorito y a showmarkedasfavorite al cumplir el resto de condiciones
        verify(mockDAO).insertarFavorito(any(Favourite.class));
        verify(mockView).showMarkedAsFavorite(redValida);

        //se verifica que no se intenta ordenar ni refrescar la lista al no estar disponible networksToSort
        verify(mockView, never()).showNetworks(anyList());
    }
}
