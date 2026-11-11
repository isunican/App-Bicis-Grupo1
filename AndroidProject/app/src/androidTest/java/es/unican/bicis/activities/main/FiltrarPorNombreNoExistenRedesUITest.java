package es.unican.bicis.activities.main;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.AllOf.allOf;

import static es.unican.bicis.utils.Matchers.withListSize;

import android.widget.EditText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

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
public class FiltrarPorNombreNoExistenRedesUITest {
    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Rule(order = 1)
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule(order = 2)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    @Before
    public void setUp() {
        hiltRule.inject();
    }

    @Test
    public void test02_NoExistenRedes() {
        //buscar la barra de busqueda y buscar su campo de texto, introducir texto y pulsar la lupa
        onView(allOf(isDescendantOfA(withId(R.id.searchView)), isAssignableFrom(EditText.class)))
                .perform(clearText(), typeText("Inexistente"), pressImeActionButton());
        onView(withId(R.id.lvNetworks)).check(matches(withListSize(lessThanOrEqualTo(0))));
    }
}
