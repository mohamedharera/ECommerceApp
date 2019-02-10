package com.example.mohamed.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText name,phone,pass;
    Button registerBtn;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loadingBar = new ProgressDialog(this);
        phone = findViewById(R.id.register_phoneNumber);
        pass = findViewById(R.id.register_pass);
        name = findViewById(R.id.register_name);
        registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    public void createAccount(){
        String regName = name.getText().toString();
        String regPhone = phone.getText().toString();
        String regPass = pass.getText().toString();

        if (regName.isEmpty()) {
            name.setError("this field couldn't be empty");
            name.requestFocus();
            return;
        }
        else if (regPhone.isEmpty()) {
            phone.setError("this field couldn't be empty");
            phone.requestFocus();
            return;
        }
        else if (regPass.isEmpty()) {
            pass.setError("this field couldn't be empty");
            pass.requestFocus();
            return;
        }
        else {
            loadingBar.setTitle("Create Account");
            loadingBar.setTitle("please wait while we are checking credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validatePhoneNumber(regName,regPhone,regPass);
        }
    }
    public void validatePhoneNumber(final String name, final String phone, final String pass){

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (! dataSnapshot.child("Users").child(phone).exists()){

                    HashMap <String,Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone",phone);
                    userDataMap.put("password",pass);
                    userDataMap.put("name",name);

                    RootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "your account has been created", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }else {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Networking Error Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
                else {
                    Toast.makeText(RegisterActivity.this, "This" + phone + "is already exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "please try again using new phone number", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
