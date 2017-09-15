package com.mobile16.progetto.showdown.utils.db_connection;

import android.content.Context;

import com.mobile16.progetto.showdown.R;
import com.mobile16.progetto.showdown.model.Debt;
import com.mobile16.progetto.showdown.model.IAppGroup;
import com.mobile16.progetto.showdown.model.ITransaction;
import com.mobile16.progetto.showdown.model.IUser;
import com.mobile16.progetto.showdown.utils.ISessionManager;
import com.mobile16.progetto.showdown.utils.SessionManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Modella il comportamento di un manager per il DB sul server.
 */
@SuppressWarnings("unchecked")
public class DBManager implements IDBManager{

    private URL server_url;
    private Context context;

    private final String server_username;
    private final String server_password;

    private ISessionManager sessionManager;

    public DBManager(Context context){
        this.context = context;
        server_username = context.getString(R.string.server_username);
        server_password = context.getString(R.string.server_password);

        sessionManager = new SessionManager(context);

        try{
            server_url = new URL(context.getString(R.string.server_url));
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
    }


    /**
     * Verifica se la mail può essere inserita (deve essere univoca!)
     */
    @Override
    public void checkMailValidity(String mail, DBCallback dbCallback){
        DBQueryTask dbQueryTask = new DBQueryTask(server_url, context, dbCallback);
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("function", "checkMailValidity");
        parameters.put("DB_username", server_username);
        parameters.put("DB_password", server_password);
        parameters.put("mail", mail);
        dbQueryTask.execute(parameters);
    }

    /**
     * Insert new user in DB.
     */
    @Override
    public void insertUser(IUser user, DBCallback dbCallback){
        DBQueryTask dbQueryTask = new DBQueryTask(server_url, context, dbCallback);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("function", "insertUser");
        parameters.put("DB_username", server_username);
        parameters.put("DB_password", server_password);
        parameters.put("mail", user.getMail());
        parameters.put("password", user.getPassword());
        parameters.put("username", user.getUsername());
        dbQueryTask.execute(parameters);
    }

    /**
     * Check if the User is correct
     */
    @Override
    public void checkUserLogin(IUser user, DBCallback dbCallback){
        DBQueryTask dbQueryTask = new DBQueryTask(server_url, context, dbCallback);
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("function", "checkUserLogin");
        parameters.put("DB_username", server_username);
        parameters.put("DB_password", server_password);
        parameters.put("mail", user.getMail());
        parameters.put("password", user.getPassword());
        dbQueryTask.execute(parameters);
    }

    /**
     * Inserisce il gruppo nel DB.
     * Si assume che tutte le mail siano corrette.
     */
    @Override
    public void insertGroup(IAppGroup appGroup, DBCallback dbCallback){
        DBQueryTask dbQueryTask = new DBQueryTask(server_url, context, dbCallback);
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("function", "insertGroup");
        parameters.put("DB_username", server_username);
        parameters.put("DB_password", server_password);

        parameters.put("group_name", appGroup.getGroupName());
        parameters.put("group_description", appGroup.getGroupDescription());
        List<String> users = appGroup.getUsersList();
        int i = 1;
        for(String u : users){
            parameters.put("mail"+i, u);
            i++;
        }
        parameters.put("me_id", sessionManager.getLoggedUser().getUserId().toString());

        dbQueryTask.execute(parameters);
    }

    /**
     * Restituisce i gruppi appartenenti all'utente
     */
    public void getGroupsForUser(IUser user, DBCallback dbCallback){
        DBQueryTask dbQueryTask = new DBQueryTask(server_url, context, dbCallback);
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("function", "getGroupsForUser");
        parameters.put("DB_username", server_username);
        parameters.put("DB_password", server_password);
        parameters.put("mail", user.getMail());

        dbQueryTask.execute(parameters);
    }

    /**
     * Inserisce una nuova transazione nel DB
     */
    public void insertTransaction(ITransaction transaction, IAppGroup group, DBCallback dbCallback){
        DBQueryTask dbQueryTask = new DBQueryTask(server_url, context, dbCallback);
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("function", "insertTransaction");
        parameters.put("DB_username", server_username);
        parameters.put("DB_password", server_password);
        parameters.put("type", transaction.getType());
        parameters.put("details", transaction.getDetails());
        parameters.put("groupId", String.valueOf(group.getGroupId()));
        parameters.put("creatorId", transaction.getCreator().getUserId().toString());

        dbQueryTask.execute(parameters);
    }

    /**
     * Restituisce una lista di transazioni nello stato PENDING per il gruppo richiesto
     */
    @Override
    public void getPendingTransactions(IAppGroup group, DBCallback dbCallback){
        DBQueryTask dbQueryTask = new DBQueryTask(server_url, context, dbCallback);
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("function", "getPendingTransactions");
        parameters.put("DB_username", server_username);
        parameters.put("DB_password", server_password);
        parameters.put("groupId", group.getGroupId().toString());

        dbQueryTask.execute(parameters);
    }

    /**
     * Aggiorna la transazione passata
     */
    @Override
    public void updatePendingTransaction(ITransaction transaction, DBCallback dbCallback){
        DBQueryTask dbQueryTask = new DBQueryTask(server_url, context, dbCallback);
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("function", "updatePendingTransaction");
        parameters.put("DB_username", server_username);
        parameters.put("DB_password", server_password);
        parameters.put("transactionId", transaction.getID());
        parameters.put("details", transaction.getDetails());

        dbQueryTask.execute(parameters);
    }

    /**
     * Prende in carico la transazione
     */
    @Override
    public void takeInChargeTransaction(ITransaction transaction, IUser currentUser, DBCallback dbCallback){
        DBQueryTask dbQueryTask = new DBQueryTask(server_url, context, dbCallback);
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("function", "takeInChargeTransaction");
        parameters.put("DB_username", server_username);
        parameters.put("DB_password", server_password);
        parameters.put("transactionId", transaction.getID());
        parameters.put("userId", currentUser.getUserId().toString());

        dbQueryTask.execute(parameters);
    }

    /**
     * Restituisce una lista di transazioni nello stato TAKEN per il gruppo richiesto
     */
    @Override
    public void getTakenTransactions(IAppGroup group, IUser currentUser, DBCallback dbCallback){
        DBQueryTask dbQueryTask = new DBQueryTask(server_url, context, dbCallback);
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("function", "getTakenTransactions");
        parameters.put("DB_username", server_username);
        parameters.put("DB_password", server_password);
        parameters.put("groupId", group.getGroupId().toString());
        parameters.put("userId", currentUser.getUserId().toString());

        dbQueryTask.execute(parameters);
    }

    /**
     * Aggiorna la transazione appena conclusa con il costo finale della stessa
     */
    public void completeTransaction(ITransaction transaction, DBCallback dbCallback){
        DBQueryTask dbQueryTask = new DBQueryTask(server_url, context, dbCallback);
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("function", "completeTransaction");
        parameters.put("DB_username", server_username);
        parameters.put("DB_password", server_password);
        parameters.put("transactionId", transaction.getID());
        parameters.put("cost", transaction.getCost().toString());

        dbQueryTask.execute(parameters);
    }

    /**
     * Restituisce le transazioni attive per lo specifico utente nel gruppo
     */
    @Override
    public void getActiveTransaction(IUser user, IAppGroup group, DBCallback dbCallback){
        DBQueryTask dbQueryTask = new DBQueryTask(server_url, context, dbCallback);
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("function", "getActiveTransaction");
        parameters.put("DB_username", server_username);
        parameters.put("DB_password", server_password);
        parameters.put("userId", user.getUserId().toString());
        parameters.put("groupId", group.getGroupId().toString());

        dbQueryTask.execute(parameters);
    }

    /**
     * Chiude la transazione passata
     */
    @Override
    public void closeTransaction(ITransaction transaction, DBCallback dbCallback){
        DBQueryTask dbQueryTask = new DBQueryTask(server_url, context, dbCallback);
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("function", "closeTransaction");
        parameters.put("DB_username", server_username);
        parameters.put("DB_password", server_password);
        parameters.put("id_debito", ((Debt)transaction).getDebtId());

        dbQueryTask.execute(parameters);
    }

    /**
     * Restituisce le transazioni chiuse per lo specifico utente nel gruppo
     */
    @Override
    public void getClosedTransaction(IUser user, IAppGroup group, DBCallback dbCallback){
        DBQueryTask dbQueryTask = new DBQueryTask(server_url, context, dbCallback);
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("function", "getClosedTransaction");
        parameters.put("DB_username", server_username);
        parameters.put("DB_password", server_password);
        parameters.put("userId", user.getUserId().toString());
        parameters.put("groupId", group.getGroupId().toString());

        dbQueryTask.execute(parameters);
    }

    /**
     * Restituisce gli utenti presenti nel gruppo
     */
    @Override
    public void getUsersInGroup(IAppGroup group, DBCallback dbCallback){
        DBQueryTask dbQueryTask = new DBQueryTask(server_url, context, dbCallback);
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("function", "getUsersInGroup");
        parameters.put("DB_username", server_username);
        parameters.put("DB_password", server_password);
        parameters.put("groupId", group.getGroupId().toString());

        dbQueryTask.execute(parameters);
    }

    /**
     * Rimuove l'utente dal gruppo
     */
    @Override
    public void leaveGroup(IUser user, IAppGroup group, DBCallback dbCallback){
        DBQueryTask dbQueryTask = new DBQueryTask(server_url, context, dbCallback);
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("function", "leaveGroup");
        parameters.put("DB_username", server_username);
        parameters.put("DB_password", server_password);
        parameters.put("groupId", group.getGroupId().toString());
        parameters.put("userId", user.getUserId().toString());

        dbQueryTask.execute(parameters);
    }
}
