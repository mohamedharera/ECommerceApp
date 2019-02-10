package com.example.mohamed.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamed.ecommerce.Model.Cart;
import com.example.mohamed.ecommerce.Prevalent.Prevalent;
import com.example.mohamed.ecommerce.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    TextView totalPrice,msg1;
    Button next;
    RecyclerView recyclerView;
    private int overTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        msg1 = findViewById(R.id.msg1);
        totalPrice = findViewById(R.id.totalPrice);
        next = findViewById(R.id.next);
        recyclerView = findViewById(R.id.recyclerView_cartList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalPrice.setText("Total price: "+ String.valueOf(overTotalPrice)+"$");
                Intent intent = new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price",String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart List");

        checkOrderState();
        FirebaseRecyclerOptions<Cart>options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(reference.child("User view")
                        .child(Prevalent.currentOnlineUser.getPhone())
                        .child("Products"),Cart.class).build();

        FirebaseRecyclerAdapter<Cart,CartViewHolder>adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                holder.productName.setText("name: " + model.getPname());
                holder.productPrice.setText("price: " +model.getPrice()+"$");
                holder.productQuantity.setText("quantity: " +model.getQuantity());

                int oneTypeProductPrice = (Integer.valueOf(model.getPrice()))*(Integer.valueOf(model.getQuantity()));
                overTotalPrice = overTotalPrice + oneTypeProductPrice;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{"Edit","Remove"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    Intent intent = new Intent(CartActivity.this,ProductDetailsActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);
                                }
                                if (i == 1){
                                    reference.child("User view")
                                            .child(Prevalent.currentOnlineUser.getPhone())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Intent intent = new Intent(CartActivity.this,HomeActivity.class);
                                                        startActivity(intent);
                                                        Toast.makeText(CartActivity.this, "Item removed successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void checkOrderState(){
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    if (shippingState.equals("shipped")){
                        totalPrice.setText("Dear: "+ userName +"\n order is shipped successfully");
                        recyclerView.setVisibility(View.GONE);
                        msg1.setVisibility(View.VISIBLE);
                        msg1.setText("congratulations,your final order has shipped successfully,soon you will recieve the order at your door step");
                        next.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "you can purchase more products once you recieved your first final order", Toast.LENGTH_SHORT).show();
                    }else if (shippingState.equals("not shipped")){
                        totalPrice.setText("shipping state = not Shipped");
                        recyclerView.setVisibility(View.GONE);
                        msg1.setVisibility(View.VISIBLE);
                        next.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "you can purchase more products once you recieved your first final order", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
