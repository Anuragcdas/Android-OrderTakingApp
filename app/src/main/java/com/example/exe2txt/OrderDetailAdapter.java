package com.example.exe2txt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {


    private Context context;
    private List<OrderItemEntity> itemEntityList;

    public OrderDetailAdapter(Context context, List<OrderItemEntity> itemEntityList) {

        this.context = context;
        this.itemEntityList = itemEntityList;

    }


    public void setOrders(List<OrderItemEntity> orderitem) {

        this.itemEntityList = orderitem;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_orderdetail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {


        OrderItemEntity orderItem = itemEntityList.get(position);

        Glide.with(context)
                .load(orderItem.getThumbnail())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.errorimage)
                .into(holder.img);

        holder.productName.setText("ProductName:  " + orderItem.getProductName());

        holder.productPrice.setText("Product Price:  $" + orderItem.getPriceItem());


        holder.orderQuantity.setText("Quantity Purchased:  " + orderItem.getQuantity());


    }

    @Override
    public int getItemCount() {
        return itemEntityList == null ? 0 : itemEntityList.size();
    }

    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        MaterialTextView productName, productPrice, orderQuantity;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.orderThumbnail);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            orderQuantity = itemView.findViewById(R.id.orderQuantity);


        }
    }
}
