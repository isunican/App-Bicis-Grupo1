package es.unican.bicis.activities.details;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.unican.bicis.activities.details.IRedDetailsContract.View;
import es.unican.bicis.model.Network;
import es.unican.bicis.model.Station;
import es.unican.bicis.repository.INetworksRepository;
import es.unican.bicis.repository.NetworkDetailsCallback;

public class RedDetailsPresenterTest {

    @Mock
    private View mockView;

    @Mock
    private INetworksRepository mockRepository;

    @Mock
    private Network mockNetwork;

    @InjectMocks
    private RedDetailsPresenter sut;

    private static final String NETWORK_ID = "tuebici";

    @Before
    public void setUp() {
        sut = new RedDetailsPresenter();
        MockitoAnnotations.openMocks(this);

        when(mockView.getNetworksRepository()).thenReturn(mockRepository);
        when(mockNetwork.getId()).thenReturn(NETWORK_ID);
    }

    /**
     * PU-01: Red seleccionada con estaciones disponibles y éxito.
     */
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

        sut.load();

        verify(mockView).showStations(stations);
        verify(mockView).showLoadCorrect(2);
        verify(mockView, never()).showNoStations();
        verify(mockView, never()).showLoadError();
    }

    /**
     * PU-02: Red seleccionada sin estaciones.
     */
    @Test
    public void testLoadSuccessNoStations() {
        List<Station> emptyStations = Collections.emptyList();
        when(mockNetwork.getStations()).thenReturn(emptyStations);

        doAnswer(invocation -> {
            NetworkDetailsCallback callback = invocation.getArgument(0);
            callback.onSuccess(mockNetwork);
            return null;
        }).when(mockRepository).requestNetwork(any(NetworkDetailsCallback.class), eq(NETWORK_ID));

        sut.load();

        verify(mockView).showNoStations();
        verify(mockView, never()).showStations(anyList());
        verify(mockView, never()).showLoadCorrect(anyInt());
        verify(mockView, never()).showLoadError();
    }

    /**
     * PU-03: Error al recuperar las estaciones de la red seleccionada.
     */
    @Test
    public void testLoadError() {
        doAnswer(invocation -> {
            NetworkDetailsCallback callback = invocation.getArgument(0);
            callback.onFailure(new Throwable());
            return null;
        }).when(mockRepository).requestNetwork(any(NetworkDetailsCallback.class), eq(NETWORK_ID));

        sut.load();

        verify(mockView).showLoadError();
        verify(mockView, never()).showStations(anyList());
        verify(mockView, never()).showLoadCorrect(anyInt());
        verify(mockView, never()).showNoStations();
    }
}