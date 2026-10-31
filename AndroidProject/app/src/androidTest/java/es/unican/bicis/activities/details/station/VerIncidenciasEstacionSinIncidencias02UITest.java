package es.unican.bicis.activities.details.station;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static es.unican.bicis.utils.Matchers.withListSize;

import android.Manifest;

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
import es.unican.bicis.utils.DatabaseTestUtils;

@HiltAndroidTest
public class VerIncidenciasEstacionSinIncidencias02UITest {
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

        // In this case, the database will be empty, so we don't need to insert any data
    }

    @After
    public void tearDown() {
        DatabaseTestUtils.clearDatabase();
    }

    @Test
    public void testVerIncidenciasEstacionSinIncidencias02() {
        // Selects TUeBICI network from the main networks screen
        onView(withText("TUeBICI")).perform(click());

        // Selects Perinés station from the list of stations of the network
        onView(withText("Perinés")).perform(click());

        // Checks that the list of incidences is empty
        onView(withId(R.id.lvIncidences)).check(matches(withListSize(0)));
    }
}
