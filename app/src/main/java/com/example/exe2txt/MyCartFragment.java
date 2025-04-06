package com.example.exe2txt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.Executors;


public class MyCartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private AppDatabase appDatabase;
    private View view;
    private TextView noCartText, total_price;
    private ImageView noCartImage;
    private CardView card;
    private Button btn_buyTotal;
    private int totalAmountInt;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        // UI COMPONENTS INITIALISATION
        view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        recyclerView = view.findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        cartAdapter = new CartAdapter(requireContext(), null);
        recyclerView.setAdapter(cartAdapter);

        noCartImage = view.findViewById(R.id.noCartImage);
        noCartText = view.findViewById(R.id.noCartText);

        total_price = view.findViewById(R.id.totalPriceTextView);
        btn_buyTotal = view.findViewById(R.id.buyAllButton);

        card = view.findViewById(R.id.cartCardView);

        appDatabase = AppDatabase.getInstance(requireContext());

        cartAdapter.setCartPriceListener(totalPrice -> {


            total_price.setText("Total Price: $" + (int) Math.round(totalPrice));

        });


        btn_buyTotal.setOnClickListener(v -> {
            palceTotalOrder();
        });


        appDatabase.cartDao().getAllCart().observe(getViewLifecycleOwner(), carts -> {
            if (carts != null && !carts.isEmpty()) {

                cartAdapter.setCarts(carts);
                card.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                noCartImage.setVisibility(View.GONE);
                noCartText.setVisibility(View.GONE);
            } else {
                card.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noCartImage.setVisibility(View.VISIBLE);
                noCartText.setVisibility(View.VISIBLE);
            }
        });





        return view;


    }

    private void palceTotalOrder() {


        Executors.newSingleThreadExecutor().execute(() -> {

            List<CartEntity> cartItems = appDatabase.cartDao().getycartItemSync();


            double actualtotal = 0;
            double totalPrice = 0;
            double totalDiscount = 0;
            int totalQuantity = 0;


            for (CartEntity cart : cartItems) {

                ProductEntity product = appDatabase.productDao().getProductByIdSync(cart.getProductId());

                if (product != null) {

                    double discount = (cart.getPrice() * product.getDiscountPercentage()) / 100;
                    double finalPrice = (cart.getPrice() - discount) * cart.getQuantity();

                    actualtotal += cart.getPrice();

                    Log.d("", "palceTotalOrder: " + actualtotal);
                    totalPrice += finalPrice;
                    totalDiscount += (discount * cart.getQuantity());
                    totalQuantity += cart.getQuantity();
                }


            }


            if (totalQuantity > 0) {


                Intent check = new Intent(requireContext(), BulkCheckOutActivity.class);

                check.putExtra("actualtotalPrice", actualtotal);
                check.putExtra("totalDiscount", totalDiscount);
                check.putExtra("isBulkOrder", true);
                check.putExtra("totalPrice", totalPrice);
                check.putExtra("totalQuantity", totalQuantity);
                startActivity(check);


            } else {

                requireActivity().runOnUiThread(() -> {

                    Toast.makeText(requireContext(), "No valid item to order..", Toast.LENGTH_SHORT).show();
                });


            }


        });
    }


}