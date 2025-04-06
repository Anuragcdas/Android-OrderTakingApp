package com.example.exe2txt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;


public class MyOrderFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private AppDatabase appDatabase;
    private View view;
    private TextView noOrdersText;
    private ImageView noOrderImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_order, container, false);


        recyclerView = view.findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        orderAdapter = new OrderAdapter(requireContext(), null);
        recyclerView.setAdapter(orderAdapter);
        noOrdersText = view.findViewById(R.id.noOrdersText);
        noOrderImage = view.findViewById(R.id.noOrderImage);


        appDatabase = AppDatabase.getInstance(requireContext());


        loadOrders(null);


        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);


        toolbar.getMenu().findItem(R.id.action_filter).getIcon()
                .setTint(ContextCompat.getColor(requireContext(), R.color.white));


        toolbar.setOnMenuItemClickListener(item -> {


            if (item.getItemId() == R.id.action_filter) {


                showFilterMenu(toolbar);

                return true;
            } else {
                return false;
            }
        });


        return view;


    }

    private void loadOrders(@Nullable String status) {

        appDatabase.orderDao().getAllOrders().observe(getViewLifecycleOwner(), orders -> {

            if (orders != null && !orders.isEmpty()) {

                if (status != null) {

                    List<OrderEntity> filteredOrders = new ArrayList<>();

                    for (OrderEntity order : orders) {

                        if (order.getOrderStatus().equals(status)) {
                            filteredOrders.add(order);
                        }


                    }


                    orderAdapter.setOrders(filteredOrders);

                } else {
                    orderAdapter.setOrders(orders);


                }


                recyclerView.setVisibility(View.VISIBLE);
                noOrdersText.setVisibility(View.GONE);
                noOrderImage.setVisibility(View.GONE);


            } else {

                recyclerView.setVisibility(View.GONE);
                noOrderImage.setVisibility(View.VISIBLE);
                noOrdersText.setVisibility(View.VISIBLE);


            }


        });


    }


    private void showFilterMenu(View anchor) {

        PopupMenu popupMenu = new PopupMenu(requireContext(), anchor);

        popupMenu.getMenu().add("All");
        popupMenu.getMenu().add("Completed");
        popupMenu.getMenu().add("Cancelled");

        popupMenu.setOnMenuItemClickListener(item -> {

            String selectedStatus = item.getTitle().toString();

            if (selectedStatus.equals("All")) {
                loadOrders(null);
            } else {


                loadOrders(selectedStatus);

            }

            return true;
        });
        popupMenu.show();

    }


}