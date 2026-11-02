package es.unican.bicis.activities.main;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;
import static es.unican.bicis.utils.Matchers.withNetworkName;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import es.unican.bicis.R;


@HiltAndroidTest // Anotación obligatoria para pruebas con Hilt
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MarcarFavoritoExitoUITest {
    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    @Before
    public void setUp() {
        hiltRule.inject();
    }

    @Test
    public void test00_exitoSinRedesFavoritas() {
        //buscar la red bicimad clicar en los 3 puntos y abrir el menu de añadir a fav
        onData(withNetworkName("BiciMAD"))
                .inAdapterView(withId(R.id.lvNetworks))
                .onChildView(withId(R.id.btPoints))
                .perform(click());
        onView(withText(R.string.mark_as_favorite)).perform(click()); //añadir a fav

        //comprobar que se marca la estrella
        onData(withNetworkName("BiciMAD"))
                .inAdapterView(withId(R.id.lvNetworks))
                .onChildView(withId(R.id.imageView))
                .check(matches(isDisplayed()));

        //verificar que se pone la primera
        onData(anything())
                .inAdapterView(withId(R.id.lvNetworks))
                .atPosition(0)
                .onChildView(withId(R.id.tvName))
                .check(matches(withText("BiciMAD")));
    }
}
