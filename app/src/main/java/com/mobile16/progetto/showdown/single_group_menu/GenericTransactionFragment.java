package com.mobile16.progetto.showdown.single_group_menu;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile16.progetto.showdown.R;
import com.mobile16.progetto.showdown.model.Debt;
import com.mobile16.progetto.showdown.model.ITransaction;
import com.mobile16.progetto.showdown.utils.ISessionManager;
import com.mobile16.progetto.showdown.utils.SessionManager;

/**
 * Implementa un fragment completamente riutilizzabile in tutte le differenti activity di
 * visualizzazione di una transazione di un gruppo. La sua view si modella in base allo stato
 * della transazione da visualizzare
 */
public class GenericTransactionFragment extends Fragment {

    private EditText typeET;
    private EditText detailsET;
    private EditText costET;

    private ITransaction currentTransaction;
    private static final String TRANSACTION = "TRANSACTION";

    // Listener del fragment
    private OnFragmentTransactionListener mListener;

    // Listener per la pressione del bottone sul fragment
    public interface OnFragmentTransactionListener{
        void onButtonClicked(ITransaction transaction, String button);
    }

    /**
     * Restituisce un'istanza dello'oggetto, passando come extra la transazione ricevuta
     */
    public static GenericTransactionFragment newInstance(ITransaction transaction){
        final GenericTransactionFragment fragment = new GenericTransactionFragment();
        final Bundle bundle = new Bundle(1);
        bundle.putParcelable(TRANSACTION, transaction);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // recupero la transazione dagli extra parcelable
        currentTransaction = getArguments().getParcelable(TRANSACTION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_generic_transaction, container, false);

        ISessionManager sessionManager = new SessionManager(view.getContext());

        Button button1 = (Button)view.findViewById(R.id.genericButton);
        Button button2 = (Button)view.findViewById(R.id.genericButton2);
        typeET = (EditText)view.findViewById(R.id.typeET);
        detailsET = (EditText)view.findViewById(R.id.detailET);
        costET = (EditText)view.findViewById(R.id.costET);
        TextView costTW = (TextView)view.findViewById(R.id.costTW);

        //
        //    CREO la view, riutilizzabile e differente per ogni stato in cui la transazione da
        //                  visualizzare si trova!
        //

        switch (currentTransaction.getTransactionState()){
            case IN_CREATION:
                button1.setText(R.string.crea_transazione);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String type = typeET.getText().toString();
                        String details = detailsET.getText().toString();
                        if(type.length() > 0 && details.length() > 0) {
                            currentTransaction.setType(type);
                            currentTransaction.setDetails(details);
                            mListener.onButtonClicked(currentTransaction, "button1");
                        } else{
                            Toast.makeText(view.getContext(), R.string.inserisci_campi, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                button2.setVisibility(View.GONE);
                costTW.setVisibility(View.GONE);
                costET.setVisibility(View.GONE);
                break;

            case PENDING:
                typeET.setFocusable(false);
                typeET.setEnabled(false);
                typeET.setCursorVisible(false);
                typeET.setBackgroundColor(Color.TRANSPARENT);
                typeET.setText(currentTransaction.getType());

                detailsET.setText(currentTransaction.getDetails());

                costTW.setVisibility(View.GONE);
                costET.setVisibility(View.GONE);

                button1.setText(R.string.modifica_transazione);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String details = detailsET.getText().toString();
                        currentTransaction.setDetails(details);
                        mListener.onButtonClicked(currentTransaction, "button1");
                    }
                });

                button2.setText(R.string.prendi_in_carico_trans);
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String details = detailsET.getText().toString();
                        currentTransaction.setDetails(details);
                        mListener.onButtonClicked(currentTransaction, "button2");
                    }
                });
                break;

            case TAKEN:
                typeET.setFocusable(false);
                typeET.setEnabled(false);
                typeET.setCursorVisible(false);
                typeET.setBackgroundColor(Color.TRANSPARENT);
                typeET.setText(currentTransaction.getType());

                detailsET.setFocusable(false);
                detailsET.setEnabled(false);
                detailsET.setCursorVisible(false);
                detailsET.setBackgroundColor(Color.TRANSPARENT);
                detailsET.setText(currentTransaction.getDetails());

                button1.setText(R.string.conferma_transazione);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(costET.getText().length() == 0 || Float.parseFloat(costET.getText().toString()) <= 0){
                            Toast.makeText(getActivity(), R.string.inserisci_dati_validi, Toast.LENGTH_SHORT).show();
                        } else {
                            Float details = Float.parseFloat(costET.getText().toString());
                            currentTransaction.setCost(details);
                            mListener.onButtonClicked(currentTransaction, "button1");
                        }
                    }
                });

                button2.setVisibility(View.GONE);
                break;

            case ACTIVE:
                typeET.setFocusable(false);
                typeET.setEnabled(false);
                typeET.setCursorVisible(false);
                typeET.setBackgroundColor(Color.TRANSPARENT);
                typeET.setText(currentTransaction.getType());

                detailsET.setFocusable(false);
                detailsET.setEnabled(false);
                detailsET.setCursorVisible(false);
                detailsET.setBackgroundColor(Color.TRANSPARENT);
                detailsET.setText(currentTransaction.getDetails());

                Integer userId = sessionManager.getLoggedUser().getUserId();
                if(userId.toString().equals(((Debt)currentTransaction).getCreditoreId())){
                    String text = "User " + ((Debt)currentTransaction).getDebitoreUsername() +
                            " should give you " + ((Debt)currentTransaction).getAmmontare() +
                            "$. ";
                    costTW.setText(text);

                    button2.setText(R.string.ha_pagato);
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onButtonClicked(currentTransaction, "button2");
                        }
                    });
                } else{
                    String text = "You should give " + ((Debt)currentTransaction).getAmmontare() +
                            "$ to " + ((Debt)currentTransaction).getCreditoreUsername();
                    costTW.setText(text);
                    costTW.setBackgroundColor(Color.RED);

                    button2.setVisibility(View.GONE);
                }

                button1.setText(R.string.ok);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onButtonClicked(currentTransaction, "button1");
                    }
                });

                costET.setVisibility(View.GONE);
                break;

            case CLOSED:
                typeET.setFocusable(false);
                typeET.setEnabled(false);
                typeET.setCursorVisible(false);
                typeET.setBackgroundColor(Color.TRANSPARENT);
                typeET.setText(currentTransaction.getType());

                detailsET.setFocusable(false);
                detailsET.setEnabled(false);
                detailsET.setCursorVisible(false);
                detailsET.setBackgroundColor(Color.TRANSPARENT);
                detailsET.setText(currentTransaction.getDetails());

                Integer userId2 = sessionManager.getLoggedUser().getUserId();
                if(userId2.toString().equals(((Debt)currentTransaction).getCreditoreId())){
                    String text = "User " + ((Debt)currentTransaction).getDebitoreUsername() +
                            " gave you " + ((Debt)currentTransaction).getAmmontare() +
                            "$. (" + ((Debt)currentTransaction).getDataChiusura() + ") ";
                    costTW.setText(text);
                } else{
                    String text = "You gave " + ((Debt)currentTransaction).getAmmontare() +
                            "$ to " + ((Debt)currentTransaction).getCreditoreUsername()
                            + " (" + ((Debt)currentTransaction).getDataChiusura() + ") ";
                    costTW.setText(text);
                    costTW.setBackgroundColor(Color.RED);
                }

                button1.setText(R.string.ok);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onButtonClicked(currentTransaction, "button1");
                    }
                });
                button2.setVisibility(View.GONE);
                costET.setVisibility(View.GONE);
                break;
        }

        return view;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnFragmentTransactionListener) {
            mListener = (OnFragmentTransactionListener) getActivity();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
