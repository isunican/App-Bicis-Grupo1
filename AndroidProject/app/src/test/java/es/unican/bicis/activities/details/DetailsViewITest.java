package es.unican.bicis.activities.details;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.parceler.Parcels;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.io.InputStream;
import java.io.InputStreamReader;

import es.unican.bicis.R;
import es.unican.bicis.model.Network;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class DetailsViewITest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    public static Network getNetworkFromNetworksJson(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.networks);
        JsonObject root = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
        JsonArray networksArray = root.getAsJsonArray("networks");

        return new Gson().fromJson(networksArray.get(0), Network.class);
    }

    @Test
    public void detailsViewMuestraDatosCorrectosDesdeJson() {
        // 1. Obtener la red desde el JSON
        Network networkCargada = getNetworkFromNetworksJson(context);

        // 2. Crear el Intent empaquetando la red con Parcels
        Intent intent = new Intent(context, DetailsView.class);
        intent.putExtra(DetailsView.INTENT_NETWORK, Parcels.wrap(networkCargada));

        // 3. Crear e inicializar la Activity directamente con Robolectric (sin bloqueo de Espresso)
        try (ActivityController controller = Robolectric.buildActivity(DetailsView.class, intent)) {
            DetailsView activity = (DetailsView) controller.setup().get();

            // 4. Obtener las vistas directamente de la Activity
            TextView tvName = activity.findViewById(R.id.tvName);
            TextView tvCity = activity.findViewById(R.id.tvCity);
            TextView tvEbikes = activity.findViewById(R.id.tvEbikes);
            TextView tvCompanysNum = activity.findViewById(R.id.tvCompanysNum);
            ListView lvCompanys = activity.findViewById(R.id.lvCompanys);

            // 5. Aserciones con JUnit
            assertEquals("Bicing", tvName.getText().toString());
            assertEquals("Barcelona (ES)", tvCity.getText().toString());
            assertEquals(context.getString(R.string.yes), tvEbikes.getText().toString());
            assertEquals("3", tvCompanysNum.getText().toString());

            assertNotNull(lvCompanys.getAdapter());
            assertEquals(3, lvCompanys.getAdapter().getCount());
            assertEquals("Barcelona de Serveis Municipals, S.A. (BSM)", lvCompanys.getAdapter().getItem(0));
            assertEquals("CESPA", lvCompanys.getAdapter().getItem(1));
            assertEquals("PBSC", lvCompanys.getAdapter().getItem(2));
        }
    }
}