package es.unican.bicis.activities.details.red;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertNotNull;

import static es.unican.bicis.utils.Matchers.withListSize;

import android.Manifest;
import android.content.Context;

import androidx.test.espresso.DataInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import es.unican.bicis.R;
import es.unican.bicis.activities.main.MainView;
import es.unican.bicis.utils.MockLocationManager;

@HiltAndroidTest
public class ModificarFiltradoPorDistanciaNoExistenEstacionesEnRangoFiltradoUITest {

    // Coordinates of the TUeBICI network in Santander (see res/raw/networks.json). */
    private static final double SANTANDER_LATITUDE = 43.471281;
    private static final double SANTANDER_LONGITUDE = -3.800898;

    // Maximum time to wait for a location update, in milliseconds. */
    private static final long LOCATION_TIMEOUT_MS = 5_000;

    @Rule(order = 0) // the Hilt rule must execute first
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);
    private MockLocationManager mockLocationManager;

    @Rule(order = 2)
    public ActivityScenarioRule<MainView> activityRule =
            new ActivityScenarioRule<>(MainView.class);

    @Before
    public void setUp() throws InterruptedException {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mockLocationManager = new MockLocationManager(context);
        mockLocationManager.setUp();
        mockLocationManager.setLocation(SANTANDER_LATITUDE, SANTANDER_LONGITUDE);
        assertNotNull("The application did not receive any GPS update",
                mockLocationManager.awaitMockedLocation(LOCATION_TIMEOUT_MS));
    }

    @Test
    public void testPrueba03_NoExistenEstacionesEnRangoFiltrado() {
        // Abrimos los detalles de la red TUeBICI desde la pantalla principal
        onView(withText("TUeBICI")).perform(click());

        //El técnico introduce 2Km y 10Km en la aplicación
        onView(withId(R.id.etMinKmFilter)).perform(clearText(), typeText("100"), closeSoftKeyboard());

        //El técnico selecciona el botón de filtrar
        onView(withId(R.id.FilterKmRange)).perform(click());

        //Se verifica que no se muestran estaciones
        onView(withId(R.id.lvStations)).check(matches(withListSize(0)));

    }
}