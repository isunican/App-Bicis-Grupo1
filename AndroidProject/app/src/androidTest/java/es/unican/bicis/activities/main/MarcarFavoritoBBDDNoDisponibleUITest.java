package es.unican.bicis.activities.main;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static es.unican.bicis.utils.Matchers.withNetworkName;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import es.unican.bicis.R;

@HiltAndroidTest // Anotación obligatoria para pruebas con Hilt
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MarcarFavoritoBBDDNoDisponibleUITest {
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
    public void test03_bbddNoDisponible() {
        //mock del dao con reflection
        activityRule.getScenario().onActivity(activity -> {
            try {
                Field daoField = MainView.class.getDeclaredField("favouriteDAO");
                daoField.setAccessible(true);
                //sobrescribir la instancia real por un null
                daoField.set(activity, null);
            } catch (Exception e) {
                throw new RuntimeException("Error al hacer mock del DAO mediante reflection", e);
            }
        });

        onData(withNetworkName("TUeBICI"))
                .inAdapterView(withId(R.id.lvNetworks))
                .onChildView(withId(R.id.btPoints))
                .perform(click());

        onView(withText(R.string.mark_as_favorite)).perform(click());

        onData(withNetworkName("TUeBICI"))
                .inAdapterView(withId(R.id.lvNetworks))
                .onChildView(withId(R.id.imageView))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }
}
