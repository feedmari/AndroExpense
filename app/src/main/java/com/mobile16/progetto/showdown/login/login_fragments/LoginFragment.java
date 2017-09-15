package com.mobile16.progetto.showdown.login.login_fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.facebook.login.LoginManager;
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
 * Classe che implementa la form di login.
 */
@SuppressWarnings("deprecation")
public class LoginFragment extends Fragment {

    private DBManager dbManager;
    private EditText inputEmail;
    private EditText inputPassword;
    private SessionManager session;
    private User currentUser;
    private CallbackManager callbackManager;
    private Bundle bFacebookData;
    private OnBtnRegClicked listener;
    private Context context;


    /**
     * Interfaccia che modella il listener che intercetta il click per andare nel fragment di
     * registrazione.
     */
    public interface OnBtnRegClicked {
        void onRegClicked();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnBtnRegClicked)
            setListener((OnBtnRegClicked) activity);
    }

    /**
     * Setta il listener per questo fragment.
     */
    public void setListener(OnBtnRegClicked listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Init Facebook SDK
        FacebookSdk.sdkInitialize(this.getActivity());

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        this.context = container.getContext();

        // Recupero gli elementi grafici
        this.inputEmail = (EditText) view.findViewById(R.id.listview_item_text);
        this.inputPassword = (EditText) view.findViewById(R.id.password);
        Button btnLogin = (Button) view.findViewById(R.id.btnLogin);
        Button btnReg = (Button) view.findViewById(R.id.btnLinkToRegisterScreen);

        //DBManager
        this.dbManager = new DBManager(getActivity());

        // Session manager
        this.session = new SessionManager(getActivity());

        // Check if user is already logged in or not
        if (this.session.isLoggedIn()) {
            // User is already logged in. Take him to home activity
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
            getActivity().finish();
        } else {
            //logout also from facebook
            LoginManager.getInstance().logOut();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                currentUser = new User(email, password);

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(currentUser);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getActivity(),
                            "Please enter the credentials!", Toast.LENGTH_LONG).show();
                }
            }

        });


        /**
         * Setting facebook login/registration button
         */
        AppEventsLogger.activateApp(getActivity());
        bFacebookData = new Bundle();  //all data will be save into this bundle
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.facebook_login);
        //Setting user permission for get only main and user public name on facebook
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        //Facebook callback registration
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                bFacebookData = getFacebookData(object);

                                final DBCallback dbCallback = new DBCallback(){
                                    @Override
                                    public void onSuccess(Map<String, String> serverResponse){
                                        if(serverResponse.get("valida").equals("si")){
                                            // Nel caso in cui l'email sia valida, procedo inserendo
                                            // il nuovo utente nel DB
                                            final User currentUser =
                                                    new User(String.valueOf(bFacebookData.get("email")),
                                                    String.valueOf(bFacebookData.get("id")) ,
                                                    String.valueOf(bFacebookData.get("name")));

                                            // Inserimento nuovo utente
                                            DBCallback dbCallback1 = new DBCallback() {
                                                @Override
                                                public void onSuccess(Map<String, String> serverResponse) {
                                                    User currentUser = new User(
                                                            String.valueOf(bFacebookData.get("email")),
                                                            String.valueOf(bFacebookData.get("id")),
                                                            String.valueOf(bFacebookData.get("name")));
                                                    currentUser.setUserId(
                                                            Integer.parseInt(serverResponse.get("user_id")));
                                                    session.setUserLogin(currentUser);

                                                    Intent intent = new Intent(context,
                                                            HomeActivity.class);
                                                    context.startActivity(intent);
                                                }

                                                @Override
                                                public void onFailure(Map<String, String> serverResponse) {
                                                    Toast.makeText(getActivity(),
                                                            serverResponse.get("error"),
                                                            Toast.LENGTH_SHORT).show();                                                }
                                            };
                                            dbManager.insertUser(currentUser, dbCallback1);

                                        } else{
                                            User currentUser = new User(
                                                    String.valueOf(bFacebookData.get("email")),
                                                    String.valueOf(bFacebookData.get("id")),
                                                    String.valueOf(bFacebookData.get("name")));
                                            currentUser.setUserId(Integer.parseInt(
                                                    serverResponse.get("user_id")));
                                            session.setUserLogin(currentUser);
                                            Intent intent = new Intent(context,
                                                    HomeActivity.class);
                                            context.startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Map<String, String> serverResponse){
                                        Toast.makeText(getActivity(),
                                                serverResponse.get("error"),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                };
                                dbManager.checkMailValidity(
                                        String.valueOf(bFacebookData.get("email")), dbCallback);

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e("LoginFragment","onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e("LoginFragment", "Log" +exception.getCause().toString());
            }
        });

        //listener sul bottone per andare alla registrazione
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegistration();
            }
        });

        return view;
    }

    /**
     * Controlla se il login è corretto
     */
    private void checkLogin(final User currentUser) {
        DBCallback dbCallback = new DBCallback() {
            @Override
            public void onSuccess(Map<String, String> serverResponse) {
                if(serverResponse.get("valida").equals("si")){
                    currentUser.setUserId(Integer.parseInt(serverResponse.get("user_id")));
                    currentUser.setUsername(serverResponse.get("username"));
                    User user = new User(currentUser.getMail(), currentUser.getPassword(),
                            serverResponse.get("username"), Integer.parseInt(serverResponse.get("user_id")));
                    session.setUserLogin(user);

                    // Launch home activity
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                } else{
                    Toast.makeText(getActivity(), R.string.errore_login, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Map<String, String> serverResponse) {
                Toast.makeText(getActivity(), serverResponse.get("error"), Toast.LENGTH_SHORT).show();
            }
        };
        dbManager.checkUserLogin(currentUser, dbCallback);
    }

    // Prendo i dati restituiti da Facebook
    private Bundle getFacebookData(JSONObject object) {
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

    /**
     * Metodo che richiama la transazione al fragment del login
     */
    public void goToRegistration(){
        this.listener.onRegClicked();
    }

    /**
     * L'onActivityResult che server per far funzionare la callback di facebook,
     * passa i dati all'onActivityResult dell'activity
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
