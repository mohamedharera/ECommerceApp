package com.example.mohamed.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mohamed.ecommerce.Model.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrderActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        recyclerView = findViewById(R.id.recyclerView_orderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders>options = new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(orderRef,AdminOrders.class)
                .build();

        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder> adapter = new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model) {
                holder.order_user_name.setText("Name: "+model.getName());
                holder.order_address_city.setText("Address: "+model.getAddress()+", City: "+model.getCity());
                holder.order_date_time.setText("Order at: "+model.getDate()+" "+model.getTime());
                holder.order_phone_number.setText("Phone: "+model.getPhone());
                holder.order_total_price.setText("Total price: $"+model.getTotalAmount());
                holder.showAllProducts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uid = getRef(position).getKey();
                        Intent intent = new Intent(AdminNewOrderActivity.this,AdminUserProductActivity.class);
                        intent.putExtra("uid",uid);
                        startActivity(intent);
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{"Yes","No"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrderActivity.this);
                        builder.setTitle("Have you shapped this order product");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (i == 0){
                                    String uid = getRef(position).getKey();
                                    removeOrder(uid);
                                }else {
                                    finish();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
             return new AdminOrdersViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void removeOrder(String uid) {
        orderRef.child(uid).removeValue();
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{

        TextView order_user_name,order_phone_number,order_total_price,order_address_city,order_date_time;
        Button showAllProducts;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            showAllProducts = itemView.findViewById(R.id.show_all_productsBtn);
            order_user_name = itemView.findViewById(R.id.order_user_name);
            order_phone_number = itemView.findViewById(R.id.order_phone_number);
            order_total_price = itemView.findViewById(R.id.order_total_price);
            order_address_city = itemView.findViewById(R.id.order_address_city);
            order_date_time = itemView.findViewById(R.id.order_date_time);
        }
    }
}
