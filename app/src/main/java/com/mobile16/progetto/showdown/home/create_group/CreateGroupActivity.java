package com.mobile16.progetto.showdown.home.create_group;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.mobile16.progetto.showdown.utils.db_connection.DBCallback;
import com.mobile16.progetto.showdown.utils.db_connection.DBManager;
import com.mobile16.progetto.showdown.R;
import com.mobile16.progetto.showdown.model.AppGroup;
import com.mobile16.progetto.showdown.model.IAppGroup;
import com.mobile16.progetto.showdown.utils.ISessionManager;
import com.mobile16.progetto.showdown.utils.SessionManager;

import java.util.List;
import java.util.Map;

/**
 * Activity utilizzata per la creazione di nuovi gruppi. Essa è anche listener per i due fragment
 * che utilizza.
 */
public class CreateGroupActivity extends AppCompatActivity
        implements CreateGroupFragment.OnCreateGroupListener, ListEmailFragment.OnEmailListener {

    private DBManager dbManager;
    private IAppGroup appGroup = null;
    private CreateGroupFragment createGroupFragment;
    private ISessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        this.dbManager = new DBManager(this);
        this.sessionManager = new SessionManager(getApplicationContext());

        this.createGroupFragment = new CreateGroupFragment();
        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fm.replace(R.id.container_prova, createGroupFragment);
        fm.commit();
    }

    // Richiamato quando viene premuto il pulsante di creazione del gruppo nel primo fragment
    @Override
    public void onCreateButtonClicked() {
        this.appGroup = new AppGroup(createGroupFragment.getGroupName(),
                createGroupFragment.getGroupDescription());
        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fm.replace(R.id.container_prova, new ListEmailFragment());
        fm.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fm.addToBackStack(null);
        fm.commit();
    }

    // Richiamato quando viene premuto il pulsante di creazione del gruppo nel secondo fragment
    @Override
    public void onCreateClicked(List<String> email_list) {
        if(appGroup == null){
            throw new NullPointerException("Il gruppo non è stato creato nel fragment precedente");
        }
        // Aggiungo l'utente corrente
        email_list.add(sessionManager.getLoggedUser().getMail());
        this.appGroup.insertEmails(email_list);
        DBCallback dbCallback = new DBCallback() {
            @Override
            public void onSuccess(Map<String, String> serverResponse) {
                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(Map<String, String> serverResponse) {
                Toast.makeText(getApplicationContext(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
            }
        };
        dbManager.insertGroup(appGroup, dbCallback);
    }
}
