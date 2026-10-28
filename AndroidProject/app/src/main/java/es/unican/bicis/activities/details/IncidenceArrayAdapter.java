package es.unican.bicis.activities.details;

import android.annotation.SuppressLint;
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
import es.unican.bicis.model.IncidenceType;

/**
 * Adapter that renders the incidences in each row of a ListView
 */
public class IncidenceArrayAdapter extends BaseAdapter {

    /** The list of Incidences to render */
    private final List<Incidence> Incidences;

    /** Context of the application */
    private final Context context;

    /**
     * Constructs an adapter to handle a list of incidences
     * @param context the application context
     * @param objects the list of incidences
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
        Incidence incidence = (Incidence) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.activity_station_details_list_item, parent, false);
        }

        TextView tvType = convertView.findViewById(R.id.tvType);
        TextView tvAmount = convertView.findViewById(R.id.tvAmount);

        // tipo
        {
            String type = incidence.getType();
            if (type == null) {
                type = context.getString(R.string.loadError);
            }
            tvType.setText(type);
        }


        // amount
        {
            int amount = incidence.getAmount();
            String amountText;
            if (amount == -1) {
                amountText = context.getString(R.string.loadError);
            } else {
                amountText = Integer.toString(amount);
            }
            tvAmount.setText(amountText);
        }

        // color
        {
            IncidenceType type = incidence.getTypeId();
            if (type != null) {
                int colorTexto = 0;
                int colorFondo = 0;
                switch (type) {
                   case BIKE:
                       colorTexto = context.getColor(R.color.colorTextoBici);
                       colorFondo = context.getColor(R.color.colorBici);
                       break;
                   case STATION:
                       colorTexto = context.getColor(R.color.colorTextoEstacion);
                       colorFondo = context.getColor(R.color.colorEstacion);
                       break;
                   case OTHER:
                       colorTexto = context.getColor(R.color.colorTextoOtros);
                       colorFondo = context.getColor(R.color.colorOtros);
                       break;
               }
                tvType.setTextColor(colorTexto);
                tvAmount.setTextColor(colorTexto);
                convertView.setBackgroundColor(colorFondo);
            }
        }


        return convertView;
    }
}