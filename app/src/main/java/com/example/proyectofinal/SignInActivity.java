package com.example.proyectofinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    private Button customerButton, driverButton;
    private CallbackManager callbackManager;
    private SharedPreferences sharedPref;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        customerButton =  findViewById(R.id.button_customer);
        driverButton =  findViewById(R.id.button_driver);

        // Handle Customer Button
        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                customerButton.setTextColor(getResources().getColor(android.R.color.white));

                driverButton.setBackgroundColor(getResources().getColor(android.R.color.white));
                driverButton.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });

        // Handle Driver Button
        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                driverButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                driverButton.setTextColor(getResources().getColor(android.R.color.white));

                customerButton.setBackgroundColor(getResources().getColor(android.R.color.white));
                customerButton.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });

        //Handle login
        buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AccessToken.getCurrentAccessToken() == null) {
                    LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("public_profile", "email"));
                }else{

                    ColorDrawable customerbuttonColor = (ColorDrawable) customerButton.getBackground();
                    if(customerbuttonColor.getColor() == getResources().getColor(R.color.colorAccent)){
                        loginToServer(AccessToken.getCurrentAccessToken().getToken(), "customer");
                        Log.d("CUSTOMER 1", "CUSTOMER 1");
                    }else{
                        loginToServer(AccessToken.getCurrentAccessToken().getToken(), "driver");
                        Log.d("DRIVER 1", "DRIVER 1");
                    }
                }



            }
        });



        callbackManager = CallbackManager.Factory.create();
        sharedPref = getSharedPreferences("DATOSFB", Context.MODE_PRIVATE);

        final Button buttonLogout = (Button) findViewById(R.id.button_logout);
        buttonLogout.setVisibility(View.GONE);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("FACE ENTRO", loginResult.getAccessToken().getToken());
                        Log.d("sharedPref NAME", sharedPref.getString("name", ""));
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        // Application code
                                        Log.d("FACE DETALLES", object.toString());


                                        SharedPreferences.Editor editor = sharedPref.edit();

                                        try{
                                            editor.putString("name", object.getString("name"));
                                            editor.putString("email", object.getString("email"));
                                            editor.putString("avatar", object.getJSONObject("picture").getJSONObject("data").getString("url"));

                                        }catch(JSONException e){
                                            e.printStackTrace();
                                        }

                                        editor.commit();

                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,picture");
                        request.setParameters(parameters);
                        request.executeAsync();

                        ColorDrawable customerbuttonColor = (ColorDrawable) customerButton.getBackground();
                        if(customerbuttonColor.getColor() == getResources().getColor(R.color.colorAccent)){
                            loginToServer(AccessToken.getCurrentAccessToken().getToken(), "customer");
                        }else{
                            loginToServer(AccessToken.getCurrentAccessToken().getToken(), "driver");
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });


        if (AccessToken.getCurrentAccessToken() != null) {
            Log.d("USER", sharedPref.getAll().toString());
            buttonLogin.setText("Continuar como " + sharedPref.getString("email", ""));
            buttonLogout.setVisibility(View.VISIBLE);
        }

        // Handle Logout button
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                buttonLogin.setText("Entrar con Facebook");
                buttonLogout.setVisibility(View.GONE);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void loginToServer(String facebookAccessToken, final String userType) {
        String url = "https://boiling-mesa-85590.herokuapp.com/api/social/convert-token";
        buttonLogin.setText("LOADING...");
        buttonLogin.setClickable(false);
        buttonLogin.setBackgroundColor(getResources().getColor(R.color.colorLightGray));

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("grant_type", "convert_token");
            jsonBody.put("client_id", "GDUdJrHqsgE8UpPHvhpqMwoUlpwix4Wh90fHuFvJ");
            jsonBody.put("client_secret", "ThVjmbuhmslQKn2ae3LCMTLRgV7Ca3cAfI7lWdPmOzk4z2jI5xucwB37hWrL0IxsgMUjGEVVg3y0mLwvbrtbEAu1qw0XiPPAfWS6bNfdTamYV9UFDeNLF1Ej22ZnIEkJ");
            jsonBody.put("backend", "facebook");
            jsonBody.put("token", facebookAccessToken);
            jsonBody.put("user_type", userType);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("ERROR EN JSONBODY", "ERROR EN JSONBODY");
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Execute code
                        Log.d("LOGIN TO SERVER", response.toString());

                        // Shave server token to local database
                        SharedPreferences.Editor editor = sharedPref.edit();

                        try {
                            editor.putString("token", response.getString("access_token"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.commit();

                        // Start main activity
                        if (userType.equals("customer")) {
                            Intent intent = new Intent(getApplicationContext(), CustomerActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), DriverActivity.class);
                            startActivity(intent);
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("EonErrorResponse tttY", error.toString());

                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);


    }



}
