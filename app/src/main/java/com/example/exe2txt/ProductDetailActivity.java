package com.example.exe2txt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.concurrent.Executors;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView title, price, description, stockTextView;
    private ImageView thumbnail;
    private ProgressBar progressBar;
    private int stock;
    private Button btn_buy, btn_addCart;

    private int productId;
    private ProductEntity product;
    private String productName, productThumbnail;
    private double productPrice;
    private AppDatabase db;

    private int quantity = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        // Initialize UI components
        title = findViewById(R.id.detailTitle);
        price = findViewById(R.id.dettailProductPrice);
        description = findViewById(R.id.detailProductDescription);
        thumbnail = findViewById(R.id.detailProductImage);
        btn_buy = findViewById(R.id.buyNowButton);
        btn_addCart = findViewById(R.id.addToCartButton);

        // Retrieve productId from Intent
        productId = getIntent().getIntExtra("product_id", 0);

        // Initialize the database and DAO
        db = AppDatabase.getInstance(this);
        ProductDao productDao = db.productDao();


        // Fetch product details from the database asynchronously
        Executors.newSingleThreadExecutor().execute(() -> {
            product = productDao.getProductByIdSync(productId);

            if (product != null) {
                runOnUiThread(() -> {
                    title.setText(product.getTitle());
                    price.setText("$" + product.getPrice());
                    description.setText(product.getDescription());
                    stock = product.getStock();

                    productPrice = product.getPrice();
                    productName = product.getTitle();
                    productThumbnail = product.getThumbnail();

                    // Load product image with Glide
                    Glide.with(this)
                            .load(product.getThumbnail())
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.errorimage)
                            .into(thumbnail);
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Product Not Found", Toast.LENGTH_SHORT).show());
            }
        });


        // Buy button click listener
        btn_buy.setOnClickListener(v -> buyProduct());

        btn_addCart.setOnClickListener(v -> AddCart());


        MaterialToolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        AppConstants.setupToolbarWithBack(this, toolbar);


        // Apply window insets for immersive UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    // Navigate to the checkout page
    private void buyProduct() {
        Intent check = new Intent(this, CheckOutActivity.class);
        check.putExtra("product_id", productId);
        startActivity(check);
    }


    private void AddCart() {

        db.productDao().getProductById(productId).observe(this, productEntity -> {

            if (productEntity != null) {
                int liveStock = productEntity.getStock();

                if (liveStock <= 0) {

                    Toast.makeText(this, "Sorry ,the product is out of stock", Toast.LENGTH_SHORT).show();
                } else {
                    addcart(liveStock);
                }

            } else {

                Toast.makeText(this, "Product Not Found", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void addcart(int liveStock) {

        CartEntity cart = new CartEntity();

        cart.setProductId(productId);
        cart.setPrice(productPrice);
        cart.setProductName(productName);
        cart.setThumbnail(productThumbnail);
        cart.setStock(stock);
        cart.setQuantity(quantity);


        cart.setTimestamp(System.currentTimeMillis());

        Executors.newSingleThreadExecutor().execute(() -> {

            CartEntity existingCartItem = db.cartDao().getCartItemByProductId(productId);

            if (existingCartItem == null) {

                db.runInTransaction(() -> {

                    db.cartDao().insertCart(cart);

                });

                runOnUiThread(() -> {
                    Toast.makeText(this, "Cart Added Successfully....", Toast.LENGTH_SHORT).show();

                });


            } else {

                runOnUiThread(() -> {
                    Toast.makeText(this, "Item is Already in the Cart...", Toast.LENGTH_SHORT).show();
                });
            }


        });

    }
}
