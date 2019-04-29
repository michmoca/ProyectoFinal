package com.example.proyectofinal.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinal.AppDatabase;
import com.example.proyectofinal.R;
import com.example.proyectofinal.Utilities.Tray;
import com.squareup.picasso.Picasso;

public class MealDetailActivity extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_detail);

        Intent intent = getIntent();
        final String restaurantId = intent.getStringExtra("restaurantId");
        final String mealId = intent.getStringExtra("mealId");
        final String mealName = intent.getStringExtra("mealName");
        String mealDescription = intent.getStringExtra("mealDescription");
        final Float mealPrice = intent.getFloatExtra("mealPrice", 0);
        String mealImage = intent.getStringExtra("mealImage");
        getSupportActionBar().setTitle(mealName);

        TextView name =  findViewById(R.id.meal_name);
        TextView desc =  findViewById(R.id.meal_desc);
        final TextView price =  findViewById(R.id.meal_price);
        ImageView image =  findViewById(R.id.meal_image);

        name.setText(mealName);
        desc.setText(mealDescription);
        price.setText("₡" + mealPrice);
        Picasso.with(getApplicationContext()).load(mealImage).fit().into(image);

        // Declara buttons
        final TextView labelQuantity =  findViewById(R.id.label_quantity);
        Button buttonIncrease =  findViewById(R.id.button_increase);
        Button buttonDecrease =  findViewById(R.id.button_decrease);
        Button buttonTray =  findViewById(R.id.button_add_tray);

        // Handle Button Increase Click
        buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(labelQuantity.getText().toString());

                qty = qty + 1;
                labelQuantity.setText(qty + "");
                price.setText("₡" + (qty * mealPrice));

            }
        });

        // Handle Button Decrease Click
        buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(labelQuantity.getText().toString());
                if (qty > 1) {
                    qty = qty - 1;
                    labelQuantity.setText(qty + "");
                    price.setText("₡" + (qty * mealPrice));
                }
            }
        });

        // Initialize DB
        db = AppDatabase.getAppDatabase(this);

        // Handle Button Add To Tray Click
        buttonTray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(labelQuantity.getText().toString());
                insertTray(mealId, mealName, mealPrice, qty, restaurantId);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meal_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("StaticFieldLeak")
    private void insertTray(final String mealId, final String mealName, final float mealPrice, final int mealQty, final String restaurantId) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Tray tray = new Tray();
                tray.setMealId(mealId);
                tray.setMealName(mealName);
                tray.setMealPrice(mealPrice);
                tray.setMealQuantity(mealQty);
                tray.setRestaurantId(restaurantId);

                db.trayDao().insertAll(tray);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "COMIDA AÑADIDA", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (R.id.tray_button == id) {
            /*Intent intent = new Intent(getApplicationContext(), CustomerActivity.class);
            intent.putExtra("screen", "tray");
            startActivity(intent);*/
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids){
                    for (Tray tray: db.trayDao().getAll()){
                        Log.d("Tray ITEM", tray.getMealName() + " - " + tray.getMealQuantity());
                    }
                    return null;
                }
            }.execute();

        }

        return super.onOptionsItemSelected(item);
    }

}
