package com.example.mohamed.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddProduct extends AppCompatActivity {

    EditText product_name,product_description,product_price;
    Button addProduct;
    ImageView productImage;
    String categoryName,description,price,name,saveCurrentDate,saveCurrentTime;

    private static final int galleryPick = 1;
    private Uri imageUri;
    private String productRandomKey,downloadImageUrl;
    private StorageReference productImageRef;
    private DatabaseReference productRef;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        categoryName = getIntent().getExtras().get("category").toString();
        productImageRef = FirebaseStorage.getInstance().getReference().child("product Images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        loadingBar = new ProgressDialog(this);

        product_description = findViewById(R.id.product_description);
        product_name = findViewById(R.id.product_name);
        product_price = findViewById(R.id.product_price);
        addProduct = findViewById(R.id.addProduct);
        productImage = findViewById(R.id.select_product_image);

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProductData();
            }
        });

    }

    private void validateProductData() {
        description = product_description.getText().toString();
        price = product_price.getText().toString();
        name = product_name.getText().toString();

        if (description.isEmpty()) {
            product_description.setError("this field couldn't be empty");
            product_description.requestFocus();
            return;
        }
        else if (price.isEmpty()) {
            product_price.setError("this field couldn't be empty");
            product_price.requestFocus();
            return;
        }
        else if (name.isEmpty()) {
            product_name.setError("this field couldn't be empty");
            product_name.requestFocus();
            return;
        }
        else {
            storeProductInformation();
        }

    }

    private void storeProductInformation() {

        loadingBar.setTitle("Adding new product");
        loadingBar.setMessage("please wait while we add the product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentdate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filepath = productImageRef.child(imageUri.getLastPathSegment()+productRandomKey + ".jpg");

        final UploadTask uploadTask = filepath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=  e.toString();
                Toast.makeText(AdminAddProduct.this, "Error "+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddProduct.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                Task<Uri>urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                            downloadImageUrl = filepath.getDownloadUrl().toString();
                            return filepath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){

                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddProduct.this, "got the product image url successfully", Toast.LENGTH_SHORT).show();

                            saveProductInfoToDatabase();
                        }
                    }
                });

            }
        });
    }

    private void saveProductInfoToDatabase() {
        HashMap<String,Object> productMap = new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",description);
        productMap.put("image",downloadImageUrl);
        productMap.put("category",categoryName);
        productMap.put("price",price);
        productMap.put("pname",name);

        productRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Intent intent = new Intent(AdminAddProduct.this,AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddProduct.this, "product added successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            loadingBar.dismiss();
                            Toast.makeText(AdminAddProduct.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==galleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            imageUri = data.getData();
            productImage.setImageURI(imageUri);
        }
    }
}
