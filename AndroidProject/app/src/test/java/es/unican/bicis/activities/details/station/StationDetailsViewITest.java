package es.unican.bicis.activities.details.station;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.parceler.Parcels;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static es.unican.bicis.common.TimestampUtils.formatTimestamp;

import es.unican.bicis.R;
import es.unican.bicis.model.Station;

/**
 * Pruebas de integración de StationDetailsView.
 * Se ubica en el directorio 'test' y se denomina <NombreClase>ITest.
 * Se utiliza Robolectric para el ciclo de vida de Android y Mockito para la capa de datos.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class StationDetailsViewITest {

    /**
     * IT-00: Integración con Mock válido (Éxito).
     */
    @Test
    public void testOnCreate_WithValidMock_ShowsData() {
        // 1. Crear el Mock de la estación usando Mockito
        Station mockStation = mock(Station.class);
        when(mockStation.getId()).thenReturn("01f5011f76069b1e81a11d4d51dd9d1d");
        when(mockStation.getName()).thenReturn("377 - Metro Abrantes");
        when(mockStation.getLatitude()).thenReturn(40.38091762);
        when(mockStation.getLongitude()).thenReturn(-3.72783615);
        when(mockStation.getTimestamp()).thenReturn("2026-09-24T11:11:40.490774+00:00Z");
        when(mockStation.getFreeBikes()).thenReturn(0);
        when(mockStation.getEmptySlots()).thenReturn(18);

        // 2. Preparar el Intent e inyectar el Mock empaquetado
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, StationDetailsView.class);
        intent.putExtra(StationDetailsView.INTENT_STATION, Parcels.wrap(mockStation));

        // 3. Lanzar la Actividad usando Robolectric (ActivityScenario)
        try (ActivityScenario<StationDetailsView> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                // Verificamos que la Actividad mapea el Mock a la UI correctamente
                TextView tvId = activity.findViewById(R.id.tvIdDisplay);
                TextView tvName = activity.findViewById(R.id.tvStName);
                TextView tvLat = activity.findViewById(R.id.tvLatDisplay);
                TextView tvLong = activity.findViewById(R.id.tvLongDisplay);
                TextView tvTime = activity.findViewById(R.id.tvTimeDisplay);
                TextView tvFreeBikes = activity.findViewById(R.id.tvUsedSlotsDisplay);
                TextView tvEmptySlots = activity.findViewById(R.id.tvFreeSlotsDisplay);

                assertEquals("01f5011f76069b1e81a11d4d51dd9d1d", tvId.getText().toString());
                assertEquals("377 - Metro Abrantes", tvName.getText().toString());
                assertEquals("40.38091762", tvLat.getText().toString());
                assertEquals("-3.72783615", tvLong.getText().toString());
                assertEquals(formatTimestamp("2026-09-24T11:11:40.490774+00:00Z"), tvTime.getText());
                assertEquals("0", tvFreeBikes.getText().toString());
                assertEquals("18", tvEmptySlots.getText().toString());
            });
        }
    }

    /**
     * IT-01: Integración con Mock de datos ausentes.
     */
    @Test
    public void testOnCreate_WithAbsentDataMock_ShowsHyphens() {
        Station mockStation = mock(Station.class);
        when(mockStation.getId()).thenReturn("01f5011f76069b1e81a11d4d51dd9d1d");
        when(mockStation.getName()).thenReturn("390 - Estacion datos ausentes");
        when(mockStation.getLatitude()).thenReturn(40.38091762);
        when(mockStation.getLongitude()).thenReturn(-3.72783615);

        // Simulamos la ausencia de datos (null)
        when(mockStation.getTimestamp()).thenReturn(null);
        when(mockStation.getFreeBikes()).thenReturn(null);
        when(mockStation.getEmptySlots()).thenReturn(null);

        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, StationDetailsView.class);
        intent.putExtra(StationDetailsView.INTENT_STATION, Parcels.wrap(mockStation));

        try (ActivityScenario<StationDetailsView> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                String noDataString = activity.getString(R.string.noData); // Extrae "-" de strings.xml

                assertEquals("01f5011f76069b1e81a11d4d51dd9d1d", ((TextView) activity.findViewById(R.id.tvIdDisplay)).getText().toString());
                assertEquals("390 - Estacion datos ausentes", ((TextView) activity.findViewById(R.id.tvStName)).getText().toString());

                // Comprobamos que latitud y longitud se muestran correctamente como texto
                assertEquals("40.38091762", ((TextView) activity.findViewById(R.id.tvLatDisplay)).getText().toString());
                assertEquals("-3.72783615", ((TextView) activity.findViewById(R.id.tvLongDisplay)).getText().toString());

                // Comprobamos que el onCreate asigna el guion a los datos ausentes del Mock
                assertEquals(noDataString, ((TextView) activity.findViewById(R.id.tvTimeDisplay)).getText().toString());
                assertEquals(noDataString, ((TextView) activity.findViewById(R.id.tvUsedSlotsDisplay)).getText().toString());
                assertEquals(noDataString, ((TextView) activity.findViewById(R.id.tvFreeSlotsDisplay)).getText().toString());
            });
        }
    }

    /**
     * IT-02: Integración con Mock de datos anómalos.
     */
    @Test
    public void testOnCreate_WithAnomalousDataMock_ShowsHyphens() {
        Station mockStation = mock(Station.class);
        when(mockStation.getId()).thenReturn("01f5011f76069b1e81a11d4d51dd9d1d");
        when(mockStation.getName()).thenReturn("377 - Metro Abrantes");
        when(mockStation.getTimestamp()).thenReturn("2026-09-24T11:11:40.490774+00:00Z");

        // Simulamos valores fuera de límites
        when(mockStation.getLatitude()).thenReturn(95.0);
        when(mockStation.getLongitude()).thenReturn(-185.0);
        when(mockStation.getFreeBikes()).thenReturn(-1);
        when(mockStation.getEmptySlots()).thenReturn(-5);

        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, StationDetailsView.class);
        intent.putExtra(StationDetailsView.INTENT_STATION, Parcels.wrap(mockStation));

        try (ActivityScenario<StationDetailsView> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                String noDataString = activity.getString(R.string.noData);

                // Comprobamos la detección de anomalías
                assertEquals(noDataString, ((TextView) activity.findViewById(R.id.tvLatDisplay)).getText().toString());
                assertEquals(noDataString, ((TextView) activity.findViewById(R.id.tvLongDisplay)).getText().toString());
                assertEquals(noDataString, ((TextView) activity.findViewById(R.id.tvUsedSlotsDisplay)).getText().toString());
                assertEquals(noDataString, ((TextView) activity.findViewById(R.id.tvFreeSlotsDisplay)).getText().toString());
            });
        }
    }

    @Test
    public void testOnCreate_WithoutStationExtra_ShowsError() {
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, StationDetailsView.class);
        // Lanzamos el Intent vacío sin INTENT_STATION
        try (ActivityScenario<StationDetailsView> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                // Verificar que se muestra el Toast con R.string.loadErrorStation
                assertEquals(
                        activity.getString(R.string.loadErrorStation),
                        org.robolectric.shadows.ShadowToast.getTextOfLatestToast()
                );
            });
        }
    }
}