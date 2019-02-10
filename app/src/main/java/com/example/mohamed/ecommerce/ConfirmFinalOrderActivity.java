package com.example.mohamed.ecommerce;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mohamed.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    EditText shipment_name,shipment_phone,shipment_address,shipment_city;
    Button confirmBtn;

    private String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");

        confirmBtn = findViewById(R.id.confirm_final_order_btn);
        shipment_name = findViewById(R.id.shipment_name);
        shipment_phone = findViewById(R.id.shipment_phone);
        shipment_address = findViewById(R.id.shipment_address);
        shipment_city = findViewById(R.id.shipment_city);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }

    private void check() {

        String name =shipment_name.getText().toString();
        String phone = shipment_phone.getText().toString();
        String address = shipment_address.getText().toString();
        String city = shipment_city.getText().toString();

        if (name.isEmpty()) {
            shipment_name.setError("this field couldn't be empty");
            shipment_name.requestFocus();
            return;
        }
        else if (phone.isEmpty()) {
            shipment_phone.setError("this field couldn't be empty");
            shipment_phone.requestFocus();
            return;
        }
        else if (address.isEmpty()) {
            shipment_address.setError("this field couldn't be empty");
            shipment_address.requestFocus();
            return;
        }
        else if (city.isEmpty()) {
            shipment_city.setError("this field couldn't be empty");
            shipment_city.requestFocus();
            return;
        }else {
            confirmOrder();
        }
    }

    private void confirmOrder() {
        final String saveCurrentDate,saveCurrentTime;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentdate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());
        HashMap<String,Object>orderMap = new HashMap<>();
        orderMap.put("totalAmount",totalAmount);
        orderMap.put("name",shipment_name.getText().toString());
        orderMap.put("phone",shipment_phone.getText().toString());
        orderMap.put("address",shipment_address.getText().toString());
        orderMap.put("city",shipment_city.getText().toString());
        orderMap.put("date",saveCurrentDate);
        orderMap.put("time",saveCurrentTime);
        orderMap.put("state","not shipped");
        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User view")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "your final order has placed successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this,HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

    }
}
