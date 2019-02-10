package com.example.mohamed.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {

    ImageView tShirts,sports,cloths_female,cloths_sweather;
    ImageView glasses,purses_bags,hats,shoes;
    ImageView headphoness,laptops,watches,mobiles;
    Button check_ordersBtn,logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        tShirts = findViewById(R.id.cloths_tshirts);
        sports = findViewById(R.id.cloths_sports);
        cloths_female = findViewById(R.id.cloths_female);
        cloths_sweather = findViewById(R.id.cloths_sweather);
        glasses = findViewById(R.id.glasses);
        purses_bags = findViewById(R.id.purses_bags);
        hats = findViewById(R.id.hats);
        shoes = findViewById(R.id.shoes);
        headphoness = findViewById(R.id.headphoness);
        laptops = findViewById(R.id.laptops);
        watches = findViewById(R.id.watches);
        mobiles = findViewById(R.id.mobiles);
        logoutBtn = findViewById(R.id.logoutBtn);
        check_ordersBtn = findViewById(R.id.check_ordersBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        check_ordersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminNewOrderActivity.class);
                startActivity(intent);
            }
        });



        tShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProduct.class);
                intent.putExtra("category","tShirts");
                startActivity(intent);
            }
        });
        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProduct.class);
                intent.putExtra("category","sports");
                startActivity(intent);
            }
        });
        cloths_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProduct.class);
                intent.putExtra("category","cloths_female");
                startActivity(intent);
            }
        });
        cloths_sweather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProduct.class);
                intent.putExtra("category","cloths_sweather");
                startActivity(intent);
            }
        });

        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProduct.class);
                intent.putExtra("category","glasses");
                startActivity(intent);
            }
        });
        purses_bags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProduct.class);
                intent.putExtra("category","purses_bags");
                startActivity(intent);
            }
        });
        hats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProduct.class);
                intent.putExtra("category","hats");
                startActivity(intent);
            }
        });
        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProduct.class);
                intent.putExtra("category","shoes");
                startActivity(intent);
            }
        });

        headphoness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProduct.class);
                intent.putExtra("category","headphoness");
                startActivity(intent);
            }
        });
        laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProduct.class);
                intent.putExtra("category","laptops");
                startActivity(intent);
            }
        });
        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProduct.class);
                intent.putExtra("category","watches");
                startActivity(intent);
            }
        });
        mobiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this,AdminAddProduct.class);
                intent.putExtra("category","mobiles");
                startActivity(intent);
            }
        });

    }

}
