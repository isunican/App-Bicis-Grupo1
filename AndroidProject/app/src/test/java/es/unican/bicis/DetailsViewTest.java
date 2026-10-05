package es.unican.bicis;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import es.unican.bicis.activities.details.DetailsView;
import es.unican.bicis.model.NetworksResponse;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class DetailsViewTest {

    private Intent createFakeIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DetailsView.class);
        intent.putExtra(DetailsView.INTENT_NETWORK, new Bundle());
        return intent;
    }


    @Test
    public void testPU01RedValidaConDatos(){
        
    }

    @Test
    public void testPU02RedSinInfoDetallada(){

    }

    @Test
    public void testPU03BBDDNoAccesible(){

    }

    @Test
    public void testPU04DatosInconsistentes(){

    }
}
