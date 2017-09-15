package com.mobile16.progetto.showdown.login;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mobile16.progetto.showdown.login.login_fragments.LoginFragment;
import com.mobile16.progetto.showdown.login.login_fragments.RegistrationFragment;
import com.mobile16.progetto.showdown.R;

/**
 * Classe utilizzata per il login degli utenti.
 */
public class MainActivity extends AppCompatActivity
        implements LoginFragment.OnBtnRegClicked, RegistrationFragment.OnBtnLoginClicked{

    private Fragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        // Setto il fragment iniziale
        this.fragment = new LoginFragment();
        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fm.replace(R.id.container_login_activity, fragment);
        fm.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    /*
     * Metodo da implementare per far sì che questa classe sia il listener del RegistrationFragment.
     * Alla pressione del bottone di login, cambio il fragment.
     */
    @Override
    public void onLoginClicked() {
        Fragment fragment = new LoginFragment();
        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fm.replace(R.id.container_login_activity, fragment);
        fm.commit();
    }

    /*
     * Metodo da implementare per far sì che questa classe sia il listener del LoginFragment. Alla
     * pressione del bottone di registrazione, cambio il fragment.
     */
    @Override
    public void onRegClicked() {
        Fragment fragment = new RegistrationFragment();
        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fm.replace(R.id.container_login_activity, fragment);
        fm.commit();
    }
}
