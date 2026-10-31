package es.unican.bicis.utils;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ListView;

import androidx.core.content.ContextCompat;

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

    /**
     * Matcher personalizado para comprobar el color de fondo de una View.
     */
    public static Matcher<View> hasBackgroundColor(final int colorResId) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                Drawable background = view.getBackground();
                if (background instanceof ColorDrawable) {
                    int color = ((ColorDrawable) background).getColor();
                    int expectedColor = ContextCompat.getColor(view.getContext(), colorResId);
                    return color == expectedColor;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("El elemento debería de tener el color con el id: " + colorResId);
            }
        };
    }
}
