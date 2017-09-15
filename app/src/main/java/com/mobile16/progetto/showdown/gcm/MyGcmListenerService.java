package com.mobile16.progetto.showdown.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.mobile16.progetto.showdown.R;
import com.mobile16.progetto.showdown.login.MainActivity;
import com.mobile16.progetto.showdown.model.AppGroup;
import com.mobile16.progetto.showdown.single_group_menu.SingleGroupMenuActivity;

/**
 * Service in background che implementa il comportamento del GCM per l'arrivo delle notifiche push.
 */
public class MyGcmListenerService extends GcmListenerService {
    private String id_intent;
    private String id_gruppo;
    private String nome_gruppo;
    private String descrizione_gruppo;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
         id_intent = data.getString("id_intent");
         id_gruppo = data.getString("id_gruppo");
         nome_gruppo = data.getString("nome_gruppo");
         descrizione_gruppo = data.getString("descrizione_gruppo");
        //Fa partire la notifica al dispositivo quando viene ricevuto un messaggio push dal server
        sendNotification(message);
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        //preparo l'intent
        final  Intent intent;
        //se il messaggio ricevuto corrisponde ad uno conosciuto (id==0) allora lancia un intent
        // al gruppo corrispondente, facendo così visualizzare all'utente la pagina relativa alla notifica
        if(this.id_intent != null && this.id_intent.equals("0")){
            intent = new Intent(this, SingleGroupMenuActivity.class);
            intent.putExtra("currentGroup", new AppGroup(this.nome_gruppo, this.descrizione_gruppo,
                    Integer.valueOf(this.id_gruppo)));
        } else {
            //altrimenti lancia un intent alla home activity al click della notifica
            intent = new Intent(this, MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Costruzione della notifica
        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("ShowDown Notification")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
