package com.example.exe2txt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<ProductEntity> productList;
    private Context context;
    private List<ProductEntity> filterList;

    public ProductAdapter(List<ProductEntity> productList, Context context) {
        this.productList = productList;
        this.context = context;
        this.filterList = new ArrayList<>(productList);


    }


    public void filter(String query) {

        filterList.clear();

        if (query.isEmpty()) {
            filterList.addAll(productList);
        } else {
            for (ProductEntity product : productList) {

                if (product.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filterList.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateList(List<ProductEntity> newList) {
        this.productList = newList;
        this.filterList.clear();
        this.filterList.addAll(newList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductEntity product = filterList.get(position);
        holder.title.setText(product.getTitle());
        holder.price.setText("$" + product.getPrice());

        holder.stock.setText(product.getStock() > 0 ? "In stock" : "Out of Stock");
        holder.stock.setTextColor(product.getStock() > 0 ? Color.GREEN : Color.RED);


        Glide.with(holder.thumbnail.getContext())
                .load(product.getThumbnail())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.errorimage)
                .into(holder.thumbnail);

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, ProductDetailActivity.class);

            intent.putExtra("product_id", product.getId());
            intent.putExtra("product_price", product.getPrice());
            intent.putExtra("product_title", product.getTitle());
            intent.putExtra("product_description", product.getDescription());
            intent.putExtra("product_thumbnail", product.getThumbnail());
            intent.putExtra("product_stock", product.getStock());
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, stock;
        ImageView thumbnail;

        ProductViewHolder(View itemView) {
            super(itemView);
            stock = itemView.findViewById(R.id.productStock);
            title = itemView.findViewById(R.id.productTitle);
            price = itemView.findViewById(R.id.productPrice);
            thumbnail = itemView.findViewById(R.id.productImage);
        }
    }
}
