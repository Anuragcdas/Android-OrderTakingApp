package com.example.exe2txt;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class OrderDetialActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detial);


        int orderId = getIntent().getIntExtra("orderId", 0);


        RecyclerView recyclerView = findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(this, null);

        recyclerView.setAdapter(orderDetailAdapter);

        AppDatabase appDatabase = AppDatabase.getInstance(this);


        appDatabase.orderItemDao().getOrderById(orderId).observe(this, new Observer<List<OrderItemEntity>>() {
            @Override
            public void onChanged(List<OrderItemEntity> itemEntityList) {

                orderDetailAdapter.setOrders(itemEntityList);

            }
        });


        MaterialToolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        AppConstants.setupToolbarWithBack(this, toolbar);


    }
}