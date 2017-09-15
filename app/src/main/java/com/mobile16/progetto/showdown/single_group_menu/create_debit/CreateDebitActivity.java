package com.mobile16.progetto.showdown.single_group_menu.create_debit;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.mobile16.progetto.showdown.R;
import com.mobile16.progetto.showdown.model.IAppGroup;
import com.mobile16.progetto.showdown.model.ITransaction;
import com.mobile16.progetto.showdown.model.Transaction;
import com.mobile16.progetto.showdown.model.User;
import com.mobile16.progetto.showdown.single_group_menu.GenericTransactionFragment;
import com.mobile16.progetto.showdown.utils.ISessionManager;
import com.mobile16.progetto.showdown.utils.SessionManager;
import com.mobile16.progetto.showdown.utils.db_connection.DBCallback;
import com.mobile16.progetto.showdown.utils.db_connection.DBManager;

import java.util.Map;

/**
 * Activity che permette la creazione di un debito.
 */
public class CreateDebitActivity extends AppCompatActivity
        implements GenericTransactionFragment.OnFragmentTransactionListener {

    private IAppGroup currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_debit);
        ISessionManager sessionManager = new SessionManager(this);

        currentGroup = getIntent().getParcelableExtra("currentGroup");

        ITransaction t = new Transaction();
        t.setCreator(new User(sessionManager.getLoggedUser()));
        GenericTransactionFragment genericTransactionFragment = GenericTransactionFragment.newInstance(t);

        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fm.replace(R.id.activity_create_debit_container, genericTransactionFragment);
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
        // Inserimento della transaction nel DB
        DBManager dbManager = new DBManager(this);
        dbManager.insertTransaction(transaction, currentGroup, new DBCallback() {
            @Override
            public void onSuccess(Map<String, String> serverResponse) {
                finish();
            }

            @Override
            public void onFailure(Map<String, String> serverResponse) {
                Toast.makeText(getBaseContext(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
