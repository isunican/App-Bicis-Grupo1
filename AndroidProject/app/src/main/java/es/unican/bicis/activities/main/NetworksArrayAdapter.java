package es.unican.bicis.activities.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;

import java.util.Collections;
import java.util.List;
import java.util.Set;

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

    /** Interface for network item click callbacks */
    public interface OnNetworkClickListener {
        void onNetworkClick(Network network);
    }

    /** The list of networks to render */
    private final List<Network> networks;

    /** Context of the application */
    private final Context context;

    /** Listener for favorite button clicks */
    private final OnFavoriteClickListener favoriteClickListener;

    /** Listener for row clicks */
    private final OnNetworkClickListener networkClickListener;

    /** The set of favorite network ids */
    private Set<String> favoriteNetworkIds;

    /**
     * Constructs an adapter to handle a list of networks
     * @param context the application context
     * @param objects the list of networks
     */
    public NetworksArrayAdapter(@NonNull Context context, @NonNull List<Network> objects) {
        this(context, objects, null, null, Collections.emptySet());
    }

    /**
     * Constructs an adapter to handle a list of networks with a favorite listener
     * @param context the application context
     * @param objects the list of networks
     * @param favoriteClickListener the listener for favorite clicks
     */
    public NetworksArrayAdapter(@NonNull Context context, @NonNull List<Network> objects, @Nullable OnFavoriteClickListener favoriteClickListener) {
        this(context, objects, favoriteClickListener, null, Collections.emptySet());
    }

    /**
     * Constructs an adapter to handle a list of networks with a favorite listener and favorite network IDs
     * @param context the application context
     * @param objects the list of networks
     * @param favoriteClickListener the listener for favorite clicks
     * @param favoriteNetworkIds set of favorite network IDs
     */
    public NetworksArrayAdapter(@NonNull Context context, @NonNull List<Network> objects,
                                @Nullable OnFavoriteClickListener favoriteClickListener,
                                @Nullable Set<String> favoriteNetworkIds) {
        this(context, objects, favoriteClickListener, null, favoriteNetworkIds);
    }

    /**
     * Constructs an adapter to handle a list of networks with item click listener, favorite listener and favorite network IDs
     * @param context the application context
     * @param objects the list of networks
     * @param favoriteClickListener the listener for favorite clicks
     * @param networkClickListener the listener for item clicks
     * @param favoriteNetworkIds set of favorite network IDs
     */
    public NetworksArrayAdapter(@NonNull Context context, @NonNull List<Network> objects,
                                @Nullable OnFavoriteClickListener favoriteClickListener,
                                @Nullable OnNetworkClickListener networkClickListener,
                                @Nullable Set<String> favoriteNetworkIds) {
        this.networks = objects;
        this.context = context;
        this.favoriteClickListener = favoriteClickListener;
        this.networkClickListener = networkClickListener;
        this.favoriteNetworkIds = favoriteNetworkIds != null ? favoriteNetworkIds : Collections.emptySet();
    }

    /**
     * Updates the set of favorite network IDs and refreshes the list
     * @param favoriteNetworkIds the new set of favorite network IDs
     */
    public void setFavoriteNetworkIds(@Nullable Set<String> favoriteNetworkIds) {
        this.favoriteNetworkIds = favoriteNetworkIds != null ? favoriteNetworkIds : Collections.emptySet();
        notifyDataSetChanged();
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

        // handle click on the item row itself
        convertView.setOnClickListener(v -> {
            if (networkClickListener != null) {
                networkClickListener.onNetworkClick(network);
            } else if (context instanceof MainView) {
                ((MainView) context).showNetworkDetails(network);
            }
        });

        // name
        {
            TextView tv = convertView.findViewById(R.id.tvName);
            if (tv != null) {
                tv.setText(network.getName());
            }
        }

        // location (city)
        {
            TextView tv = convertView.findViewById(R.id.tvCity);
            if (tv != null) {
                String city = network.getLocation() != null ? network.getLocation().getCity() : "";
                tv.setText(city);
            }
        }

        // favorite star icon
        boolean isFavorite = network != null && network.getId() != null && favoriteNetworkIds.contains(network.getId());
        {
            ImageView ivStar = convertView.findViewById(R.id.imageView);
            if (ivStar != null) {
                ivStar.setVisibility(isFavorite ? View.VISIBLE : View.GONE);
            }
        }

        // options button (three points)
        {
            ImageButton btPoints = convertView.findViewById(R.id.btPoints);
            if (btPoints != null) {
                btPoints.setOnClickListener(v -> {
                    PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.network_item_menu, popup.getMenu());

                    MenuItem favItem = popup.getMenu().findItem(R.id.action_mark_favorite);
                    if (favItem != null) {
                        favItem.setTitle(R.string.mark_as_favorite);
                    }

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