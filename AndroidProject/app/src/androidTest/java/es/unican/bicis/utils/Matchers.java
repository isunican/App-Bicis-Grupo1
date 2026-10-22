package es.unican.bicis.utils;

import android.view.View;
import android.widget.ListView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

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



}
