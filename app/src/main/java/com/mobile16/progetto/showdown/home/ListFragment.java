package com.mobile16.progetto.showdown.home;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile16.progetto.showdown.utils.db_connection.DBCallback;
import com.mobile16.progetto.showdown.utils.db_connection.DBManager;
import com.mobile16.progetto.showdown.utils.db_connection.IDBManager;
import com.mobile16.progetto.showdown.R;
import com.mobile16.progetto.showdown.model.AppGroup;
import com.mobile16.progetto.showdown.model.IAppGroup;
import com.mobile16.progetto.showdown.utils.ISessionManager;
import com.mobile16.progetto.showdown.utils.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Fragment che visualizza la lista di gruppi dell'utente
 */
public class ListFragment extends Fragment {

    private GroupAdapter adapter;
    private OnListListener listener;
    private IDBManager dbManager;
    private ISessionManager sessionManager;

    public interface OnListListener {
        void onClickAddNewGroup();
        void onGroupClicked(IAppGroup appGroup);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnListListener)
            setListener((OnListListener) activity);
    }

    // Setta il listener per questa classe
    public void setListener(OnListListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);

        adapter = new GroupAdapter(getActivity());
        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.onGroupClicked(adapter.getItem(position));
                }
            }
        });

        view.findViewById(R.id.addNewGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickAddNewGroup();
                }
            }
        });

        // Ricavo i gruppi dell'utente dal server, e li aggiungo alla listView
        dbManager = new DBManager(getActivity());
        sessionManager = new SessionManager(getActivity());
        DBCallback dbCallback = new DBCallback() {
            @Override
            public void onSuccess(Map<String, String> serverResponse) {
                List<IAppGroup> orderedList = new ArrayList<>();
                int i = 1;
                while(serverResponse.containsKey("nome_gruppo" + i)){
                    orderedList.add(new AppGroup(serverResponse.get("nome_gruppo" + i),
                            serverResponse.get("descrizione" + i),
                            Integer.parseInt(serverResponse.get("id" + i)),
                            serverResponse.get("lastModified" + i)));
                    i++;
                }

                Collections.sort(orderedList, new Comparator<IAppGroup>() {
                    @Override
                    public int compare(IAppGroup lhs, IAppGroup rhs) {
                        return rhs.getLastModifiedDate().compareTo(lhs.getLastModifiedDate());
                    }
                });
                for (IAppGroup group : orderedList){
                    adapter.add(group);
                }
            }

            @Override
            public void onFailure(Map<String, String> serverResponse) {
                Toast.makeText(getActivity(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
            }
        };
        dbManager.getGroupsForUser(sessionManager.getLoggedUser(), dbCallback);

        return view;
    }

    // Aggiorna la lista di gruppi presente nella view (da server!)
    public void updateGroupList() {
        DBCallback dbCallback = new DBCallback() {
            @Override
            public void onSuccess(Map<String, String> serverResponse) {

                List<IAppGroup> orderedList = new ArrayList<>();
                int i = 1;
                while(serverResponse.containsKey("nome_gruppo" + i)){
                    orderedList.add(new AppGroup(serverResponse.get("nome_gruppo" + i),
                                    serverResponse.get("descrizione" + i),
                                    Integer.parseInt(serverResponse.get("id" + i)),
                                    serverResponse.get("lastModified" + i)));
                    i++;
                }

                Collections.sort(orderedList, new Comparator<IAppGroup>() {
                    @Override
                    public int compare(IAppGroup lhs, IAppGroup rhs) {
                        return rhs.getLastModifiedDate().compareTo(lhs.getLastModifiedDate());
                    }
                });
                adapter.removeAll();

                for (IAppGroup group : orderedList){
                    adapter.add(group);
                }
            }

            @Override
            public void onFailure(Map<String, String> serverResponse) {
                Toast.makeText(getActivity(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
            }
        };
        dbManager.getGroupsForUser(sessionManager.getLoggedUser(), dbCallback);
        adapter.notifyDataSetChanged();
    }


    // Adapter per lista di gruppi
    private class GroupAdapter extends ArrayAdapter<IAppGroup> {

        private LayoutInflater inflater;
        private List<IAppGroup> groupList;

        public GroupAdapter(Context context) {
            super(context, 0);
            inflater = LayoutInflater.from(context);
            groupList = new ArrayList<>();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.item_group, parent, false);

            IAppGroup group = getItem(position);
            ((TextView) convertView.findViewById(R.id.title_group)).setText(group.getGroupName());
            ((TextView) convertView.findViewById(R.id.desc_group)).setText(group.getGroupDescription());
            ((TextView) convertView.findViewById(R.id.time)).setText(group.getLastModifiedDate());

            return convertView;
        }

        @Override
        public void add(IAppGroup group) {
            if (!groupList.contains(group)){
                super.add(group);
                groupList.add(group);
            }
        }

        public void removeAll(){
            for(IAppGroup group : groupList){
                super.remove(group);
            }
            groupList.clear();
        }
    }
}