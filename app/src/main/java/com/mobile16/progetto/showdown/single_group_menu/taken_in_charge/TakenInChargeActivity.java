package com.mobile16.progetto.showdown.single_group_menu.taken_in_charge;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
 * Activity che mostra le transazioni prese in carico per l'utente nello specifico gruppo.
 */
public class TakenInChargeActivity extends AppCompatActivity
        implements GenericListFragment.OnGenericListListener,
        GenericTransactionFragment.OnFragmentTransactionListener {

    private IAppGroup currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_charge);

        currentGroup = getIntent().getParcelableExtra("currentGroup");

        Fragment listFragment = GenericListFragment.newInstance(currentGroup, TransactionState.TAKEN);

        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fm.replace(R.id.container_take_charge, listFragment);
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
        fm.replace(R.id.container_take_charge, fragment);
        fm.commit();
    }

    /**
     * In this case button is unused.
     *
     * @param transaction the transaction created
     * @param button unused.
     */
    @Override
    public void onButtonClicked(ITransaction transaction, String button){
        IDBManager dbManager = new DBManager(this);
        DBCallback dbCallback = new DBCallback() {
            @Override
            public void onSuccess(Map<String, String> serverResponse) {
                finish();
            }

            @Override
            public void onFailure(Map<String, String> serverResponse) {
                Toast.makeText(getApplication(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
            }
        };
        dbManager.completeTransaction(transaction, dbCallback);
    }
}
