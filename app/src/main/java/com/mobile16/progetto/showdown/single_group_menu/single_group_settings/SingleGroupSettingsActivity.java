package com.mobile16.progetto.showdown.single_group_menu.single_group_settings;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile16.progetto.showdown.R;
import com.mobile16.progetto.showdown.model.IAppGroup;
import com.mobile16.progetto.showdown.single_group_menu.SingleGroupMenuActivity;
import com.mobile16.progetto.showdown.utils.ISessionManager;
import com.mobile16.progetto.showdown.utils.SessionManager;
import com.mobile16.progetto.showdown.utils.db_connection.DBCallback;
import com.mobile16.progetto.showdown.utils.db_connection.DBManager;
import com.mobile16.progetto.showdown.utils.db_connection.IDBManager;

import java.util.List;
import java.util.Map;

/**
 * Impostazioni del gruppo.
 */
public class SingleGroupSettingsActivity extends AppCompatActivity {

    private IAppGroup currentGroup;
    private EmailAdapter adapter;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_group_settings);

        currentGroup = getIntent().getParcelableExtra("currentGroup");
        TextView groupName = (TextView) findViewById(R.id.state_name);
        TextView groupDescription = (TextView) findViewById(R.id.group_description_settings);
        ListView listEmails = (ListView) findViewById(R.id.genericListView);
        this.adapter = new EmailAdapter(this.getBaseContext(), currentGroup.getUsersList());
        listEmails.setAdapter(adapter);

        groupName.setText(currentGroup.getGroupName());
        groupDescription.setText(currentGroup.getGroupDescription());

        // Ricavo dal server gli utenti che fanno parte del gruppo
        IDBManager dbManager = new DBManager(this);
        DBCallback dbCallback = new DBCallback() {
            @Override
            public void onSuccess(Map<String, String> serverResponse) {
                int i = 1;
                while(serverResponse.containsKey("id_utente"+i)){
                    currentGroup.insertUserEmail(serverResponse.get("mail"+i));
                    adapter.add(serverResponse.get("username"+i) + "("+ serverResponse.get("mail"+i) + ")");
                    i++;
                }
            }

            @Override
            public void onFailure(Map<String, String> serverResponse) {
                Toast.makeText(getApplication(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
            }
        };
        dbManager.getUsersInGroup(currentGroup, dbCallback);
    }

    // Metodo richiamato dal button 'leave from this group', per abbandonare il gruppo corrente
    public void leaveFromThisGroup(View view){
        IDBManager dbManager = new DBManager(this);
        ISessionManager sessionManager = new SessionManager(this);
        DBCallback dbCallback = new DBCallback() {
            @Override
            public void onSuccess(Map<String, String> serverResponse) {
                // Torno indietro all'activity precedente, comunicandoglielo
                setResult(SingleGroupMenuActivity.ACTIVITY_FOR_RESULT_ID);
                finish();
            }

            @Override
            public void onFailure(Map<String, String> serverResponse) {
                Toast.makeText(getApplication(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
            }
        };
        dbManager.leaveGroup(sessionManager.getLoggedUser(), currentGroup, dbCallback);
    }


    // ADAPTER che modella la lista degli utenti presenti in questo gruppo

    private class EmailAdapter extends ArrayAdapter<String> {
        private LayoutInflater inflater;

        @SuppressWarnings("unused")
        public EmailAdapter(Context context, List<String> emails) {
            super(context, 0);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.item_listview, parent, false);

            String email = getItem(position);
            ((TextView) convertView.findViewById(R.id.listview_item_text)).setText(email);

            return convertView;
        }
    }
}
