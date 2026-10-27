package es.unican.bicis.activities.details;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.unican.bicis.common.LocationUtils;
import es.unican.bicis.common.Utils;
import es.unican.bicis.model.Network;
import es.unican.bicis.model.Station;

public class RedDetailsPresenterInitTest {

    private RedDetailsPresenter presenter;
    private IRedDetailsContract.View view;
    private Context context;
    private Network network;

    private MockedStatic<Utils> mockedUtils;
    private MockedStatic<LocationUtils> mockedLocationUtils;

    @Before
    public void setUp() {
        // Mocks de las dependencias principales
        view = mock(IRedDetailsContract.View.class);
        context = mock(Context.class);
        network = mock(Network.class);

        when(view.getContext()).thenReturn(context);

        // Inicialización de mocks estáticos
        mockedUtils = mockStatic(Utils.class);
        mockedLocationUtils = mockStatic(LocationUtils.class);

        // ACTUALIZADO: Anulamos la llamada real a updateLocation aceptando el nuevo parámetro Runnable
        mockedLocationUtils.when(() -> LocationUtils.updateLocation(any())).thenAnswer(invocation -> {
            // Ejecutamos el runnable (callback) si se pasa alguno, tal y como haría el LocationUtils real
            Runnable callback = invocation.getArgument(0);
            if (callback != null) {
                callback.run();
            }
            return null;
        });

        // Instanciamos el presenter y lo inicializamos
        presenter = new RedDetailsPresenter();

        // Evitamos que init() llame a load() y falle por falta de repositorio en estas pruebas
        mockedUtils.when(() -> Utils.isNetworkAvailable(context)).thenReturn(false);
        presenter.init(view, network);
    }

    @After
    public void tearDown() {
        // Es obligatorio cerrar los mocks estáticos al finalizar cada prueba
        if (mockedUtils != null) mockedUtils.close();
        if (mockedLocationUtils != null) mockedLocationUtils.close();
    }

    /**
     * PU-01 (Rango superior seleccionado)
     * Distancia: 1Km, Filtro: MORE_THAN
     * Comprueba: Lista ordenada y filtrada.
     */
    @Test
    public void testPU01_RangoSuperiorSeleccionado() {
        mockedUtils.when(() -> Utils.isLocationEnabled(context)).thenReturn(true);

        Station stCerca = crearEstacionMock("Estacion 1", 10.0, 10.0);
        Station stLejos1 = crearEstacionMock("Estacion 2", 20.0, 20.0);
        Station stLejos2 = crearEstacionMock("Estacion 3", 30.0, 30.0);

        when(network.getStations()).thenReturn(Arrays.asList(stLejos2, stCerca, stLejos1));

        mockedLocationUtils.when(() -> LocationUtils.calcularDistanciaA(10.0, 10.0)).thenReturn(0.5f); // Descarta (< 1)
        mockedLocationUtils.when(() -> LocationUtils.calcularDistanciaA(20.0, 20.0)).thenReturn(2.5f); // Guarda
        mockedLocationUtils.when(() -> LocationUtils.calcularDistanciaA(30.0, 30.0)).thenReturn(1.5f); // Guarda

        presenter.onFilterClicked("1", DistanceFilter.MORE_THAN);

        ArgumentCaptor<List<Station>> captor = ArgumentCaptor.forClass(List.class);
        verify(view).showStations(captor.capture());

        List<Station> filtradas = captor.getValue();
        assertEquals(2, filtradas.size());
        assertEquals("Estacion 3", filtradas.get(0).getName()); // Ordenada por distancia (1.5)
        assertEquals("Estacion 2", filtradas.get(1).getName()); // Ordenada por distancia (2.5)
        verify(view, never()).showNoStationsInRangeError();
    }

