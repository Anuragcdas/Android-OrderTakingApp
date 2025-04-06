package com.example.exe2txt;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.concurrent.Executors;

public class BulkCheckOutActivity extends AppCompatActivity {

    MaterialTextView txtActualPrice, txtTotalPrice, txtTotalDiscount, txtQuantity, txtFlatdiscount;
    MaterialButton btn_confirm;
    private double actualtotaPrice, finalPrice, totalDiscount, grandoffer = 0;
    private int actualtotalPriceInt, finalPriceInt, totDiscountInt;
    private int quantity;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bulk_check_out);


        //UI COMPONETS INITIALISATION

        txtActualPrice = findViewById(R.id.totalActualBulkPrice);
        txtTotalPrice = findViewById(R.id.totalBulkPrice);
        txtTotalDiscount = findViewById(R.id.totalBulkDiscount);
        txtQuantity = findViewById(R.id.totalBulkProducts);
        txtFlatdiscount = findViewById(R.id.flatDiscount);
        btn_confirm = findViewById(R.id.ConfirmButtom);


        db = AppDatabase.getInstance(this);


        loadCartItems();


        btn_confirm.setOnClickListener(v -> {

            AppConstants.showConfirmationDialog(this, "Place Order", "Are you sure you want to place this order?", () ->

                    placeOrder());
        });


        MaterialToolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        AppConstants.setupToolbarWithBack(this, toolbar);


        ///////////t///////////////////////////
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void loadCartItems() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CartEntity> carts = db.cartDao().getycartItemSync();

            double price;
            double discount = 0;
            totalDiscount = 0;
            actualtotaPrice = 0;
            finalPrice = 0;
            quantity = 0;

            for (CartEntity cart : carts) {

                ProductEntity product = db.productDao().getProductByIdSync(cart.getProductId());

                price = product.getPrice() * (1 - product.getDiscountPercentage() / 100);
                discount = product.getPrice() - price;

                totalDiscount += discount * cart.getQuantity();
                actualtotaPrice += product.getPrice() * cart.getQuantity();
                finalPrice += price * cart.getQuantity();
                quantity += cart.getQuantity();


            }


            finalPrice = billLevelPrice(finalPrice);
            totalDiscount += grandoffer;// Apply grand offer discount

            // ✅ Update UI on Main Thread
            runOnUiThread(() -> {
                txtActualPrice.setText("Actual Total Price of Products:  $" + Math.round(actualtotaPrice));
                txtQuantity.setText("Total no of Products:  " + quantity);
                txtTotalDiscount.setText("Total Discount Applied:  $" + Math.round(totalDiscount + grandoffer));
                txtTotalPrice.setText("Grand Total:  $" + Math.round(finalPrice));
            });
        });
    }


    private double billLevelPrice(double totalBillAmount) {
        if (totalBillAmount > 1000) {
            int billDicountPercent = (int) totalBillAmount / 1000;

            grandoffer = (totalBillAmount * billDicountPercent) / 100;

            txtFlatdiscount.setText("Hey flat offer of" + billDicountPercent + "%  is Availabe !!");
            txtFlatdiscount.setVisibility(View.VISIBLE);

            return totalBillAmount - grandoffer;

        } else {

            return totalBillAmount;
        }


    }


    private void placeOrder() {


        Executors.newSingleThreadExecutor().execute(() -> {


            OrderEntity order = new OrderEntity();

            order.setQuantity(quantity);
            order.setFinalPrice(Math.round(finalPrice));
            order.setDiscount(Math.round(totalDiscount));
            order.setTimestamp(System.currentTimeMillis());

            long orderId = db.orderDao().inserOrder(order);

            List<CartEntity> carts = db.cartDao().getycartItemSync();

            for (CartEntity cart : carts) {

                ProductEntity product = db.productDao().getProductByIdSync(cart.getProductId());

                OrderItemEntity orderItem = new OrderItemEntity();
                orderItem.setOrderId((int) orderId);
                orderItem.setProductName(product.getTitle());
                orderItem.setThumbnail(product.getThumbnail());
                orderItem.setPriceItem(product.getPrice());
                orderItem.setQuantity(cart.getQuantity());
                orderItem.setTimestamp(System.currentTimeMillis());
                db.orderItemDao().insertOrderItem(orderItem);


            }

            db.cartDao().clearTable();

            runOnUiThread(() -> {
                Toast.makeText(this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
            });

            finish();
        });


    }
}