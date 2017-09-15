package com.mobile16.progetto.showdown.single_group_menu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile16.progetto.showdown.R;

import com.mobile16.progetto.showdown.model.Debt;
import com.mobile16.progetto.showdown.model.IAppGroup;
import com.mobile16.progetto.showdown.model.IDebt;
import com.mobile16.progetto.showdown.model.ITransaction;
import com.mobile16.progetto.showdown.model.Transaction;
import com.mobile16.progetto.showdown.model.TransactionState;
import com.mobile16.progetto.showdown.utils.ISessionManager;
import com.mobile16.progetto.showdown.utils.SessionManager;
import com.mobile16.progetto.showdown.utils.db_connection.DBCallback;
import com.mobile16.progetto.showdown.utils.db_connection.DBManager;

import java.util.Map;

/**
 * This class represent a Generic list Fragment that will be used for show Pending, Closed, Active and Taken
 * Transictions.
 */
public class GenericListFragment extends Fragment {

    private static final String GROUP = "GROUP";
    private static final String STATE = "STATE";

    private OnGenericListListener listener;
    private GenericListAdapter adapter;
    private TransactionState state;
    private IAppGroup currentGroup;
    private DBManager dbManager;
    private ISessionManager sessionManager;


    /**
     * newInstance() static method, i'm passing any parameters via arguments (bundle)
     * The best practise for init a fragment with params.
     *
     * @param group the current group
     * @param state the state of the items to show
     * @return fragment the fragment
     */
    public static GenericListFragment newInstance(IAppGroup group, TransactionState state){
        final GenericListFragment fragment = new GenericListFragment();
        final Bundle bundle = new Bundle(2);
        bundle.putParcelable(GROUP, group);
        bundle.putSerializable(STATE, state);
        fragment.setArguments(bundle);

        return fragment;
    }


