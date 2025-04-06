package com.example.exe2txt;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.concurrent.Executors;

public class CheckOutActivity extends AppCompatActivity {
    private TextView title, price, discountPercentage, discountAmount, quantityText, finalAmount, finalStock, TotalcheckDiscount;
    private Button btn_confirm, btn_inc, btn_dec;

    private int productId, quantity = 1, stock;
    private String productTitle, productImage;
    private double productPrice, discountPercent, discount, finalPrice, singleItemFinalPrice, offerDiscunt = 0;

    private AppDatabase db;
    private ProductDao productDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_out);

        // UI COMPONENTS INITIALIZATION
        title = findViewById(R.id.checkoutProductTitle);
        price = findViewById(R.id.checkoutPrice);

        discountAmount = findViewById(R.id.checkoutDiscountAmount);
        quantityText = findViewById(R.id.quantityText);
        finalAmount = findViewById(R.id.checkoutFinalAmount);
        finalStock = findViewById(R.id.checkoutStock);
        TotalcheckDiscount = findViewById(R.id.totalDiscountAmount);

        btn_confirm = findViewById(R.id.confirm_button);
        btn_inc = findViewById(R.id.increaseQuantity);
        btn_dec = findViewById(R.id.decreaseQuantity);


        // Intialize database
        db = AppDatabase.getInstance(this);
        productDao = db.productDao();

        //Get productId from intent

        productId = getIntent().getIntExtra("product_id", 0);

        fetchProductDetails();
        calculatetotal();


        btn_inc.setOnClickListener(v -> {

            quantity++;
            calculatetotal();

        });

        btn_dec.setOnClickListener(v -> {

            if (quantity > 1) {
                quantity--;
                calculatetotal();
            }


        });


        btn_confirm.setOnClickListener(v -> {


            AppConstants.showConfirmationDialog(CheckOutActivity.this, "Confirm Order", "Are you sure you want to place this order", () -> placeOrder());
        });


        ///////////////////

        MaterialToolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        AppConstants.setupToolbarWithBack(this, toolbar);


        ///////////////////
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }


    private void fetchProductDetails() {


        LiveData<ProductEntity> productLiveData = productDao.getProductById(productId);


        productLiveData.observe(this, new Observer<ProductEntity>() {
            @Override
            public void onChanged(ProductEntity product) {


                if (product != null) {

                    productTitle = product.getTitle();

                    title.setText(productTitle);


                    productPrice = product.getPrice();
                    discountPercent = product.getDiscountPercentage();
                    stock = product.getStock();
                    productImage = product.getThumbnail();


                    calculatetotal();

                } else {

                    Toast.makeText(CheckOutActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });
    }


    private void calculatetotal() {


        if (quantity >= stock) {
            btn_inc.setEnabled(false); // Disable increment button
            btn_inc.setAlpha(0.5f); // Make button look disabled (optional)
        } else {
            btn_inc.setEnabled(true); // Enable increment button
            btn_inc.setAlpha(1.0f); // Restore original appearance
        }


        if (stock < quantity) {
            quantityText.setText("Out of Stock");
            quantityText.setTextColor(Color.RED);

            TotalcheckDiscount.setText("Total Discount:  $" + "N/A");

            finalAmount.setText("Grand Total:  $" + "N/A");

            discountAmount.setVisibility(View.INVISIBLE);


        } else {


            discount = (productPrice * discountPercent) / 100;
            singleItemFinalPrice = productPrice - discount;

            // function call

            finalPrice = billLevelPrice(singleItemFinalPrice * quantity);

            quantityText.setText(String.valueOf(quantity));


            TotalcheckDiscount.setText("Total Discount:  $" + String.format("%.2f", ((discount * quantity) + offerDiscunt)));
            finalAmount.setText("Grand Total:   $" + Math.round(finalPrice));

            finalStock.setText("Total Availabe stock:  " + String.valueOf(stock));

            finalStock.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));

        }


        String priceText = "Product Price:  $";
        String discountText = discountPercent + "%  off";

        String fullText = priceText + productPrice + "\t\t" + discountText;


        SpannableString spannableString = new SpannableString(fullText);

        spannableString.setSpan(new ForegroundColorSpan(Color.DKGRAY),
                0, priceText.length() + String.valueOf(productPrice).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        spannableString.setSpan(new ForegroundColorSpan(Color.RED),
                fullText.indexOf(discountText), fullText.length(),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);


        price.setText(spannableString);

    }


    private double billLevelPrice(double totalAmount) {

        if (totalAmount >= 1000) {
            int percentagediscount = (int) (totalAmount / 1000);

            discountAmount.setVisibility(View.VISIBLE);
            discountAmount.setText("Hey Flat Discount off " + "\t\t" + percentagediscount + "%" + "\t\t" + "is Available");

            offerDiscunt = (totalAmount * percentagediscount) / 100;
            return ((totalAmount - offerDiscunt));
        } else {

            return totalAmount;

        }


    }


    private void placeOrder() {


        if (quantity <= stock) {

            OrderEntity order = new OrderEntity();
            order.setTimestamp(System.currentTimeMillis());
            order.setQuantity(quantity);


            order.setDiscount(Math.round(discount * quantity));
            order.setFinalPrice(Math.round(finalPrice));
            order.setQuantity(quantity);


            order.setPrdImage(productImage);


            Executors.newSingleThreadExecutor().execute(() -> {

                db.runInTransaction(() -> {


                    long orderId = db.orderDao().inserOrder(order);

                    OrderItemEntity orderItem = new OrderItemEntity();
                    orderItem.setPriceItem(Math.round(productPrice));
                    orderItem.setDiscount(Math.round(discount));
                    orderItem.setQuantity(quantity);
                    orderItem.setProductId(productId);
                    orderItem.setProductName(db.productDao().getProductName(productId));
                    orderItem.setThumbnail(db.productDao().getProductThumbNail(productId));
                    orderItem.setOrderId((int) orderId);


                    db.orderItemDao().insertOrderItem(orderItem);


                    int newStock = stock - quantity;
                    db.productDao().updateStock(productId, newStock);

                });

                runOnUiThread(() -> {

                    stock -= quantity;
                    finalStock.setText(String.valueOf(stock));

                    Toast.makeText(this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();

                });
            });


        } else {

            Toast.makeText(this, "Out of Stock", Toast.LENGTH_SHORT).show();
        }

    }


}