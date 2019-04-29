package com.example.proyectofinal.Activities;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.proyectofinal.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

public class PaymentActivity extends AppCompatActivity {

    private Button buttonPlaceOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getSupportActionBar().setTitle("");

        final CardInputWidget mCardInputWidget =  findViewById(R.id.card_input_widget);

        buttonPlaceOrder = (Button) findViewById(R.id.button_place_order);
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
                    //setButtonPlaceOrder("LOADING...", false);

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {

                            Stripe stripe = new Stripe(getApplicationContext(), "pk_test_RUecjVLIm5Et4DNwOK2d6WIP00GaFZKLbU");
                            stripe.createToken(
                                    card,
                                    new TokenCallback() {
                                        public void onSuccess(Token token) {
                                            // Make an order
                                            //addOrder(token.getId());
                                            //prueba de token
                                            Toast.makeText(getApplicationContext(),
                                                    token.getId(),
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        }
                                        public void onError(Exception error) {
                                            // Show localized error message
                                            Toast.makeText(getApplicationContext(),
                                                    error.getLocalizedMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show();

                                            // Enable the Place Order Button
                                           /// setButtonPlaceOrder("PLACE ORDER", true);
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
}