    /**
     * Il listener per il click sul singolo elemento della listview.
     * Questa interfaccia deve essere implementata da tutte le activity che vogliono usare questo
     * fragment.
     */
    public interface OnGenericListListener {
        void onItemClicked(ITransaction transaction);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnGenericListListener)
            setListener((OnGenericListListener) activity);
    }

    /**
     * setting the listener
     * @param listener the listener
     */
    public void setListener(GenericListFragment.OnGenericListListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getting the fragment values from the bundle
        this.currentGroup = getArguments().getParcelable(GROUP);
        this.state = (TransactionState) getArguments().getSerializable(STATE);

        this.sessionManager = new SessionManager(getActivity());
        this.dbManager = new DBManager(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generic_list, container, false);

        //getting the title from the enumeration TrasactionState
        ((TextView) view.findViewById(R.id.title_state)).setText(this.state.getTitle());

        adapter = new GenericListAdapter(getActivity());
        ListView listView = (ListView) view.findViewById(R.id.genericListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null)
                    listener.onItemClicked(adapter.getItem(position));
            }
        });
        this.getDataFromServer(this.state);

        return view;
    }


    /*
     * Questo metodo setta i campi della lista a seconda di quale sia lo stato
     * della TransactioState corrente, prendendoli dal server, chiamando l'opportuno metodo
     */
    private void getDataFromServer(TransactionState state){

        switch (state){
            /**
             * Get Pending Transactions from server in case TransactionState == PENDING
             */
            case PENDING:
                DBCallback dbCallbackPending = new DBCallback() {
                    @Override
                    public void onSuccess(Map<String, String> serverResponse) {
                        int i = 1;
                        while(serverResponse.containsKey("id_transazione" + i)){
                            ITransaction transaction = new Transaction(serverResponse.get("tipo"+i),
                                    serverResponse.get("dettagli"+i),
                                    serverResponse.get("apertura_ts"+i));
                            transaction.setID(serverResponse.get("id_transazione" + i));
                            transaction.setTransactionState(TransactionState.PENDING);
                            adapter.add(transaction);
                            i++;
                        }
                        //No transactions found
                        if(i==1){
                            Toast.makeText(getActivity(), R.string.nessuna_transazione, Toast.LENGTH_SHORT ).show();
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Map<String, String> serverResponse) {
                        Toast.makeText(getActivity(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
                    }
                };
                dbManager.getPendingTransactions(this.currentGroup, dbCallbackPending);
                break;

            /**
             * Getting the taken transactions from server in case TransactionState == TAKEN
             */
            case TAKEN:
                DBCallback dbCallbackTaken = new DBCallback() {
                    @Override
                    public void onSuccess(Map<String, String> serverResponse) {
                        int i = 1;
                        while(serverResponse.containsKey("id_transazione" + i)){
                            ITransaction transaction = new Transaction(serverResponse.get("tipo"+i),
                                    serverResponse.get("dettagli"+i),
                                    serverResponse.get("apertura_ts"+i));
                            transaction.setID(serverResponse.get("id_transazione" + i));
                            transaction.setTransactionState(TransactionState.TAKEN);
                            adapter.add(transaction);
                            i++;
                        }
                        //No transactions found
                        if(i==1){
                            Toast.makeText(getActivity(), R.string.nessuna_transazione, Toast.LENGTH_SHORT ).show();
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Map<String, String> serverResponse) {
                        Toast.makeText(getActivity(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
                    }
                };
                dbManager.getTakenTransactions(this.currentGroup, this.sessionManager.getLoggedUser(), dbCallbackTaken);
                break;

            /**
             * Getting Active Transactions form Server in case TransactionState == ACTIVE
             */
            case ACTIVE:
                DBCallback dbCallbackActive = new DBCallback() {
                    @Override
                    public void onSuccess(Map<String, String> serverResponse){
                        int i = 1;
                        while(serverResponse.containsKey("id_debito" + i)){
                            ITransaction transaction = new Debt();
                            transaction.setType(serverResponse.get("tipo"+i));
                            transaction.setDetails(serverResponse.get("dettagli"+i));
                            transaction.setTransactionState(TransactionState.ACTIVE);
                            ((Debt)transaction).setCreditoreId(serverResponse.get("creditore_id"+i));
                            ((Debt)transaction).setCreditoreUsername(serverResponse.get("creditore_username"+i));
                            ((Debt)transaction).setDebitoreId(serverResponse.get("debitore_id"+i));
                            ((Debt)transaction).setDebitoreUsername(serverResponse.get("debitore_username"+i));
                            ((Debt)transaction).setDebtId(serverResponse.get("id_debito"+i));
                            ((Debt)transaction).setAmmontare(Float.parseFloat(serverResponse.get("ammontare"+i)));
                            adapter.add(transaction);
                            i++;
                        }
                        //No transactions found
                        if(i==1){
                            Toast.makeText(getActivity(), R.string.nessuna_transazione, Toast.LENGTH_SHORT ).show();
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Map<String, String> serverResponse) {
                        Toast.makeText(getActivity(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
                    }
                };
                dbManager.getActiveTransaction(this.sessionManager.getLoggedUser(), this.currentGroup, dbCallbackActive);
                break;

            /**
             * Getting Closed Transactions from Server in case TransactionState == CLOSED
             */
            case CLOSED:
                DBCallback dbCallbackClose = new DBCallback() {
                    @Override
                    public void onSuccess(Map<String, String> serverResponse){
                        int i = 1;
                        while(serverResponse.containsKey("id_debito" + i)){
                            ITransaction transaction = new Debt();
                            transaction.setType(serverResponse.get("tipo"+i));
                            transaction.setDetails(serverResponse.get("dettagli"+i));
                            transaction.setTransactionState(TransactionState.CLOSED);
                            ((Debt)transaction).setCreditoreId(serverResponse.get("creditore_id"+i));
                            ((Debt)transaction).setCreditoreUsername(serverResponse.get("creditore_username"+i));
                            ((Debt)transaction).setDebitoreId(serverResponse.get("debitore_id"+i));
                            ((Debt)transaction).setDebitoreUsername(serverResponse.get("debitore_username"+i));
                            ((Debt)transaction).setDebtId(serverResponse.get("id_debito"+i));
                            ((Debt)transaction).setAmmontare(Float.parseFloat(serverResponse.get("ammontare"+i)));
                            ((Debt)transaction).setDataChiusura(serverResponse.get("saldo_ts"+i));
                            adapter.add(transaction);
                            i++;
                        }
                        //No transactions found
                        if(i==1){
                            Toast.makeText(getActivity(), R.string.nessuna_transazione, Toast.LENGTH_SHORT ).show();
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Map<String, String> serverResponse) {
                        Toast.makeText(getActivity(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
                    }
                };
                dbManager.getClosedTransaction(this.sessionManager.getLoggedUser(), this.currentGroup, dbCallbackClose);
                break;
        }

    }

    /**
     * L'adapter che mi modella la listview generica
     */
    private class GenericListAdapter extends ArrayAdapter<ITransaction>{

        private LayoutInflater inflater;

        public GenericListAdapter(Context context) {
            super(context, 0);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = inflater.inflate(R.layout.item_single_generic_listfragment, parent, false);
            }

            final ITransaction transaction = getItem(position);
            final TextView transaction_typeTw = ((TextView) convertView.findViewById(R.id.listview_item_text));
            transaction_typeTw.setText(transaction.getType());

            //String di appoggio che utilizzo per inserire il nome del creditore
            //o debitore di fiaco al nome della transazione se mi trovo all'interno
            //dell'activity che mostra le transizione attive e le transazioni chiuse
            String textTw;

            //Nel caso mi trovo nello stato chiuso o attivo, aggiungo nella lista delle transazioni
            // un flag che mi dice se devo dare o ricevere dei soldi da quella specifica transizione
            // scrivendo: CREDI o DEBIT rispettivamnte se devo ricevere o dare.
            final TextView active_closed = ((TextView) convertView.findViewById(R.id.text_active_closed));
            active_closed.setVisibility(View.GONE);
            //Controllo lo stato in cui si trova il fragment corrisponde alle transizione attive
            // o alle transizioni chiuse
            if(GenericListFragment.this.state == TransactionState.CLOSED ||
                    GenericListFragment.this.state == TransactionState.ACTIVE){
                active_closed.setVisibility(View.VISIBLE);
                //Controllo se l'utente attualmente loggato corrisponde al creditore della transazione
                if(((IDebt)transaction).getCreditoreId().equals(
                        sessionManager.getLoggedUser().getUserId().toString())) {
                    //prendo il nome della transizione dalla textview
                    textTw = ((TextView) convertView.findViewById(R.id.listview_item_text)).
                            getText().toString();
                    //Inserisce il nome del debitore di fianco al nome della transazione
                    textTw = textTw + " (" + ((IDebt)transaction).getDebitoreUsername() + ")";
                    transaction_typeTw.setText(textTw);

                    //imposto la text view come credito
                    active_closed.setText(R.string.credito);
                    active_closed.setTextColor(Color.GREEN);
                }else{
                    //prendo il nome della transizione dalla textview
                    textTw = ((TextView) convertView.findViewById(R.id.listview_item_text))
                            .getText().toString();
                    //Inserisce il nome del creditore di fianco al nome della transizione
                    textTw = textTw + " (" + ((IDebt)transaction).getCreditoreUsername() + ")";
                    transaction_typeTw.setText(textTw);

                    //imposto la textview come debito
                    active_closed.setText(R.string.debito);
                    active_closed.setTextColor(Color.RED);
                }
            }
            return convertView;
        }
    }
}
