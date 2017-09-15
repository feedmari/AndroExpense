package com.mobile16.progetto.showdown.utils.db_connection;

import com.mobile16.progetto.showdown.model.IAppGroup;
import com.mobile16.progetto.showdown.model.ITransaction;
import com.mobile16.progetto.showdown.model.IUser;

import java.util.Iterator;

/**
 * Interfaccia che modella il comportamento di un manager per il DB sul server.
 */
public interface IDBManager {
    /**
     * Controlla se la mail dell'utente da inserire nel db è valida(deve essere unica)
     */
    void checkMailValidity(String mail, DBCallback dbCallback);

    /**
     * Inserisce l'utente nel database
     */
    void insertUser(IUser user, DBCallback dbCallback);

    /**
     * Controlla se l'utente è presente nel database e se la password è corretta
     */
    void checkUserLogin(IUser user, DBCallback dbCallback);

    /**
     * Inserisce un nuovo gruppo nel database
     */
    void insertGroup(IAppGroup appGroup, DBCallback dbCallback);

    /**
     * Restituisce i gruppi appartenenti all'utente
     */
    void getGroupsForUser(IUser user, DBCallback dbCallback);

    /**
     * Inserisce una nuova transazione nel DB
     */
    void insertTransaction(ITransaction transaction, IAppGroup group, DBCallback dbCallback);

    /**
     * Restituisce una lista di transazioni nello stato PENDING per il gruppo richiesto
     */
    void getPendingTransactions(IAppGroup group, DBCallback dbCallback);

    /**
     * Aggiorna la transazione passata
     */
    void updatePendingTransaction(ITransaction transaction, DBCallback dbCallback);

    /**
     * Prende in carico la transazione
     */
    void takeInChargeTransaction(ITransaction transaction, IUser currentUser, DBCallback dbCallback);

    /**
     * Restituisce una lista di transazioni nello stato TAKEN per il gruppo richiesto
     */
    void getTakenTransactions(IAppGroup group, IUser currentUser, DBCallback dbCallback);

    /**
     * Aggiorna la transazione appena conclusa con il costo finale della stessa
     */
    void completeTransaction(ITransaction transaction, DBCallback dbCallback);

    /**
     * Restituisce le transazioni attive per lo specifico utente nel gruppo
     */
    void getActiveTransaction(IUser user, IAppGroup group, DBCallback dbCallback);

    /**
     * Chiude la transazione passata
     */
    void closeTransaction(ITransaction transaction, DBCallback dbCallback);

    /**
     * Restituisce le transazioni chiuse per lo specifico utente nel gruppo
     */
    void getClosedTransaction(IUser user, IAppGroup group, DBCallback dbCallback);

    /**
     * Restituisce gli utenti presenti nel gruppo passato
     */
    void getUsersInGroup(IAppGroup group, DBCallback dbCallback);

    /**
     * Rimuove l'utente dal gruppo
     */
    void leaveGroup(IUser user, IAppGroup group, DBCallback dbCallback);
}
