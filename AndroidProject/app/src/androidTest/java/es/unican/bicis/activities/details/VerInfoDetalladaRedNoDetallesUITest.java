package es.unican.bicis.activities.details;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import es.unican.bicis.R;
import es.unican.bicis.activities.main.MainView;
import es.unican.bicis.common.Utils;
import es.unican.bicis.injection.RepositoriesModule;
import es.unican.bicis.model.Network;
import es.unican.bicis.repository.INetworksRepository;
import es.unican.bicis.repository.NetworkDetailsCallback;
import es.unican.bicis.repository.NetworksCallback;

@UninstallModules(RepositoriesModule.class) //Necesitamos desvincular la inyeccion
@HiltAndroidTest
public class VerInfoDetalladaRedNoDetallesUITest {

    @Rule(order = 0)  // the Hilt rule must execute first
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    /*
     Tenemos que sobreescribir la inyeccion para que se lea sirectamente el .json
     que tiene las redes sin detalles
     */
    @BindValue
    INetworksRepository repository = new INetworksRepository() {
        @Override
        public void requestNetworks(NetworksCallback cb) {
            Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
            List<Network> networks = Utils.parseNetworks(context, R.raw.networks_no_detail);
            cb.onSuccess(networks);
        }

        @Override
        public void requestNetwork(NetworkDetailsCallback cb, String networkId) {
            /* Dejar vacio ya que no es necesario*/
        }
    };

    @Rule(order = 1)
    public ActivityScenarioRule<MainView> activityRule = new ActivityScenarioRule<>(MainView.class);

    @Before
    public void setup() {
        hiltRule.inject();
    }

    @Test
    public void verInfoDetalladaRedNoDetallesTest() {
        // Hace click en el segundo elemento de la lista (posición 1, que es BiciMAD)
        onData(anything()).inAdapterView(withId(R.id.lvNetworks)).atPosition(1).perform(click());

        // Comprueba que se muestren los datos correctamente según el JSON
        onView(withId(R.id.tvName)).check(matches(withText("BiciMAD")));
        onView(withId(R.id.tvCity)).check(matches(withText("- (-)")));
        onView(withId(R.id.tvEbikes)).check(matches(withText("-")));
        onView(withId(R.id.tvCompanysNum)).check(matches(withText("-")));
    }
}
