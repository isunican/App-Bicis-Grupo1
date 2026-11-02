package es.unican.bicis.activities.details.station;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;
import android.widget.Spinner;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import es.unican.bicis.R;
import es.unican.bicis.activities.main.MainView;

@HiltAndroidTest
public class CrearIncidenciaPredefinidaExitoUITest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule(order = 2)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    @Test
    public void crearIncidenciaPredefinidaExitoTest() {

        onView(withText("TUeBICI")).perform(click());

        onView(withText("Perinés")).perform(click());

        onView(withId(R.id.btBotonIncidencia)).perform(click());

        ViewInteraction spIncidencias = onView(withId(R.id.spIncidencias));
        spIncidencias.perform(click());
    }
}
