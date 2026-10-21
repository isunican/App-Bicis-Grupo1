package es.unican.bicis.activities.details;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;

import android.Manifest;
import android.content.Context;
import android.view.View;
import android.widget.ListView;

import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import es.unican.bicis.R;
import es.unican.bicis.activities.main.MainView;
import es.unican.bicis.utils.MockLocationManager;

@HiltAndroidTest
public class FiltrarPorDistanciaSinEstacionesEnRango {

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
        activityRule.getScenario().onActivity(
                activity -> decorView = activity.getWindow().getDecorView()
        );
    }

    /*
    @After
    public void tearDown() {
        mockLocationManager.tearDown();
    }
    */

    /**
     * Matcher personalizado para comprobar el número de estaciones mostradas en el ListView.
     */
    public static Matcher<View> withListSize(final int expectedSize) {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(final View view) {
                return ((ListView) view).getCount() == expectedSize;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("ListView debería tener " + expectedSize + " elementos");
            }
        };
    }

    private View decorView;



    @Test
    public void testPrueba01_SinEstacionesEnRango() {
        // Abrimos los detalles de la red TUeBICI desde la pantalla principal
        onView(withText("TUeBICI")).perform(click());

        // 1 y 2. El técnico introduce 5Km en la aplicación
        onView(withId(R.id.etKmFilter)).perform(clearText(), typeText("0"), closeSoftKeyboard());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 4 y 5. El técnico selecciona la opción de ver a una distancia superior
        onView(withId(R.id.btnFilterLessThanKm)).perform(click());

        // 8. Se verifica que se muestran todas las estaciones que cumplen el requisito
        // (Ajusta el número y los textos inferiores según la ubicación fija de tu emulador/LocationUtils)
        onView(withId(R.id.lvStations)).check(matches(withListSize(0)));
        onView(withText(R.string.noStationsInRange)).inRoot(RootMatchers.withDecorView(not(decorView))).check(matches(isDisplayed()));
    }
}