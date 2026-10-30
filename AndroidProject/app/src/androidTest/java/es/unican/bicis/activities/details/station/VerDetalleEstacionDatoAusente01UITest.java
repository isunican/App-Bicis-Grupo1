package es.unican.bicis.activities.details.station;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import es.unican.bicis.R;
import es.unican.bicis.activities.main.MainView;

@HiltAndroidTest // Anotación obligatoria para pruebas con Hilt
@RunWith(AndroidJUnit4.class)
@LargeTest
public class VerDetalleEstacionDatoAusente01UITest {

    // Regla de Hilt con orden 0 para que se ejecute primero
    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule(order = 1)
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    // Regla de la Actividad con orden 1 para que se lance después de inicializar Hilt
    @Rule(order = 2)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    @Test
    public void prueba01_DatosAusentes() {
        // 1. Clic exacto en la red BiciMAD de la primera pantalla
        onView(withText("BiciMAD")).perform(click());

        // 2. Clic exacto en la estación dictada por el plan de pruebas en la segunda pantalla
        onView(withText("390 - Estacion datos ausentes")).perform(click());
        // Se verifican los identificadores de la vista contra los datos exactos esperados
        onView(withId(R.id.tvStName)).check(matches(withText("390 - Estacion datos ausentes")));
        onView(withId(R.id.tvIdDisplay)).check(matches(withText("01f5011f76069b1e81a11d4d51dd9d1d")));

        onView(withId(R.id.tvLatDisplay)).check(matches(withText("40.38091762")));
        onView(withId(R.id.tvLongDisplay)).check(matches(withText("-3.72783615")));

        onView(withId(R.id.tvUsedSlotsDisplay)).check(matches(withText("-")));
        onView(withId(R.id.tvFreeSlotsDisplay)).check(matches(withText("-")));

        onView(withId(R.id.tvTimeDisplay)).check(matches(withText("-")));
    }
}
