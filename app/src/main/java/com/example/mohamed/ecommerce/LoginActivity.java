package com.example.mohamed.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamed.ecommerce.Model.Users;
import com.example.mohamed.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    EditText phone,pass;
    Button loginBtn;
    CheckBox remember_me;
    TextView admin,notAdmin;
    ProgressDialog loadingBar;
    private String DbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingBar = new ProgressDialog(this);
        phone = findViewById(R.id.login_phoneNumber);
        pass = findViewById(R.id.login_pass);
        loginBtn = findViewById(R.id.loginBtn);
        admin = findViewById(R.id.admin_panelLink);
        notAdmin = findViewById(R.id.not_admin_panelLink);

        remember_me = findViewById(R.id.rememberme_chb);
        Paper.init(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLogin();
            }
        });
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setText("Login Admin");
                admin.setVisibility(View.INVISIBLE);
                notAdmin.setVisibility(View.VISIBLE);
                DbName = "Admins";
            }
        });
        notAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setText("Login");
                admin.setVisibility(View.INVISIBLE);
                notAdmin.setVisibility(View.VISIBLE);
                DbName = "Users";
            }
        });

    }

    public void startLogin(){
        String logPhone = phone.getText().toString();
        String logPass = pass.getText().toString();


        if (logPhone.isEmpty()) {
            phone.setError("this field couldn't be empty");
            phone.requestFocus();
            return;
        }
        else if (logPass.isEmpty()) {
            pass.setError("this field couldn't be empty");
            pass.requestFocus();
            return;
        }
        else {
            loadingBar.setTitle("Login Account");
            loadingBar.setTitle("please wait while we are checking credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccesstoAccount(logPhone,logPass);
        }
    }
    public void AllowAccesstoAccount(final String phone, final String pass){

        if (remember_me.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,pass);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(DbName).child(phone).exists()){

                    Users userData = dataSnapshot.child(DbName).child(phone).getValue(Users.class);
                    if (userData.getPhone().equals(phone)){
                        if (userData.getPassword().equals(pass)){

                            if (DbName.equals("Admins")){
                                Toast.makeText(LoginActivity.this, "Welcome Admin,you logged in successfully ", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this,AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if (DbName.equals("Users")){
                                Toast.makeText(LoginActivity.this, "logged in successfully ", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                Prevalent.currentOnlineUser = userData;
                                startActivity(intent);
                            }

                        }
                    }else {
                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, "password is incorrect", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(LoginActivity.this, "Account with this " +phone +"number not exists" , Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
