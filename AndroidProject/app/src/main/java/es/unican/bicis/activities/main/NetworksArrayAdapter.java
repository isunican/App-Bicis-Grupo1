package es.unican.bicis.activities.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;

import java.util.List;

import es.unican.bicis.R;
import es.unican.bicis.model.Network;

/**
 * Adapter that renders the bike networks in each row of a ListView
 */
public class NetworksArrayAdapter extends BaseAdapter {

    /** Interface for favorite click callbacks */
    public interface OnFavoriteClickListener {
        void onFavoriteClick(Network network);
    }

    /** The list of networks to render */
    private final List<Network> networks;

    /** Context of the application */
    private final Context context;

    /** Listener for favorite button clicks */
    private final OnFavoriteClickListener favoriteClickListener;

    /**
     * Constructs an adapter to handle a list of networks
     * @param context the application context
     * @param objects the list of networks
     */
    public NetworksArrayAdapter(@NonNull Context context, @NonNull List<Network> objects) {
        this(context, objects, null);
    }

    /**
     * Constructs an adapter to handle a list of networks with a favorite listener
     * @param context the application context
     * @param objects the list of networks
     * @param favoriteClickListener the listener for favorite clicks
     */
    public NetworksArrayAdapter(@NonNull Context context, @NonNull List<Network> objects, @Nullable OnFavoriteClickListener favoriteClickListener) {
        this.networks = objects;
        this.context = context;
        this.favoriteClickListener = favoriteClickListener;
    }

    @Override
    public int getCount() {
        return networks.size();
    }

    @Override
    public Object getItem(int position) {
        return networks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Network network = (Network) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.activity_main_list_item, parent, false);
        }

        // name
        {
            TextView tv = convertView.findViewById(R.id.tvName);
            tv.setText(network.getName());
        }

        // location (city)
        {
            TextView tv = convertView.findViewById(R.id.tvCity);
            String city = network.getLocation() != null ? network.getLocation().getCity() : "";
            tv.setText(city);
        }

        // options button (three points)
        {
            ImageButton btPoints = convertView.findViewById(R.id.btPoints);
            if (btPoints != null) {
                btPoints.setOnClickListener(v -> {
                    PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.network_item_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == R.id.action_mark_favorite) {
                            if (favoriteClickListener != null) {
                                favoriteClickListener.onFavoriteClick(network);
                            }
                            return true;
                        }
                        return false;
                    });
                    popup.show();
                });
            }
        }

        return convertView;
    }
}