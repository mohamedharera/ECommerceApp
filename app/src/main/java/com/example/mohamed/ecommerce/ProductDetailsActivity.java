package com.example.mohamed.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.mohamed.ecommerce.Model.Products;
import com.example.mohamed.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    TextView nameDetails,descDetails,priceDetails;
    ImageView product_imageDetails;
    ElegantNumberButton numberBtn;
    Button addToCart;

    DatabaseReference productRef;
    private String productID = "";
    private String state = "Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID = getIntent().getStringExtra("pid");

        product_imageDetails = findViewById(R.id.product_imageDetails);
        nameDetails = findViewById(R.id.product_nameDetails);
        descDetails = findViewById(R.id.product_description_details);
        priceDetails = findViewById(R.id.product_price_details);
        numberBtn = findViewById(R.id.numberBtn);
        addToCart = findViewById(R.id.pd_add_to_cart_button);

        getProductDetails(productID);

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state.equals("order placed") || state.equals("order shipped")){
                    Toast.makeText(ProductDetailsActivity.this, "you can add purchase more products , once your order is shipped or confirmed", Toast.LENGTH_SHORT).show();
                }else {
                    addToCartList();
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOrderState();
    }

    private void addToCartList() {
        String saveCurrentDate,saveCurrentTime;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentdate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String,Object> cartMap = new HashMap<>();
        cartMap.put("pid",productID);
        cartMap.put("pname",nameDetails.getText().toString());
        cartMap.put("price",priceDetails.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",numberBtn.getNumber());
        cartMap.put("discount","");

        cartListRef.child("User view").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products")
                .child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        cartListRef.child("Admin view").child(Prevalent.currentOnlineUser.getPhone())
                                .child("Products")
                                .child(productID)
                                .updateChildren(cartMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(ProductDetailsActivity.this, "Added to Cart list", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                });
    }

    private void getProductDetails(final String id) {

        productRef= FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Products products = dataSnapshot.getValue(Products.class);
                    nameDetails.setText(products.getPname());
                    priceDetails.setText(products.getPrice());
                    descDetails.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(product_imageDetails);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void checkOrderState(){
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    if (shippingState.equals("shipped")){
                        state = "order shipped";
                    }
                    else if (shippingState.equals("not shipped")){
                        state = "order placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}
