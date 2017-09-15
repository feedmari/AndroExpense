package com.mobile16.progetto.showdown.utils.db_connection;

import java.util.Map;

/**
 * Oggetto da passare alla DBQueryTask. Nel caso in cui tutto sia andato bene, il task richiamerà
 * il metodo onSuccess passandogli i dati ricevuti dal server, onFailure altrimenti, con i dati
 * del server e l'eventuale errore prodotto.
 */
public interface DBCallback {
    void onSuccess(Map<String, String> serverResponse);
    void onFailure(Map<String, String> serverResponse);
}
