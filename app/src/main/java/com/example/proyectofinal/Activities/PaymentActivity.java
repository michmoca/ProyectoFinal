package com.example.proyectofinal.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyectofinal.AppDatabase;
import com.example.proyectofinal.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private String restaurantId, address, orderDetails;
    private Button buttonPlaceOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getSupportActionBar().setTitle("");

        // Get Order Data
        Intent intent = getIntent();
        restaurantId = intent.getStringExtra("restaurantId");
        address = intent.getStringExtra("address");
        orderDetails = intent.getStringExtra("orderDetails");

        final CardInputWidget mCardInputWidget =  findViewById(R.id.card_input_widget);

        buttonPlaceOrder =  findViewById(R.id.button_place_order);
        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                final Card card = mCardInputWidget.getCard();
                if (card == null) {
                    // Do not continue token creation.
                    Toast.makeText(getApplicationContext(), "La tarjeta no puede estar en blanco", Toast.LENGTH_LONG).show();
                } else {

                    // Disable the Place Order Button
                    setButtonPlaceOrder("CARGANDO...", false);


                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {

                            Stripe stripe = new Stripe(getApplicationContext(), "pk_test_RUecjVLIm5Et4DNwOK2d6WIP00GaFZKLbU");
                            stripe.createToken(
                                    card,
                                    new TokenCallback() {
                                        public void onSuccess(Token token) {
                                            // Make an order
                                            addOrder(token.getId());

                                            //prueba de token
                                            /*Toast.makeText(getApplicationContext(),
                                                    token.getId(),
                                                    Toast.LENGTH_LONG
                                            ).show();*/
                                        }
                                        public void onError(Exception error) {
                                            // Show localized error message
                                            Toast.makeText(getApplicationContext(),
                                                    error.getLocalizedMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show();

                                            // Enable the Place Order Button
                                           setButtonPlaceOrder("REALIZAR PEDIDO", true);
                                        }
                                    }
                            );

                            return null;
                        }
                    }.execute();
                }
            }
        });

    }

    private void addOrder(final String stripeToken) {
        String url = getString(R.string.API_URL) + "/customer/order/add/";

        StringRequest postRequest = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // Execute code
                        Log.d("PEDIDO AÑADIDO", response.toString());

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equals("success")) {
                                deleteTray();

                                // Saltar a la pantalla de pedido
                                Intent intent = new Intent(getApplicationContext(), CustomerActivity.class);
                                intent.putExtra("screen", "order");
                                startActivity(intent);

                            } else {
                                Toast.makeText(getApplicationContext(),
                                        jsonObject.getString("error"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Enable the Place Order Button
                        setButtonPlaceOrder("REALIZAR PEDIDO", true);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        // Enable the Place Order Button
                        setButtonPlaceOrder("REALIZAR PEDIDO", true);

                        Toast.makeText(getApplicationContext(),
                                error.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final SharedPreferences sharedPref = getSharedPreferences("DATOSFB", Context.MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();
                params.put("access_token", sharedPref.getString("token", ""));
                params.put("restaurant_id", restaurantId);
                params.put("address", address);
                params.put("order_details", orderDetails);
                params.put("stripe_token", stripeToken);

                return params;
            }
        };

        postRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }

    private void setButtonPlaceOrder(String text, boolean isEnable) {
        buttonPlaceOrder.setText(text);
        buttonPlaceOrder.setClickable(isEnable);
        if (isEnable) {
            buttonPlaceOrder.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        } else {
            buttonPlaceOrder.setBackgroundColor(getResources().getColor(R.color.colorLightGray));
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void deleteTray() {
        final AppDatabase db = AppDatabase.getAppDatabase(this);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                db.trayDao().deleteAll();
                return null;
            }
        }.execute();
    }

}
