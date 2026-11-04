package es.unican.bicis.activities.details.station;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

import static es.unican.bicis.utils.Matchers.withSpinnerSize;

import android.Manifest;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import es.unican.bicis.R;
import es.unican.bicis.activities.main.MainView;
import es.unican.bicis.databaseAccess.AppDatabase;
import es.unican.bicis.model.Incident;
import es.unican.bicis.model.IncidentType;
import es.unican.bicis.utils.DatabaseTestUtils;

@HiltAndroidTest
public class CrearIncidenciaPredefinidaExitoUITest {

    private static final String STATION_ID = "0a7638aa73949d3c8742f13f56cedff0";

    private AppDatabase db;

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule(order = 2)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    @Before
    public void setUp() {
        db = DatabaseTestUtils.setInMemoryDatabase();
    }

    @After
    public void tearDown() {
        DatabaseTestUtils.clearDatabase();
    }

    @Test
    public void crearIncidenciaPredefinidaExitoTest() {

        onView(withText("TUeBICI")).perform(click());
        onView(withText("Perinés")).perform(click());

        onView(withId(R.id.spIncidencias)).check(matches(withSpinnerSize(4)));

        onView(withId(R.id.btBotonIncidencia)).check(matches(isDisplayed()));
        onView(withId(R.id.btBotonIncidencia)).perform(click());

        Context context = ApplicationProvider.getApplicationContext();
        String[] expectedSpOptions = context.getResources().getStringArray(R.array.incidencias_options);

        for (String option : expectedSpOptions) {
            onData(allOf(is(instanceOf(String.class)), is(option))).check(matches(isDisplayed()));
        }

        onData(allOf(is(instanceOf(String.class)), is(expectedSpOptions[2]))).perform(click());

        List<Incident> incidents = db.incidenciaDao().obtenerIncidenciasPorEstacion(STATION_ID);
        assertEquals(1, incidents.size());

        Incident incident = incidents.get(0);
        assertEquals(STATION_ID, incident.getIdEstacion());
        assertEquals(IncidentType.BIKE, incident.getTypeId());
        assertEquals(IncidentType.BIKE.getDescripcion(), incident.getType());
        assertEquals(1, incident.getAmount());
    }
}