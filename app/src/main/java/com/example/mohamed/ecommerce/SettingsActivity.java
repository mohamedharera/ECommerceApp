package com.example.mohamed.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamed.ecommerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    TextView close_settings,update_account_settings,profile_text_changeBtn;
    EditText settings_phoneNumber,settings_fullName,settings_address;
    CircleImageView setings_profile_image;

    private StorageTask uploadTask;
    private Uri imageUri;
    private String myUrl = "";
    private StorageReference storageProfilePictureReference;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureReference = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        close_settings = findViewById(R.id.close_settings);
        update_account_settings = findViewById(R.id.update_account_settings);
        profile_text_changeBtn = findViewById(R.id.profile_text_changeBtn);
        settings_fullName = findViewById(R.id.settings_fullName);
        settings_address = findViewById(R.id.settings_address);
        settings_phoneNumber = findViewById(R.id.settings_phoneNumber);
        setings_profile_image = findViewById(R.id.setings_profile_image);

        userInfoDisplay(setings_profile_image,settings_fullName,settings_phoneNumber,settings_address);

        close_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        update_account_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){
                    userInfoSaved();
                }else {
                    updateOnlyUserInfo();
                }
            }
        });
        profile_text_changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";
                CropImage.activity(imageUri).setAspectRatio(1,1).start(SettingsActivity.this);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            setings_profile_image.setImageURI(imageUri);
        }
        else {
            Toast.makeText(this, "Error please try again", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this,SettingsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String,Object>userMap = new HashMap<>();
        userMap.put("name",settings_fullName.getText().toString());
        userMap.put("address",settings_address.getText().toString());
        userMap.put("phoneOrder",settings_phoneNumber.getText().toString());

        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        Intent intent = new Intent(SettingsActivity.this,HomeActivity.class);
        startActivity(intent);

        Toast.makeText(SettingsActivity.this, "profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(settings_fullName.getText().toString())){
            Toast.makeText(this, "name is mandatory", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(settings_address.getText().toString())){
            Toast.makeText(this, "address is mandatory", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(settings_phoneNumber.getText().toString())){
            Toast.makeText(this, "phone is mandatory", Toast.LENGTH_SHORT).show();
        }else if (checker.equals("clicked")) {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("update profile");
        progressDialog.setMessage("please wait while updating your profile");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null){
            final StorageReference fileRef = storageProfilePictureReference
                    .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String,Object>userMap = new HashMap<>();
                        userMap.put("name",settings_fullName.getText().toString());
                        userMap.put("address",settings_address.getText().toString());
                        userMap.put("phoneOrder",settings_phoneNumber.getText().toString());
                        userMap.put("image",myUrl);

                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();
                        Intent intent = new Intent(SettingsActivity.this,HomeActivity.class);
                        startActivity(intent);

                        Toast.makeText(SettingsActivity.this, "profile updated successfully", Toast.LENGTH_SHORT).show();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(this, "image is not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final CircleImageView setings_profile_image, final EditText settings_fullName, final EditText settings_phoneNumber, final EditText settings_address) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("image").exists()){
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(setings_profile_image);
                        settings_fullName.setText(name);
                        settings_phoneNumber.setText(phone);
                        settings_address.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
