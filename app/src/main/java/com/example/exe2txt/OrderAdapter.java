package com.example.exe2txt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {


    private Context context;
    private List<OrderEntity> orderList;


    private AppDatabase db;

    public OrderAdapter(Context context, List<OrderEntity> orderList) {

        this.context = context;
        this.orderList = orderList;
    }

    public void setOrders(List<OrderEntity> orders) {
        if (this.orderList == null) {
            this.orderList = new ArrayList<>();  // Initialize if null
        }
        this.orderList.clear();
        this.orderList.addAll(orders);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        db = AppDatabase.getInstance(context);

        OrderEntity order = orderList.get(position);


        String date=DateFormat.getDateFromTimestamp(order.getTimestamp());
        String time=DateFormat.getTimeFromTimestamp(order.getTimestamp());

        String fullText="Id:  "+ order.getOrderId()+ "\t\t" + "("+date+"\t\t"+ time+")";


        int dateStart=fullText.indexOf(date);
        int dateEnd=dateStart+date.length();

        int timeStart=fullText.indexOf(time);
        int timeEnd=timeStart+time.length();



        SpannableString spannableString=new SpannableString(fullText);

        spannableString.setSpan(new ForegroundColorSpan(Color.GRAY),dateStart,dateEnd,spannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC),timeStart,timeEnd,spannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ForegroundColorSpan(Color.GRAY),timeStart,timeEnd,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC),timeStart,timeEnd,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);



        holder.orderId.setText(spannableString);
        holder.finalorderprice.setText("Order Prce:  $" + order.getFinalPrice());
        holder.orderQuantity.setText("Status:  " + order.getOrderStatus());


        holder.cardView.setOnClickListener(v -> {


            Intent intent = new Intent(context, OrderDetialActivity.class);
            intent.putExtra("orderId", order.getOrderId());
            context.startActivity(intent);

        });

        if (order.getOrderStatus().equals("Cancelled")) {
            holder.btn_cancel.setText("Order Cancelled");
            holder.btn_cancel.setBackgroundColor(Color.RED);
            holder.btn_cancel.setEnabled(false);  // Disable button
        } else {
            holder.btn_cancel.setText("Cancel Order");
            holder.btn_cancel.setBackgroundColor(ContextCompat.getColor(context, R.color.order_Cancel));
            holder.btn_cancel.setEnabled(true);  // Enable button
        }


        holder.btn_cancel.setOnClickListener(v -> {
            if (!order.getOrderStatus().equals("Cancelled")) {


                AppConstants.showConfirmationDialog(context, "Cancel Order",
                        "Are you sure you want to cancel this order?",


                        () -> cancelOrder(order, position, holder));
            } else {

                Toast.makeText(context, "This Order is Already Cancelled", Toast.LENGTH_SHORT).show();
            }


        });


        Glide.with(context).load(R.drawable.bulk)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.errorimage)
                .into(holder.img);


    }


    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    private void cancelOrder(OrderEntity order, int position, OrderViewHolder holder) {

        Executors.newSingleThreadExecutor().execute(() -> {

            db.orderDao().updateOrderStatus("Cancelled", order.getOrderId());

            ((android.app.Activity) context).runOnUiThread(() -> {

                order.setOrderStatus("Cancelled");
                orderList.set(position, order);
                notifyItemChanged(position);


                holder.btn_cancel.setText("Order Cancelled");
                holder.btn_cancel.setBackgroundColor(Color.RED);
                holder.btn_cancel.setEnabled(false);
            });


        });
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        MaterialTextView orderId, finalorderprice, orderQuantity;
        ImageView img;
        MaterialButton btn_cancel;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.orderThumbnail);
            btn_cancel = itemView.findViewById(R.id.cancelOrder);

            cardView = itemView.findViewById(R.id.cardOrder);
            orderId = itemView.findViewById(R.id.orderId);

            finalorderprice = itemView.findViewById(R.id.finalOrderPrice);
            orderQuantity = itemView.findViewById(R.id.orderQuantity);

        }
    }
}
