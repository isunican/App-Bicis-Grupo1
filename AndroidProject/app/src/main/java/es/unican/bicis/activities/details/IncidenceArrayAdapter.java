package es.unican.bicis.activities.details;

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
import es.unican.bicis.model.Incidence;

/**
 * Adapter that renders the incidences in each row of a ListView
 */
public class IncidenceArrayAdapter extends BaseAdapter {

    /** The list of Incidences to render */
    private final List<Incidence> Incidences;

    /** Context of the application */
    private final Context context;

    /**
     * Constructs an adapter to handle a list of networks
     * @param context the application context
     * @param objects the list of networks
     */
    public IncidenceArrayAdapter(@NonNull Context context, @NonNull List<Incidence> objects) {
        // we know the parameters are not null because of the @NonNull annotation
        this.Incidences = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return Incidences.size();
    }

    @Override
    public Object getItem(int position) {
        return Incidences.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Incidence Incidence = (Incidence) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.activity_details_list_item, parent, false);
        }

        // tipo
        {
            TextView tvType = convertView.findViewById(R.id.tvType);
            String type = Incidence.getType();
            if (type == null) {
                type = context.getString(R.string.noData);
            }
            tvType.setText(type);
        }

        // amount
        {
            TextView tvAmount = convertView.findViewById(R.id.tvAmount);
            int amount = Incidence.getAmount();
            String amountText;
            if (amount == -1) {
                amountText = context.getString(R.string.noData);
            } else {
                amountText = Integer.toString(amount);
            }
            tvAmount.setText(amount);
        }

        return convertView;
    }
}