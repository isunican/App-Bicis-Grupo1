package es.unican.bicis.activities.details;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import es.unican.bicis.R;
import es.unican.bicis.activities.main.MainView;

@HiltAndroidTest
public class VerInfoDetalladaRedExitoUITest {

    @Rule(order = 0)  // the Hilt rule must execute first
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    @Test
    public void verInfoDetalladaRedExitoTest() {
        //Hace click en el primer elemento de la lista
        onData(anything()).inAdapterView(ViewMatchers.withId(R.id.lvNetworks)).atPosition(0).perform(click());

        /* Suponemos que el elemento 0 es Bicing segun el .json de ejemplo en el directorio raw */

        //Comprueba que se muestren los datos correctamente

        onView(withId(R.id.tvName)).check(matches(withText("Bicing")));
        onView(withId(R.id.tvCity)).check(matches(withText("Barcelona (ES)")));
        onView(withId(R.id.tvEbikes)).check(matches(withText("Yes")));
        onView(withId(R.id.tvCompanysNum)).check(matches(withText("3")));

        onData(anything()).inAdapterView(withId(R.id.lvCompanys)).atPosition(0)
                .check(matches(withText("Barcelona de Serveis Municipals, S.A. (BSM)")));

        onData(anything()).inAdapterView(withId(R.id.lvCompanys)).atPosition(1)
                .check(matches(withText("CESPA")));

        onData(anything()).inAdapterView(withId(R.id.lvCompanys)).atPosition(2)
                .check(matches(withText("PBSC")));
    }
}
