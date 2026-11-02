package es.unican.bicis.utils;

import android.view.View;
import android.widget.ListView;

import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import es.unican.bicis.model.Network;

/**
 * Custom matchers for Espresso tests.
 */
public class Matchers {

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

    //matcher personalizado para buscar las redes dentro de la listview y marcar como favoritos
    public static Matcher<Object> withNetworkName(final String expectedName) {
        return new BoundedMatcher<Object, Network>(Network.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("una red con el nombre: " + expectedName);
            }

            @Override
            protected boolean matchesSafely(Network network) {
                // Compara el nombre esperado con el nombre de la red actual
                return network != null && expectedName.equals(network.getName());
            }
        };
    }


}
