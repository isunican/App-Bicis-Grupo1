package es.unican.bicis.activities.details.station;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

import static es.unican.bicis.utils.Matchers.hasBackgroundColor;
import static es.unican.bicis.utils.Matchers.withListSize;

import android.Manifest;

import androidx.test.espresso.DataInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import es.unican.bicis.R;
import es.unican.bicis.activities.main.MainView;
import es.unican.bicis.databaseAccess.AppDatabase;
import es.unican.bicis.model.Incident;
import es.unican.bicis.model.IncidentType;
import es.unican.bicis.utils.DatabaseTestUtils;

@HiltAndroidTest
public class VerIncidenciasEstacionExitoVariasIncidencias01UITest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule(order = 2)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    @Before
    public void setUp() {
        // Initializes the app database with test data
        AppDatabase db = DatabaseTestUtils.setInMemoryDatabase();

        String idEstacion = "0a7638aa73949d3c8742f13f56cedff0"; // Station id of the test station

        /*
         * Inserts all the data into the database. The data entered into the database is as follows:
         * +----+------------------------------------+---------+-------------------------------------+
         * | id | idEstacion                         | typeId  | type                                |
         * +----+------------------------------------+---------+-------------------------------------+
         * | 1  | 0a7638aa73949d3c8742f13f56cedff0   | BIKE    | BiciRota                            |
         * | 2  | 0a7638aa73949d3c8742f13f56cedff0   | BIKE    | BiciRota                            |
         * | 3  | 0a7638aa73949d3c8742f13f56cedff0   | BIKE    | BiciRota                            |
         * | 4  | 0a7638aa73949d3c8742f13f56cedff0   | STATION | EstacionRota                        |
         * | 5  | 0a7638aa73949d3c8742f13f56cedff0   | OTHER   | Falta de material de mantenimiento  |
         * | 6  | 0a7638aa73949d3c8742f13f56cedff0   | STATION | EstacionRota                        |
         * +----+------------------------------------+---------+-------------------------------------+
         */
        db.incidenciaDao().insertarIncidencia(new Incident(idEstacion, IncidentType.BIKE, "BiciRota"));
        db.incidenciaDao().insertarIncidencia(new Incident(idEstacion, IncidentType.BIKE, "BiciRota"));
        db.incidenciaDao().insertarIncidencia(new Incident(idEstacion, IncidentType.BIKE, "BiciRota"));
        db.incidenciaDao().insertarIncidencia(new Incident(idEstacion, IncidentType.STATION, "EstacionRota"));
        db.incidenciaDao().insertarIncidencia(new Incident(idEstacion, IncidentType.OTHER, "Falta de material de mantenimiento"));
        db.incidenciaDao().insertarIncidencia(new Incident(idEstacion, IncidentType.STATION, "EstacionRota"));
    }

    @After
    public void tearDown() {
        DatabaseTestUtils.clearDatabase();
    }

    @Test
    public void testVerIncidenciasEstacionExitoVariasIncidencias01() {
        // Selects TUeBICI network from the main networks screen
        onView(withText("TUeBICI")).perform(click());

        // Selects Perinés station from the list of stations of the network
        onView(withText("Perinés")).perform(click());

        // Checks that the list of incidences contains 3 elements grouped (all 3 possible types)
        onView(withId(R.id.lvIncidences)).check(matches(withListSize(3)));

        //INFO: App fetches data in descendant order following the insertion order.

        // Verifies that the list of incidences contains an entry with a custom incident
        // and has the correct background color
        DataInteraction itemOtro = onData(anything()).inAdapterView(withId(R.id.lvIncidences)).atPosition(0);
        itemOtro.check(matches(hasBackgroundColor(R.color.colorOtros)));
        itemOtro.onChildView(withId(R.id.tvType)).check(matches(withText("Falta de material de mantenimiento")));
        itemOtro.onChildView(withId(R.id.tvAmount)).check(matches(withText("1")));

        // Verifies that the list of incidences contains an entry with 3 BiciRota incidents
        // and has the correct background color
        DataInteraction itemBici = onData(anything()).inAdapterView(withId(R.id.lvIncidences)).atPosition(1);
        itemBici.check(matches(hasBackgroundColor(R.color.colorBici)));
        itemBici.onChildView(withId(R.id.tvType)).check(matches(withText("BiciRota")));
        itemBici.onChildView(withId(R.id.tvAmount)).check(matches(withText("3")));

        // Verifies that the list of incidences contains an entry with 2 EstacionRota incidents
        // and has the correct background color
        DataInteraction itemEstacion = onData(anything()).inAdapterView(withId(R.id.lvIncidences)).atPosition(2);
        itemEstacion.check(matches(hasBackgroundColor(R.color.colorEstacion)));
        itemEstacion.onChildView(withId(R.id.tvType)).check(matches(withText("EstacionRota")));
        itemEstacion.onChildView(withId(R.id.tvAmount)).check(matches(withText("2")));
    }
}
