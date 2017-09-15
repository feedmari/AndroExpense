package com.mobile16.progetto.showdown.home.create_group;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile16.progetto.showdown.utils.db_connection.DBCallback;
import com.mobile16.progetto.showdown.utils.db_connection.DBManager;
import com.mobile16.progetto.showdown.R;
import com.mobile16.progetto.showdown.utils.ISessionManager;
import com.mobile16.progetto.showdown.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Classe per l'inserimento dei contatti appartenenti ad un gruppo
 */
public class ListEmailFragment extends Fragment {

    private EmailAdapter adapter;
    private final List<String> emails = new ArrayList<String>(){};
    private EditText email_editText;
    private OnEmailListener listener;
    private ISessionManager sessionManager;

    // Listener implementato dall'activity CreateGroupActivity
    public interface OnEmailListener {
        void onCreateClicked(List<String> emails);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnEmailListener)
            this.listener = (OnEmailListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_list, container, false);
        adapter = new EmailAdapter(getActivity());
        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        this.email_editText = (EditText) view.findViewById(R.id.emailEditText);
        this.sessionManager = new SessionManager(getActivity());

        final DBManager dbManager = new DBManager(getActivity());

        view.findViewById(R.id.aggiungiEmailButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(email_editText.getText().toString().length() > 0) {
                    // Controllo se l'email è già stata inserita
                    if (emails.contains(email_editText.getText().toString())) {
                        Toast.makeText(getActivity(), R.string.email_inserted, Toast.LENGTH_LONG).show();
                    } else if (email_editText.getText().toString()
                            .equals(sessionManager.getLoggedUser().getMail())){ // Controllo se l'email sono io
                        Toast.makeText(getActivity(), R.string.your_email, Toast.LENGTH_LONG).show();
                    } else{
                        final DBCallback dbCallback = new DBCallback() {
                            @Override
                            public void onSuccess(Map<String, String> serverResponse) {
                                if (serverResponse.get("valida").equals("si")) {
                                    Toast.makeText(getActivity(), R.string.email_not_forund, Toast.LENGTH_LONG).show();
                                } else {
                                    updateEmailList(email_editText.getText().toString());
                                    email_editText.setText("");
                                }
                            }

                            @Override
                            public void onFailure(Map<String, String> serverResponse) {
                                Toast.makeText(getActivity(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
                            }
                        };
                        dbManager.checkMailValidity(email_editText.getText().toString(), dbCallback);
                    }
                    }else{
                        Toast.makeText(getActivity(), R.string.insert_email, Toast.LENGTH_LONG).show();

                }
            }
        });


        view.findViewById(R.id.creaGruppo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emails.isEmpty()){
                    Toast.makeText(getActivity(), R.string.no_email, Toast.LENGTH_LONG).show();
                }else{
                    listener.onCreateClicked(emails);
                }
            }
        });

        return view;
    }

    // Aggiorno la lista di email
    private void updateEmailList(String email) {
        adapter.add(email);
        emails.add(email);
        adapter.notifyDataSetChanged();
    }

    // Adapter per la lista di email
    private class EmailAdapter extends ArrayAdapter<String> {

        private LayoutInflater inflater;

        public EmailAdapter(Context context) {
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

