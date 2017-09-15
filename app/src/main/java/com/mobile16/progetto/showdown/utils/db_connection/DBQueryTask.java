package com.mobile16.progetto.showdown.utils.db_connection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Esegue una singola task di query sul database. Implementato come AsyncTask.
 */
public class DBQueryTask extends AsyncTask<Map<String, String>, Void, Void> {

    private final URL server_url;
    private final DBCallback dbCallback;

    private final Map<String, String> serverResponse;
    private ProgressDialog progressDialog;

    public DBQueryTask(URL server_url, Context context, DBCallback dbCallback){
        this.server_url = server_url;
        this.dbCallback = dbCallback;
        this.serverResponse = new HashMap<>();
        this.progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute(){
        progressDialog.setMessage("Thinking...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    @SuppressWarnings("all")
    protected Void doInBackground(Map<String, String>... params) {
        try {
            // Creo una connessione al server con metodo post
            HttpURLConnection connection = (HttpURLConnection)server_url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Creo l'oggetto JSON da inviare al server
            JSONObject jsonObject = new JSONObject();
            for(Map<String, String> map : params){
                Iterator it = map.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry entry = (Map.Entry)it.next();
                    try {
                        jsonObject.accumulate(entry.getKey().toString(), entry.getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Invio la richiesta al server
            DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
            dStream.writeBytes(jsonObject.toString());
            dStream.flush();
            dStream.close();

            // Risposta dal server
            int responseCode = connection.getResponseCode();
            if(responseCode == 200){
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                while((line = br.readLine()) != null ) {
                    try {
                        // Scompongo la risposta ricevuta dal server
                        JSONObject json = new JSONObject(line);
                        Iterator it = json.keys();
                        while(it.hasNext()){
                            String p = it.next().toString();
                            serverResponse.put(p, json.get(p).toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                br.close();
            } else{
                // Errore dalla parte del server
                serverResponse.put("ok", "no");
                serverResponse.put("error", "server error");
            }

        } catch (IOException e) {
            // Errore di rete
            serverResponse.put("ok", "no");
            serverResponse.put("error", "connection unavailable");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v){
        progressDialog.dismiss();

        // Richiamo la callback, in caso positivo onSuccess e negativo onFailure.
        // Come argomento passo i dati ricevuti come risposta dal server
        if(serverResponse.get("ok").equals("ok")){
            dbCallback.onSuccess(serverResponse);
        } else{
            dbCallback.onFailure(serverResponse);
        }
    }
}


