package com.mobile16.progetto.showdown.single_group_menu;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mobile16.progetto.showdown.R;
import com.mobile16.progetto.showdown.single_group_menu.active_transaction.ActiveTransactionActivity;
import com.mobile16.progetto.showdown.single_group_menu.closed_transaction.ClosedTransactionActivity;
import com.mobile16.progetto.showdown.single_group_menu.create_debit.CreateDebitActivity;
import com.mobile16.progetto.showdown.model.IAppGroup;
import com.mobile16.progetto.showdown.single_group_menu.pending_transaction.PendingTransactionActivity;
import com.mobile16.progetto.showdown.single_group_menu.single_group_settings.SingleGroupSettingsActivity;
import com.mobile16.progetto.showdown.single_group_menu.taken_in_charge.TakenInChargeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity che gestisce il menu del singolo gruppo.
 */
public class SingleGroupMenuActivity extends AppCompatActivity {
    public static final int ACTIVITY_FOR_RESULT_ID = 1;

    private IAppGroup currentGroup;

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        TextView groupName = (TextView)findViewById(R.id.groupName);
        TextView groupDescription = (TextView)findViewById(R.id.groupDescription);
        currentGroup = getIntent().getParcelableExtra("currentGroup");
        groupName.setText(currentGroup.getGroupName());
        groupDescription.setText(currentGroup.getGroupDescription());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(SingleGroupMenuActivity.this, 2);
        RecyclerView rw = (RecyclerView) findViewById(R.id.rw);
        rw.setLayoutManager(gridLayoutManager);
        rw.setHasFixedSize(true);
        List<MenuItem> allItems = getMenuItems();

        MenuGroupAdapter customAdapter = new MenuGroupAdapter(allItems);
        rw.setAdapter(customAdapter);
    }

    /*
     *  Restituisce tutti i menù da aggiungere, con relativi colori di sfondo, immagini ed
     *  azioni da compiere in caso di click.
     */
    private List<MenuItem> getMenuItems(){
        List<MenuItem> items = new ArrayList<>();

        // CREA DEBITO
        items.add(new MenuItem(R.drawable.create_debit,
                getString(R.string.crea_debito),
                Color.rgb(237, 217, 138),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SingleGroupMenuActivity.this,
                                CreateDebitActivity.class);
                        intent.putExtra("currentGroup", currentGroup);
                        startActivity(intent);
                    }
                }));


        // TRANSAZIONI IN SOSPESO
        items.add(new MenuItem(R.drawable.pending_transaction,
                getString(R.string.transazioni_in_sospeso),
                Color.rgb(138, 237, 224),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SingleGroupMenuActivity.this,
                                PendingTransactionActivity.class);
                        intent.putExtra("currentGroup", currentGroup);
                        startActivity(intent);
                    }
                }));

        // PRESA IN CARICO
        items.add(new MenuItem(R.drawable.take_charge,
                getString(R.string.prese_in_carico),
                Color.rgb(138, 149, 237),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SingleGroupMenuActivity.this,
                                TakenInChargeActivity.class);
                        intent.putExtra("currentGroup", currentGroup);
                        startActivity(intent);
                    }
                }));

        // TRANSAZIONI ATTIVE
        items.add(new MenuItem(R.drawable.active_transaction,
                getString(R.string.transazioni_attive),
                Color.rgb(157, 237, 138),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SingleGroupMenuActivity.this,
                                ActiveTransactionActivity.class);
                        intent.putExtra("currentGroup", currentGroup);
                        startActivity(intent);
                    }
                }));

        // TRANSAZIONI CHIUSE
        items.add(new MenuItem(R.drawable.closed_transaction,
                getString(R.string.transazioni_chiuse),
                Color.rgb(255, 168, 168),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SingleGroupMenuActivity.this,
                                ClosedTransactionActivity.class);
                        intent.putExtra("currentGroup", currentGroup);
                        startActivity(intent);
                    }
                }));

        // IMPOSTAZIONI GRUPPO (single_group_settings)
        items.add(new MenuItem(R.drawable.group_detail,
                getString(R.string.dettagli_gruppo),
                Color.rgb(211, 138, 237),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SingleGroupMenuActivity.this,
                                SingleGroupSettingsActivity.class);
                        intent.putExtra("currentGroup", currentGroup);

                        // E' necessario far partire questa activity come activity result, poichè
                        // è presente l'opzione per l'abbandono al gruppo corrente.
                        // Nel caso in cui questo gruppo venga abbandonato, devo agire differentemente
                        startActivityForResult(intent, ACTIVITY_FOR_RESULT_ID);
                    }
                }));

        return items;
    }

    // Gestisco il caso in cui nell'activity per le impostazioni del gruppo, questo gruppo
    // venga abbandonato dall'utente. Semplicemente esco dalla schermata di questo gruppo.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == ACTIVITY_FOR_RESULT_ID){
            setResult(ACTIVITY_FOR_RESULT_ID);
            finish();
        }
    }
}
