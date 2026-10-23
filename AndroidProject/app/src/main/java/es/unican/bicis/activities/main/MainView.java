package es.unican.bicis.activities.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import es.unican.bicis.R;
import es.unican.bicis.activities.details.IRedDetailsContract;
import es.unican.bicis.activities.details.StationsArrayAdapter;
import es.unican.bicis.activities.info.InfoView;
import es.unican.bicis.activities.details.RedDetailsView;
import es.unican.bicis.common.LocationUtils;
import es.unican.bicis.model.Network;
import es.unican.bicis.repository.INetworksRepository;

/**
 * The main view of the application. It shows a list of bike sharing networks.
 */
@AndroidEntryPoint
public class MainView extends AppCompatActivity implements IMainContract.View {

    /** The presenter of this view */
    private IMainContract.Presenter presenter;

    /** The repository to access the data. This is automatically injected by Hilt in this class */
    @Inject
    INetworksRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // The default theme does not include a toolbar.
        // In this app the toolbar is explicitly declared in the layout
        // Set this toolbar as the activity ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Evitar solape con el notch
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ConstraintLayout mainLayout = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            toolbar.setPadding(toolbar.getPaddingLeft(), insets.top, toolbar.getPaddingRight(), toolbar.getPaddingBottom());

            android.view.ViewGroup.LayoutParams params = toolbar.getLayoutParams();
            int baseHeight = getResources().getDimensionPixelSize(androidx.appcompat.R.dimen.abc_action_bar_default_height_material);
            params.height = baseHeight + insets.top;
            toolbar.setLayoutParams(params);

            v.setPadding(insets.left, 0, insets.right, insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        LocationUtils.init(getApplicationContext());

        // Obtener permisos de ubicacion
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si no los tiene, abrimos la ventanita para pedirlos (código 100)
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 100);
        } else {
            LocationUtils.updateLocation(() -> {
                ListView list = findViewById(R.id.lvStations);
                if (list != null && list.getAdapter() != null) {
                    ((StationsArrayAdapter) list.getAdapter()).notifyDataSetChanged();
                }
            });
        }

        // instantiate presenter and launch initial business logic
        presenter = new MainPresenter();
        presenter.init(this);
    }

    /**
     * @see AppCompatActivity#onResume()
     */
    @Override
    protected void onResume() {
        LocationUtils.updateLocation(() -> {
            ListView list = findViewById(R.id.lvStations);
            if (list != null && list.getAdapter() != null) {
                ((StationsArrayAdapter) list.getAdapter()).notifyDataSetChanged();
            }
        });
        super.onResume();
    }

    /**
     * This creates the menu that is shown in the action bar (the upper toolbar)
     * @param menu The options menu in which you place your items.
     *
     * @return true because we are defining a new menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * This is called when an item in the action bar menu is selected.
     * @param item The menu item that was selected.
     *
     * @return true if we have handled the selection
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menuItemInfo) {
            presenter.onMenuInfoClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @see IMainContract.View#init()
     */
    @Override
    public void init() {
        // initialize on click listeners (when clicking on a network in the list)
        ListView list = findViewById(R.id.lvNetworks);
        list.setOnItemClickListener((parent, view, position, id) -> {
            Network network = (Network) parent.getItemAtPosition(position);
            presenter.onNetworkClicked(network);
        });
    }

    /**
     * @see IMainContract.View#getNetworksRepository()
     * @return the repository to access the data
     */
    @Override
    public INetworksRepository getNetworksRepository() {
        return repository;
    }

    /**
     * @see IMainContract.View#showNetworks(List)
     * @param networks the list of networks
     */
    @Override
    public void showNetworks(List<Network> networks) {
        ListView list = findViewById(R.id.lvNetworks);
        NetworksArrayAdapter adapter = new NetworksArrayAdapter(this, networks);
        list.setAdapter(adapter);
    }

    /**
     * @see IMainContract.View#showLoadCorrect(int)
     * @param networks
     */
    @Override
    public void showLoadCorrect(int networks) {
        Toast.makeText(this, getString(R.string.loadCorrect, networks), Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IMainContract.View#showLoadError()
     */
    @Override
    public void showLoadError() {
        Toast.makeText(this, R.string.loadError, Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IRedDetailsContract.View#showRedError()
     */
    @Override
    public void showRedError() {
        Toast.makeText(this, R.string.redError, Toast.LENGTH_SHORT).show();
    }

    /**
     * @see IRedDetailsContract.View#getContext()
     */
    @Override
    public Context getContext() {
        return this;
    }

    /**
     * @see IMainContract.View#showNetworkDetails(Network)
     * @param network the network
     */
    @Override
    public void showNetworkDetails(Network network) {
        Intent intent = new Intent(this, RedDetailsView.class);
        intent.putExtra(RedDetailsView.INTENT_NETWORK, Parcels.wrap(network));
        startActivity(intent);
    }

    /**
     * @see IMainContract.View#showInfoActivity()
     */
    @Override
    public void showInfoActivity() {
        Intent intent = new Intent(this, InfoView.class);
        startActivity(intent);
    }

    /**
     * Este metodo se ejecuta automáticamente cuando el usuario responde al cuadro de diálogo de permisos
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Comprobamos que sea nuestra petición de permisos (código 100) y que el usuario haya aceptado
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // El usuario acaba de aceptar, actualizamos la ubicación ahora mismo
            LocationUtils.updateLocation(() -> {
                ListView list = findViewById(R.id.lvStations);
                if (list != null && list.getAdapter() != null) {
                    ((StationsArrayAdapter) list.getAdapter()).notifyDataSetChanged();
                }
            });
        }
    }
}