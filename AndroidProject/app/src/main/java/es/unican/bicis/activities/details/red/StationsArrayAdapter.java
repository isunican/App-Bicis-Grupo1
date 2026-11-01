package es.unican.bicis.activities.details.red;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import es.unican.bicis.R;
import es.unican.bicis.common.LocationUtils;
import es.unican.bicis.common.Utils;
import es.unican.bicis.model.Station;

/**
 * Adapter that renders the bike stations in each row of a ListView
 */
public class StationsArrayAdapter extends BaseAdapter {

    /** The list of stations to render */
    private final List<Station> stations;

    /** Context of the application */
    private final Context context;

    /**
     * Constructs an adapter to handle a list of networks
     * @param context the application context
     * @param objects the list of networks
     */
    public StationsArrayAdapter(@NonNull Context context, @NonNull List<Station> objects) {
        // we know the parameters are not null because of the @NonNull annotation
        this.stations = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return stations.size();
    }

    @Override
    public Object getItem(int position) {
        return stations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Station station = (Station) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.activity_details_list_item, parent, false);
        }

        // name
        {
            TextView tvName = convertView.findViewById(R.id.tvName);
            String name = station.getName();
            if (name == null) {
                name = context.getString(R.string.noData);
            }
            tvName.setText(name);
        }

        // distance
        {
            String distanceString = context.getString(R.string.noData);
            if (Utils.isLocationEnabled(context)) {
                float distance = LocationUtils.calcularDistanciaA(station.getLatitude(), station.getLongitude());
                if (distance == -1){
                    distanceString = context.getString(R.string.noData);
                } else {
                    distanceString = String.format(java.util.Locale.US, "%.1f Km", distance);
                }
            }
            TextView tvDistance = convertView.findViewById(R.id.tvDistance);
            tvDistance.setText(distanceString);
        }

        return convertView;
    }
}