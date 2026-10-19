package es.unican.bicis.activities.details;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.Manifest;
import android.view.View;
import android.widget.ListView;

import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import es.unican.bicis.R;
import es.unican.bicis.activities.main.MainView;

@HiltAndroidTest
public class FiltrarPorDistanciaSinEstacionesEnRango {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    // ESTA ES LA REGLA QUE OTORGA EL PERMISO DE UBICACIÓN AUTOMÁTICAMENTE
    @Rule(order = 1)
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule(order = 2)
    public ActivityScenarioRule<MainView> activityRule =
            new ActivityScenarioRule<>(MainView.class);

    /**
     * Matcher personalizado para comprobar el número de estaciones mostradas en el ListView.
     */
    public static Matcher<View> withListSize(final int expectedSize) {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(final View view) {
                return ((ListView) view).getCount() == expectedSize;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("ListView debería tener " + expectedSize + " elementos");
            }
        };
    }

    private View decorView;

    @Before
    public void setUp() {
        // Si usas ActivityScenarioRule:
        activityRule.getScenario().onActivity(
                activity -> decorView = activity.getWindow().getDecorView()
        );
    }



    @Test
    public void testPrueba01_SinEstacionesEnRango() {
        // Abrimos los detalles de la red TUeBICI desde la pantalla principal
        onView(withText("TUeBICI")).perform(click());

        // 1 y 2. El técnico introduce 5Km en la aplicación
        onView(withId(R.id.etKmFilter)).perform(clearText(), typeText("0"), closeSoftKeyboard());

        // 4 y 5. El técnico selecciona la opción de ver a una distancia superior
        onView(withId(R.id.btnFilterLessThanKm)).perform(click());

        // 8. Se verifica que se muestran todas las estaciones que cumplen el requisito
        // (Ajusta el número y los textos inferiores según la ubicación fija de tu emulador/LocationUtils)
        onView(withId(R.id.lvStations)).check(matches(withListSize(0)));
        onView(withText(R.string.noStationsInRange)).inRoot(RootMatchers.withDecorView(not(decorView))).check(matches(isDisplayed()));
    }
}