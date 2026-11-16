package es.unican.bicis.activities.details.station;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static es.unican.bicis.utils.Matchers.hasBackgroundColor;

import android.Manifest;
import android.widget.EditText;

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
public class ActualizarIncidenciaAPersonalizadaExitoUITest {

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
         * | 4  | 0a7638aa73949d3c8742f13f56cedff0   | STATION | EstacionRota                        |
         * | 5  | 0a7638aa73949d3c8742f13f56cedff0   | OTHER   | Incidencia de prueba                |
         * +----+------------------------------------+---------+-------------------------------------+
         */
        db.incidenciaDao().insertarIncidencia(new Incident(idEstacion, IncidentType.BIKE, "Defective bike"));
        db.incidenciaDao().insertarIncidencia(new Incident(idEstacion, IncidentType.STATION, "Damaged station"));
        db.incidenciaDao().insertarIncidencia(new Incident(idEstacion, IncidentType.OTHER, "Incidencia de prueba"));
    }

    @After
    public void tearDown() {
        DatabaseTestUtils.clearDatabase();
    }

    @Test
    public void actualizarIncidenciaAPredefinidaExitoTest() {

        onView(withText("TUeBICI")).perform(click());
        onView(withText("Perinés")).perform(click());


        onData(anything()).inAdapterView(withId(R.id.lvIncidences)).atPosition(0)
                .onChildView(withId(R.id.btEditarIncidencia)).perform(click());

        onView(withId(R.id.spEditType)).perform(click());

        String[] expectedSpOptions = new String[3];
        expectedSpOptions[0] = "Damaged station";
        expectedSpOptions[1] = "Defective bike";
        expectedSpOptions[2] = "Other issue";

        for (String option : expectedSpOptions) {
            onData(allOf(is(instanceOf(String.class)), is(option)))
                    .inRoot(isPlatformPopup())
                    .check(matches(isDisplayed()));
        }

        onData(allOf(is(instanceOf(String.class)), is(expectedSpOptions[2])))
                .inRoot(isPlatformPopup())
                .perform(click());

        String expectedTitle = "Edit Incident";
        String expectedSubtitle1 = "Type";
        String expectedSubtitle2 = "Description";
        String expectedPositiveButtonText = "Confirm";
        String expectedNegativeButtonText = "Cancel";

        onView(withText(expectedTitle)).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText(expectedSubtitle1)).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText(expectedSubtitle2)).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText(expectedPositiveButtonText)).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withText(expectedNegativeButtonText)).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(instanceOf(EditText.class)).inRoot(isDialog()).check(matches(allOf(withText("Incidencia de prueba"), isDisplayed())));

        onView(withId(R.id.etEditDescription)).inRoot(isDialog()).perform(clearText(), typeText("Incidencia de prueba 2"), closeSoftKeyboard());

        onView(withText(expectedPositiveButtonText)).inRoot(isDialog()).perform(click());

        DataInteraction itemEstacion = onData(anything()).inAdapterView(withId(R.id.lvIncidences)).atPosition(0);
        itemEstacion.check(matches(hasBackgroundColor(R.color.colorOtros)));
        itemEstacion.onChildView(withId(R.id.tvType)).check(matches(withText("Incidencia de prueba 2")));
        itemEstacion.onChildView(withId(R.id.tvAmount)).check(matches(withText("1")));

    }
}