package com.mobile16.progetto.showdown.login.login_fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.mobile16.progetto.showdown.R;
import com.mobile16.progetto.showdown.home.HomeActivity;
import com.mobile16.progetto.showdown.model.User;
import com.mobile16.progetto.showdown.utils.SessionManager;
import com.mobile16.progetto.showdown.utils.db_connection.DBCallback;
import com.mobile16.progetto.showdown.utils.db_connection.DBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;

/**
 * Classe che implementa la form di registrazione.
 */
public class RegistrationFragment extends Fragment {

    private DBManager dbManager;
    private CallbackManager callbackManager;
    private Bundle bFacebookData;
    private SessionManager session;
    private OnBtnLoginClicked listener;
    private EditText usernameEt, emailEt, passwordEt;


    /**
     * Interfaccia che serve per intercettare il click sul pulsante per passare nel LoginFragment
     */
    public interface OnBtnLoginClicked {
        void onLoginClicked();
    }

    @Override@SuppressWarnings("deprecation")

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnBtnLoginClicked)
            setListener((OnBtnLoginClicked) activity);
    }

    /**
     * Setta il listener per questo fragment.
     */
    public void setListener(OnBtnLoginClicked listener) {
        this.listener = listener;
    }

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Inizializzo l'sdk di facebook
        FacebookSdk.sdkInitialize(getActivity());
        //Dopo aver inizializzato l'sdk posso impostare il layout
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        this.usernameEt = ((EditText) view.findViewById(R.id.name));
        this.emailEt = ((EditText) view.findViewById(R.id.listview_item_text));
        this.passwordEt = ((EditText)view.findViewById(R.id.password));

        //Init the SessionManager
        this.session = new SessionManager(getActivity());

        //Init DBManager
        this.dbManager = new DBManager(getActivity());

        // Setting facebook login/registration button
        AppEventsLogger.activateApp(getActivity());
        this.bFacebookData = new Bundle();  //all data will be save into this bundle
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.facebook_login);
        //Setting user permission for get only main and user public name on facebook
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        //Callback registration
        this.callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                bFacebookData = getFacebookData(object);
                                final DBCallback dbCallback = new DBCallback(){
                                    @Override
                                    public void onSuccess(Map<String, String> serverResponse){

                                        if(serverResponse.get("valida").equals("si")){
                                            // Nel caso in cui l'email sia valida, procedo inserendo il nuovo utente nel DB
                                            final User currentUser = new User(String.valueOf(bFacebookData.get("email")),String.valueOf(bFacebookData.get("id")) ,
                                                    String.valueOf(bFacebookData.get("name")));
                                            DBCallback dbCallback1 = new DBCallback() {
                                                @Override
                                                public void onSuccess(Map<String, String> serverResponse) {// L'id è la password per quelli che accedono con facebook
                                                    User currentUser = new User(String.valueOf(bFacebookData.get("email")), String.valueOf(bFacebookData.get("id")),
                                                            String.valueOf(bFacebookData.get("name")));
                                                    currentUser.setUserId(Integer.parseInt(serverResponse.get("user_id")));
                                                    //setto la sessione e passo alla homeactivity
                                                    session.setUserLogin(currentUser);
                                                    Intent intent = new Intent(getActivity(),
                                                            HomeActivity.class);
                                                    getActivity().startActivity(intent);

                                                }

                                                @Override
                                                public void onFailure(Map<String, String> serverResponse) {
                                                    Toast.makeText(getActivity(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();                                                }
                                            };
                                            dbManager.insertUser(currentUser, dbCallback1);

                                        } else{
                                            // L'id è la password per quelli che accedono con facebook
                                            User currentUser = new User(String.valueOf(bFacebookData.get("email")), String.valueOf(bFacebookData.get("id")),
                                                    String.valueOf(bFacebookData.get("name")));
                                            currentUser.setUserId(Integer.parseInt(serverResponse.get("user_id")));
                                            //setto la sessione e passo alla homeactivity
                                            session.setUserLogin(currentUser);
                                            Intent intent = new Intent(getActivity(),
                                                    HomeActivity.class);
                                            getActivity().startActivity(intent);

                                        }
                                    }

                                    @Override
                                    public void onFailure(Map<String, String> serverResponse){
                                        Toast.makeText(getActivity(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
                                    }
                                };
                                dbManager.checkMailValidity(String.valueOf(bFacebookData.get("email")), dbCallback);
                            }
                        });
                Bundle parameters = new Bundle();
                //i paramentri che voglio richiedere da facebook
                parameters.putString("fields", "id, name, email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.v("RegistrationFragment", "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.v("RegistrartionFragment", exception.getCause().toString());
            }
        });

        Button btnRegister = (Button) view.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertUser(v);
            }
        });
        Button btnLogin = (Button) view.findViewById(R.id.btnLinkToLoginScreen);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLoginClicked();
            }
        });

        return view;
    }


    /**
     * Ricavo i dati da Facebook
     */
    private Bundle getFacebookData(final JSONObject object) {
        Bundle bundle = new Bundle();
        try {
            if (object.has("name"))
                bundle.putString("name", object.getString("name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("id")){
                bundle.putString("id", object.getString("id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    //Metodo usato dalla callback di facebook che richaiama l'onActivityResult dell'activity
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        this.callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Metodo usato per registrare normalmente l'utente
     */
    public void insertUser(final View view) {
        final String name = usernameEt.getText().toString();
        final String email = emailEt.getText().toString();
        final String password = passwordEt.getText().toString();
        if (name.length() == 0 || email.length() == 0 || password.length() == 0) {
            Toast.makeText(getActivity(), R.string.inserisci_tutti_dati, Toast.LENGTH_SHORT).show();
        } else {

            final DBCallback dbCallback = new DBCallback(){
                @Override
                public void onSuccess(Map<String, String> serverResponse){
                    if(serverResponse.get("valida").equals("si")){

                        // Nel caso in cui l'email sia valida, procedo inserendo il nuovo utente nel DB
                        final User currentUser = new User(email, password, name);
                        DBCallback dbCallback1 = new DBCallback() {
                            @Override
                            public void onSuccess(Map<String, String> serverResponse) {
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                currentUser.setUserId(Integer.parseInt(serverResponse.get("user_id")));
                                session.setUserLogin(currentUser);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                            }

                            @Override
                            public void onFailure(Map<String, String> serverResponse) {
                                Toast.makeText(getActivity(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
                            }
                        };
                        dbManager.insertUser(currentUser, dbCallback1);

                    } else{
                        Toast.makeText(getActivity(), R.string.mail_non_valida, Toast.LENGTH_SHORT).show();
                        ((EditText) view.findViewById(R.id.listview_item_text)).setTextColor(Color.RED);
                    }
                }

                @Override
                public void onFailure(Map<String, String> serverResponse){
                    Toast.makeText(getActivity(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
                }
            };
            dbManager.checkMailValidity(email, dbCallback);
        }
    }
}
