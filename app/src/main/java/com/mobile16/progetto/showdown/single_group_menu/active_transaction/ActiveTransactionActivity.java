package com.mobile16.progetto.showdown.single_group_menu.active_transaction;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.mobile16.progetto.showdown.R;
import com.mobile16.progetto.showdown.model.IAppGroup;
import com.mobile16.progetto.showdown.model.ITransaction;
import com.mobile16.progetto.showdown.model.TransactionState;
import com.mobile16.progetto.showdown.single_group_menu.GenericListFragment;
import com.mobile16.progetto.showdown.single_group_menu.GenericTransactionFragment;
import com.mobile16.progetto.showdown.utils.db_connection.DBCallback;
import com.mobile16.progetto.showdown.utils.db_connection.DBManager;
import com.mobile16.progetto.showdown.utils.db_connection.IDBManager;

import java.util.Map;

/**
 * Activity che mostra le transazioni attive per l'utente nello specifico gruppo.
 */
public class ActiveTransactionActivity extends AppCompatActivity
        implements GenericListFragment.OnGenericListListener,
        GenericTransactionFragment.OnFragmentTransactionListener {

    private IDBManager dbManager;

    private Fragment genericList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_transaction);
        dbManager = new DBManager(this);

        IAppGroup currentGroup = getIntent().getParcelableExtra("currentGroup");

        genericList = GenericListFragment.newInstance(currentGroup, TransactionState.ACTIVE);

        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fm.replace(R.id.container_active, genericList);
        fm.commit();
    }

    /**
     * Override del metodo per la classe GenericListFragment. Richiamato quando una transazione
     * dalla lista viene cliccata.
     *
     * @param transaction la transazione cliccata
     */
    @Override
    public void onItemClicked(ITransaction transaction) {
        Fragment fragment = GenericTransactionFragment.newInstance(transaction);
        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fm.replace(R.id.container_active, fragment);
        fm.commit();
    }

    /**
     * Override del metodo per la classe GenericListFragment. Richiamato quando uno dei due button
     * viene premuto.
     *
     * @param transaction La transazione corrente
     * @param button Il bottone cliccato (button1 o button2)
     */
    @Override
    public void onButtonClicked(ITransaction transaction, String button){
        if(button.equals("button2")){
            DBCallback dbCallback = new DBCallback() {
                @Override
                public void onSuccess(Map<String, String> serverResponse) {
                    FragmentTransaction fm = getFragmentManager().beginTransaction();
                    fm.replace(R.id.container_active, genericList);
                    fm.commit();
                }

                @Override
                public void onFailure(Map<String, String> serverResponse) {
                    Toast.makeText(getApplication(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
                }
            };
            dbManager.closeTransaction(transaction, dbCallback);
        } else{
            FragmentTransaction fm = getFragmentManager().beginTransaction();
            fm.replace(R.id.container_active, genericList);
            fm.commit();
        }
    }
}
