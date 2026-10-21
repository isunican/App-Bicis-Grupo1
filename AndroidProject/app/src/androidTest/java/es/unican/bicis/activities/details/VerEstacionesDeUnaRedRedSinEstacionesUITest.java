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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

import android.view.View;
import android.widget.ListView;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import es.unican.bicis.R;
import es.unican.bicis.activities.main.MainView;

@HiltAndroidTest
public class VerEstacionesDeUnaRedRedSinEstacionesUITest {
    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule(order = 2)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    View decorView;
    @Before
    public void init() {
        activityRule.getScenario().onActivity(activity -> decorView = activity.getWindow().getDecorView());
    }

    @Test
    public void verEstacionesDeUnaRedRedSinEstacionesTest() throws InterruptedException {

        onView(withText("Loaded 4 networks")).inRoot(RootMatchers.withDecorView(not(decorView))).check(matches(isDisplayed()));
        //elegimos TUeBICI
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

        onView(withText(R.string.noStations)).inRoot(RootMatchers.withDecorView(not(decorView))).check(matches(isDisplayed()));

    }

}
