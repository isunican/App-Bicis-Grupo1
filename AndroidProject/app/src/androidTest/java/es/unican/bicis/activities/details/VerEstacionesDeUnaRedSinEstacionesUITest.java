package es.unican.bicis.activities.details;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import es.unican.bicis.R;
import es.unican.bicis.activities.main.MainView;
import es.unican.bicis.utils.MockLocationManager;

@HiltAndroidTest
public class VerEstacionesDeUnaRedSinEstacionesUITest {
    private static final double SANTANDER_LATITUDE = 43.471167;
    private static final double SANTANDER_LONGITUDE = -3.801385;

    private static final long LOCATION_TIMEOUT_MS = 5_000;

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    );

    private MockLocationManager mockLocationManager;

    @Rule(order = 2)
    public ActivityScenarioRule<MainView> activityRule =
            new ActivityScenarioRule<>(MainView.class);

    View decorView;
    @Before
    public void setUp() throws InterruptedException {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mockLocationManager = new MockLocationManager(context);
        mockLocationManager.setUp();
        activityRule.getScenario().onActivity(activity -> decorView = activity.getWindow().getDecorView());
        mockLocationManager.setLocation(SANTANDER_LATITUDE, SANTANDER_LONGITUDE);
        assertNotNull("The application did not receive any GPS update",
                mockLocationManager.awaitMockedLocation(LOCATION_TIMEOUT_MS));
    }

    @After
    public void tearDown() {
        mockLocationManager.tearDown();
    }

    @Test
    public void verEstacionesDeUnaRedRedSinEstacionesTest() throws InterruptedException {

        //elegimos TUeBICI

        /* El thread sleep era necesario para comprobar los toast
        try {
            // Esperamos 10 segundos para que el toast anterior se cierre
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        */

        onData(anything()).inAdapterView(withId(R.id.lvNetworks)).atPosition(3).perform(click());

        onView(allOf(withId(R.id.tvName), withParent(withId(R.id.main)))).check(matches(withText("BiciSinEstaciones")));
        onView(withId(R.id.tvCity)).check(matches(withText("Santander (ES)")));
        onView(withId(R.id.tvEbikes)).check(matches(withText("No")));
        onView(withId(R.id.tvCompanysNum)).check(matches(withText("1")));


        onView(withId(R.id.lvStations)).check((view, noViewFoundException) -> {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }
            ListView listView = (ListView) view;
            int count = (listView.getAdapter() != null) ? listView.getAdapter().getCount() : 0;
            assertEquals(0, count);
        });

        /* Aqui comprobaba el toast de que no se han cargado estaciones
        onView(withText(R.string.noStations)).inRoot(RootMatchers.withDecorView(not(decorView))).check(matches(isDisplayed()));
        */
    }

}