    /**
     * PU-02 (Rango inferior seleccionado)
     * Distancia: 5Km, Filtro: LESS_THAN
     * Comprueba: Lista ordenada y filtrada.
     */
    @Test
    public void testPU02_RangoInferiorSeleccionado() {
        mockedUtils.when(() -> Utils.isLocationEnabled(context)).thenReturn(true);

        Station stCerca = crearEstacionMock("Estacion A", 40.0, 40.0);
        Station stMedia = crearEstacionMock("Estacion B", 50.0, 50.0);
        Station stLejos = crearEstacionMock("Estacion C", 60.0, 60.0);

        when(network.getStations()).thenReturn(Arrays.asList(stMedia, stLejos, stCerca));

        mockedLocationUtils.when(() -> LocationUtils.calcularDistanciaA(40.0, 40.0)).thenReturn(2.0f); // Guarda
        mockedLocationUtils.when(() -> LocationUtils.calcularDistanciaA(50.0, 50.0)).thenReturn(4.5f); // Guarda
        mockedLocationUtils.when(() -> LocationUtils.calcularDistanciaA(60.0, 60.0)).thenReturn(6.0f); // Descarta (> 5)

        presenter.onFilterClicked("5", DistanceFilter.LESS_THAN);

        ArgumentCaptor<List<Station>> captor = ArgumentCaptor.forClass(List.class);
        verify(view).showStations(captor.capture());

        List<Station> filtradas = captor.getValue();
        assertEquals(2, filtradas.size());
        assertEquals("Estacion A", filtradas.get(0).getName()); // Distancia menor (2.0)
        assertEquals("Estacion B", filtradas.get(1).getName()); // Distancia mayor (4.5)
    }

    /**
     * PU-03 (Distancia invalida)
     * Distancia: -10Km, Filtro: MORE_THAN
     * Comprueba: Interrupción de la ejecución (return directo en código).
     */
    @Test
    public void testPU03_DistanciaInvalida() {
        presenter.onFilterClicked("-10", DistanceFilter.MORE_THAN);

        verify(view, never()).showStations(any());
        verify(view, never()).showLocationError();

        presenter.onFilterClicked("5.5", DistanceFilter.MORE_THAN);
        verify(view, never()).showStations(any());
    }

    /**
     * PU-04 (Ubicación desactivada)
     * Distancia: 20Km, Filtro: MORE_THAN
     * Comprueba: Mensaje de error de ubicación.
     */
    @Test
    public void testPU04_UbicacionDesactivada() {
        mockedUtils.when(() -> Utils.isLocationEnabled(context)).thenReturn(false);

        presenter.onFilterClicked("20", DistanceFilter.MORE_THAN);

        verify(view).showLocationError();
        verify(view, never()).showStations(any());
    }

    /**
     * PU-05 (Sin estaciones en rango)
     * Distancia: 1Km, Filtro: LESS_THAN
     * Comprueba: Mensaje de error por falta de estaciones en el rango.
     */
    @Test
    public void testPU05_SinEstacionesEnRango() {
        mockedUtils.when(() -> Utils.isLocationEnabled(context)).thenReturn(true);

        Station stLejos = crearEstacionMock("Estacion Lejana", 10.0, 10.0);
        when(network.getStations()).thenReturn(Collections.singletonList(stLejos));

        mockedLocationUtils.when(() -> LocationUtils.calcularDistanciaA(anyDouble(), anyDouble())).thenReturn(5.0f);

        presenter.onFilterClicked("1", DistanceFilter.LESS_THAN);

        verify(view).showNoStationsInRangeError();

        ArgumentCaptor<List<Station>> captor = ArgumentCaptor.forClass(List.class);
        verify(view).showStations(captor.capture());
        assertTrue(captor.getValue().isEmpty());
    }

    /**
     * Método auxiliar para instanciar rápidamente estaciones falsas en las pruebas
     */
    private Station crearEstacionMock(String nombre, double lat, double lon) {
        Station st = mock(Station.class);
        when(st.getName()).thenReturn(nombre);
        when(st.getLatitude()).thenReturn(lat);
        when(st.getLongitude()).thenReturn(lon);
        return st;
    }
}