package com.mobile16.progetto.showdown.single_group_menu.closed_transaction;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mobile16.progetto.showdown.R;
import com.mobile16.progetto.showdown.model.IAppGroup;
import com.mobile16.progetto.showdown.model.ITransaction;
import com.mobile16.progetto.showdown.model.TransactionState;
import com.mobile16.progetto.showdown.single_group_menu.GenericListFragment;
import com.mobile16.progetto.showdown.single_group_menu.GenericTransactionFragment;

/**
 * Activity che mostra le transazioni chiuse per l'utente nello specifico gruppo.
 */
public class ClosedTransactionActivity extends AppCompatActivity
        implements GenericListFragment.OnGenericListListener,
        GenericTransactionFragment.OnFragmentTransactionListener{

    private Fragment genericList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closed_transaction);

        IAppGroup currentGroup = getIntent().getParcelableExtra("currentGroup");

        genericList = GenericListFragment.newInstance(currentGroup, TransactionState.CLOSED);

        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fm.replace(R.id.container_closed, genericList);
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
        fm.replace(R.id.container_closed, fragment);
        fm.commit();
    }

    /**
     * Override del metodo per la classe GenericListFragment. Richiamato quando uno dei due button
     * viene premuto.
     *
     * @param transaction La transazione corrente
     * @param button unused
     */
    @Override
    public void onButtonClicked(ITransaction transaction, String button){
        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fm.replace(R.id.container_closed, genericList);
        fm.commit();
    }
}
