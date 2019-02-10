package com.example.mohamed.ecommerce.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.mohamed.ecommerce.Interface.ItemClickListner;
import com.example.mohamed.ecommerce.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView productName, productPrice, productQuantity;
    public ItemClickListner listner;


    public CartViewHolder(View itemView)
    {
        super(itemView);


        productQuantity = (TextView) itemView.findViewById(R.id.product_Quantity);
        productName     = (TextView) itemView.findViewById(R.id.productName);
        productPrice    = (TextView) itemView.findViewById(R.id.productPrice);
    }

    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onClick(view, getAdapterPosition(), false);
    }
}
