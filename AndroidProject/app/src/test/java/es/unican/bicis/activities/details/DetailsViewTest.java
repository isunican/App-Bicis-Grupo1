package es.unican.bicis.activities.details;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.parceler.Parcels;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import es.unican.bicis.R;
import es.unican.bicis.model.Location;
import es.unican.bicis.model.Network;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class DetailsViewTest {

    /**
     * metodo auxiliar para crear un intent que permita introducir el mock de la clase network
     * @return intent para introducir la clase mock
     */
    private Intent createFakeIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DetailsView.class);
        intent.putExtra(DetailsView.INTENT_NETWORK, new Bundle());
        return intent;
    }


    @Test
    public void testPU01RedValidaConDatos(){
        //preparar mocks
        Network mockNetwork = mock(Network.class);
        Location mockLocation = mock (Location.class);
        when(mockNetwork.getName()).thenReturn("Bicing");
        when(mockNetwork.isEbikes()).thenReturn(true);
        when(mockNetwork.getCompany()).thenReturn(new String[]
                {"Barcelona de Serveis Municipals,S.A.(BSM)", "CESPA", "PBSC"});
        when(mockNetwork.getLocation()).thenReturn(mockLocation);

        when(mockLocation.getCity()).thenReturn("Barcelona");
        when(mockLocation.getCountry()).thenReturn("ES");

        //Interceptar el onCreate en el unwrap para que pase la red mockeada
        try (MockedStatic<Parcels> mockedParcels = Mockito.mockStatic(Parcels.class)) {
            mockedParcels.when(() -> Parcels.unwrap(any())).thenReturn(mockNetwork);

            try (ActivityScenario<DetailsView> scenario = ActivityScenario.launch(createFakeIntent())) {
                //comprobar interfaz
                onView(ViewMatchers.withId(R.id.tvName)).check(matches(withText("Bicing")));
                onView(withId(R.id.tvCity)).check(matches(withText("Barcelona (ES)")));
                onView(withId(R.id.tvEbikes)).check(matches(withText("true")));
                onView(withId(R.id.tvCompanysNum)).check(matches(withText("3")));
            }
        }

    }

    @Test
    public void testPU02RedSinInfoDetallada(){
        //preparar mocks
        Network mockNetwork = mock(Network.class);
        Location mockLocation = mock (Location.class);
        when(mockNetwork.getName()).thenReturn("TUeBICI");
        when(mockNetwork.isEbikes()).thenReturn(null);
        when(mockNetwork.getCompany()).thenReturn(null);
        when(mockNetwork.getLocation()).thenReturn(mockLocation);

        when(mockLocation.getCity()).thenReturn("Santander");
        when(mockLocation.getCountry()).thenReturn("ES");
        String noData = ApplicationProvider.getApplicationContext().getString(R.string.noData);
        String toastStr = ApplicationProvider.getApplicationContext().getString(R.string.noDetailInfo);
        //Interceptar el onCreate en el unwrap para que pase la red mockeada
        try (MockedStatic<Parcels> mockedParcels = Mockito.mockStatic(Parcels.class)) {
            mockedParcels.when(() -> Parcels.unwrap(any())).thenReturn(mockNetwork);

            try (ActivityScenario<DetailsView> scenario = ActivityScenario.launch(createFakeIntent())) {
                //comprobar la interfaz con los datos corretos
                onView(withId(R.id.tvName)).check(matches(withText("TUeBICI")));
                onView(withId(R.id.tvCity)).check(matches(withText("Santander (ES)")));
                onView(withId(R.id.tvEbikes)).check(matches(withText(noData)));
                onView(withId(R.id.tvCompanysNum)).check(matches(withText(noData)));

                //comprobar que se lanza el toast
                String lastToastMsg = ShadowToast.getTextOfLatestToast();
                assertEquals(toastStr, lastToastMsg);
            }
        }
    }

    @Test
    public void testPU03BBDDNoAccesible(){
        try (MockedStatic<Parcels> mockedParcels = Mockito.mockStatic(Parcels.class)) {
            mockedParcels.when(() -> Parcels.unwrap(any())).thenReturn(null);

            try (ActivityScenario<DetailsView> scenario = ActivityScenario.launch(createFakeIntent())) {
                //comprobar que al ser null, la UI no crashea y carga un string vacío
                onView(withId(R.id.tvName)).check(matches(withText("")));
                //comprobar que se lanza el toast de error de red
                String toastMsgExpected = ApplicationProvider.getApplicationContext().getString(R.string.loadError);
                String lastToastMsg = ShadowToast.getTextOfLatestToast();
                assertEquals(toastMsgExpected, lastToastMsg);
            }
        }
    }

    @Test
    public void testPU04DatosInconsistentes(){
        Network mockNetwork = mock(Network.class);

        when(mockNetwork.getName()).thenReturn("BiciMAD");
        when(mockNetwork.getCompany()).thenReturn(new String[]{"EMTMadrid"});
        when(mockNetwork.isEbikes()).thenReturn(null);
        String noData = ApplicationProvider.getApplicationContext().getString(R.string.noData);
        try (MockedStatic<Parcels> mockedParcels = Mockito.mockStatic(Parcels.class)) {
            mockedParcels.when(() -> Parcels.unwrap(any())).thenReturn(mockNetwork);

            try (ActivityScenario<DetailsView> scenario = ActivityScenario.launch(createFakeIntent())) {
                onView(withId(R.id.tvName)).check(matches(withText("BiciMAD")));

                //Verificamos que el dato inconsistente pone el texto por defecto
                onView(withId(R.id.tvEbikes)).check(matches(withText(noData)));
            }
        }
    }
}
