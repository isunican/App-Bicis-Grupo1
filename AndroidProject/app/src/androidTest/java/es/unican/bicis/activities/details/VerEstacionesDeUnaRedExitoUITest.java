package es.unican.bicis.activities.details;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import java.util.jar.Manifest;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import es.unican.bicis.R;
import es.unican.bicis.activities.main.MainView;

@HiltAndroidTest
public class VerEstacionesDeUnaRedExitoUITest {
    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule(order = 2)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    @Test
    public void verEstacionesDeUnaRedExitoTest() {
        //elegimos TUeBICI
        onData(anything()).inAdapterView(withId(R.id.lvNetworks)).atPosition(2).perform(click());

        onView(allOf(withId(R.id.tvName), withParent(withId(R.id.main)))).check(matches(withText("TUeBICI")));
        onView(withId(R.id.tvCity)).check(matches(withText("Santander (ES)")));
        onView(withId(R.id.tvEbikes)).check(matches(withText("No")));
        onView(withId(R.id.tvCompanysNum)).check(matches(withText("1")));


        DataInteraction elementoLista = onData(anything()).inAdapterView(withId(R.id.lvStations)).atPosition(0);
        elementoLista.onChildView(withId(R.id.tvName)).check(matches(withText("Perinés")));
        elementoLista.onChildView(withId(R.id.tvDistance)).check(matches(withText("2.0 Km")));

        elementoLista = onData(anything()).inAdapterView(withId(R.id.lvStations)).atPosition(41);
        elementoLista.onChildView(withId(R.id.tvName)).check(matches(withText("Tetuan")));
        elementoLista.onChildView(withId(R.id.tvDistance)).check(matches(withText("0.9 Km")));
    }

}
