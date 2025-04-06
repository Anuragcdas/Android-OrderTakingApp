package com.example.exe2txt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {


    private Context context;
    private List<CartEntity> cartList;
    private int productId;
    private ProductDao productDao;
    private AppDatabase db;
    private ProductEntity product;
    private CartPriceListener priceListener;

    private List<String> quantites = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "More"));

    public CartAdapter(Context context, List<CartEntity> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    public void setCartPriceListener(CartPriceListener listener) {

        this.priceListener = listener;
    }

    private void updateTotalPrice() {

        double totalPrice = 0.0;

        for (CartEntity cart : cartList) {

            totalPrice += cart.getPrice() * cart.getQuantity();
        }

        if (priceListener != null) {

            priceListener.onTotalPriceUpdated(totalPrice);
        }


    }

    public void setCarts(List<CartEntity> carts) {
        this.cartList = carts;
        updateTotalPrice();

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

        CartEntity cart = cartList.get(position);
        productId = cart.getProductId();

        db = AppDatabase.getInstance(context);
        productDao = db.productDao();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, quantites);
        holder.spinner.setAdapter(adapter);

        int quantity = cart.getQuantity();


        if (quantites.contains(String.valueOf(quantity))) {
            holder.spinner.setSelection(quantites.indexOf(String.valueOf(quantity)));
        } else {

            holder.spinner.setSelection(quantites.indexOf("More"));
        }

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            boolean isUserInteraction = false;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                // ignores first auto-call from setSelection
                if (!isUserInteraction) {
                    isUserInteraction = true;
                    return;
                }

                String selectedValue = quantites.get(position);

                if (!selectedValue.equalsIgnoreCase("More")) {

                    try {

                        int newQuantity = Integer.parseInt(selectedValue);
                        Executors.newSingleThreadExecutor().execute(() -> {

                            db.cartDao().updateQuantity(newQuantity, cart.getCartId());


                        });


                    } catch (NumberFormatException e) {
                        throw new RuntimeException(e);
                    }


                } else {

                    showQuantityDialog(holder, cart);


                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Executors.newSingleThreadExecutor().execute(() -> {

            product = productDao.getProductByIdSync(productId);

            if (product != null) {

                ((Activity) context).runOnUiThread(() -> {
                    //  holder.productStock.setText(product.getStock() > 0 ? "In Stock" : "Out Of Stock");
                    // holder.productStock.setTextColor(product.getStock() > 0 ? Color.GRAY : Color.RED);
                });


            }


        });


        holder.btn_buy.setOnClickListener(v -> {

            Intent check = new Intent(context, CheckOutActivity.class);
            check.putExtra("product_id", cart.getProductId());
            context.startActivity(check);


        });


        //  holder.productStock.setText( cart.getStock() >0 ? "In Stock" : "Out Of Stock");
        //  holder.productStock.setTextColor(cart.getStock()>0 ? Color.GRAY : Color.RED);


        holder.productName.setText(cart.getProductName());
        holder.productPrice.setText(" $" + cart.getPrice());

        holder.productPrice.setTextColor(ContextCompat.getColor(context, R.color.light_blue_900));


        Glide.with(context)
                .load(cart.getThumbnail())
                .error(R.drawable.errorimage)
                .placeholder(R.drawable.placeholder)

                .into(holder.productImage);


        holder.btn_remove.setOnClickListener(v -> {

            AppConstants.showConfirmationDialog(context, "Remove Cart Item", "Are you sure you want to remove item ?", () -> {


                removecart(cart, position, holder);
            });

        });


    }

    @Override
    public int getItemCount() {
        return cartList == null ? 0 : cartList.size();
    }

    private void removecart(CartEntity cart, int position, CartViewHolder holder) {

        db = AppDatabase.getInstance(context);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            db.cartDao().deleteOrderBYId(cart.getCartId());


            ((Activity) context).runOnUiThread(() -> {
                int currentPosition = holder.getBindingAdapterPosition(); // Use this instead of getAdapterPosition()
                if (currentPosition != RecyclerView.NO_POSITION && currentPosition < cartList.size()) {

                    cartList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, cartList.size());
                }
            });
        });

    }

    private void showQuantityDialog(CartViewHolder holder, CartEntity cartItem) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Quantity");

        final EditText input = new EditText(context);

        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setView(input);


        builder.setPositiveButton("OK", ((dialog, which) -> {

            try {


                String enteredValue = input.getText().toString();

                if (enteredValue.isEmpty()) {

                    //If no value is entered, reset spinner selection

                    ((Activity) context).runOnUiThread(() -> {
                        holder.spinner.setSelection(0, false);

                        holder.spinner.postDelayed(() -> {

                            holder.spinner.setSelection(quantites.indexOf("More"), false);

                        }, 200);
                    });

                    return;


                }

                int customQuantity = Integer.parseInt(enteredValue);

                if (customQuantity > 0) {

                    Executors.newSingleThreadExecutor().execute(() -> {

                        db.cartDao().updateQuantity(customQuantity, cartItem.getCartId());

                        ((Activity) context).runOnUiThread(() -> {

                            //Clear any previous custom values

                            if (!quantites.contains(String.valueOf(customQuantity))) {

                                quantites.add(quantites.size() - 1, String.valueOf(customQuantity));
                            }

                            //force update the Spinner UI
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, quantites);

                            holder.spinner.setAdapter(adapter);
                            holder.spinner.setSelection(quantites.indexOf(String.valueOf(customQuantity)));

                            updateTotalPrice();
                            notifyDataSetChanged();


                        });
                    });


                }


            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }


        }));


        builder.setNegativeButton("Cancel", ((dialog, which) -> {

            dialog.dismiss();


            ((Activity) context).runOnUiThread(() -> {

                holder.spinner.setSelection(quantites.indexOf(String.valueOf(cartItem.getQuantity())), false);

            });
        }));


        builder.show();

    }

    public interface CartPriceListener {

        void onTotalPriceUpdated(double totalPrice);
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {


        MaterialCardView cardView;
        ImageView productImage;
        MaterialTextView productName, productPrice, productStock;
        MaterialButton btn_remove, btn_buy;
        Spinner spinner;


        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardCart);
            productImage = itemView.findViewById(R.id.cartThumbnail);
            productName = itemView.findViewById(R.id.cartName);
            productPrice = itemView.findViewById(R.id.cartPrice);
            productStock = itemView.findViewById(R.id.cartStock);
            btn_buy = itemView.findViewById(R.id.buyNowButton);
            btn_remove = itemView.findViewById(R.id.removeFromCartButton);
            spinner = itemView.findViewById(R.id.quantitySpinner);

        }
    }


}
