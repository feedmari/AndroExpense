package com.mobile16.progetto.showdown.home;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mobile16.progetto.showdown.R;
import com.mobile16.progetto.showdown.gcm.QuickstartPreferences;
import com.mobile16.progetto.showdown.gcm.RegistrationIntentService;
import com.mobile16.progetto.showdown.home.create_group.CreateGroupActivity;
import com.mobile16.progetto.showdown.login.MainActivity;
import com.mobile16.progetto.showdown.model.IAppGroup;
import com.mobile16.progetto.showdown.single_group_menu.SingleGroupMenuActivity;
import com.mobile16.progetto.showdown.utils.SessionManager;

import java.util.ArrayList;

/**
 * Classe Home, utilizzata per la gestione iniziale dei gruppi, subito dopo il Login.
 * Al momento dell'accesso, registro sul GCM il dispositivo e l'utente che lo sta utilizzando.
 */

public class HomeActivity extends AppCompatActivity implements ListFragment.OnListListener {

    private static final int ACTIVITY_ADD_GROUP = 1;
    private ArrayList<NavItem> mNavItems = new ArrayList<>();
    private SessionManager session;

    // Variabili per gcm
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "HomeActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        session = new SessionManager(getApplicationContext());

        if(!session.isLoggedIn()){
            this.logoutUser();
        }

        FragmentManager fragmentManager = getFragmentManager();
        Fragment listFragment = fragmentManager.findFragmentById(R.id.list_fragment);
        if(listFragment == null){
            replaceFragment(new ListFragment(), false);
        }

        //Menu ListView
        mNavItems.add(new NavItem("Logout!", "Click here for logout.", R.drawable.logout));

        // DrawerLayout
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        RelativeLayout mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        ListView mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        mDrawerLayout.openDrawer(Gravity.LEFT);
        ((TextView)(this.findViewById(R.id.userName))).setText(session.getLoggedUser().getUsername());

        //gcm
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.v("Token Sent","all ok");
                } else {
                    Log.v("Token error","nothing ok");
                }
            }
        };

        // Registering BroadcastReceiver
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }



    // Click sul pulsante aggiungi gruppo.
    @Override
    public void onClickAddNewGroup() {
        startActivityForResult(new Intent(this, CreateGroupActivity.class), ACTIVITY_ADD_GROUP);
    }

    // Click su di uno specifico gruppo
    @Override
    public void onGroupClicked(IAppGroup appGroup) {
        Intent intent = new Intent(HomeActivity.this, SingleGroupMenuActivity.class);
        intent.putExtra("currentGroup", appGroup);
        startActivityForResult(intent, SingleGroupMenuActivity.ACTIVITY_FOR_RESULT_ID);
    }

    private void replaceFragment(Fragment fragment, boolean back) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        if (back)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }

    // Activity result utilizzata per intercettare i risultati dalle activity lanciate.
    // Al ritorno aggiorno nuovamente la lista di gruppi presente.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager manager = getFragmentManager();
        ListFragment list = (ListFragment) manager.findFragmentById(R.id.list_fragment);
        list.updateGroupList();
    }

    // Eseguo il logout dell'utente
    private void logoutUser() {
        session.userLogout();
        // Launching the login activity
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Classe relativa all'utente loggato, visualizzato nel menu scorrevole laterale

    class NavItem {
        String mTitle;
        String mSubtitle;
        int mIcon;

        public NavItem(String title, String subtitle, int icon) {
            mTitle = title;
            mSubtitle = subtitle;
            mIcon = icon;
        }
    }

    // Metodo richiamato dal drower laterale quando uno specifico utente è selezionato
    @SuppressWarnings("unused")
    private void selectItemFromDrawer(int position) {
        this.logoutUser();
    }

    class DrawerListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<NavItem> mNavItems;

        public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
            mContext = context;
            mNavItems = navItems;
        }

        @Override
        public int getCount() {
            return mNavItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mNavItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        @SuppressWarnings("all")
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_drawer, null);
            }
            else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
            ImageView iconView = (ImageView) view.findViewById(R.id.icon);

            titleView.setText( mNavItems.get(position).mTitle );
            subtitleView.setText( mNavItems.get(position).mSubtitle );
            iconView.setImageResource(mNavItems.get(position).mIcon);

            return view;
        }
    }

    //gcm methods
    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
