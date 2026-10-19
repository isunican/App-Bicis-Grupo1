package es.unican.bicis.common;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * Clase de apoyo para la visualizacion de los timestamps de las bicis
 */
public class TimestampUtils {

    /**
     * Formats a timestamp for the user display
     * @param rawTimestamp raw timestamp string
     * @return formatted timestamp string
     */
    public static String formatTimestamp(String rawTimestamp) {
        //Clean redundant time 'Z' offset if already included (+00:00Z -> +00:00)
        String cleanTimestamp = rawTimestamp.replaceAll("([+-]\\d{2}:\\d{2})Z$",
                "$1");

        //Parse string to format ISO 8601
        OffsetDateTime odt = OffsetDateTime.parse(cleanTimestamp);

        //Format string to adapt to local date and time standards
        DateTimeFormatter formatter = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT)
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault());

        return formatter.format(odt);
    }
}
