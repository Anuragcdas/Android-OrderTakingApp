package com.example.exe2txt;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OrderItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrderItem(OrderItemEntity orderItem);

    @Query("SELECT * FROM order_items ORDER BY timestamp DESC")
    LiveData<List<OrderItemEntity>> getAllOrderItem();

    @Query("SELECT * FROM order_items WHERE orderId=:orderId")
    LiveData<List<OrderItemEntity>> getOrderById(int orderId);

    @Query("DELETE  FROM order_items ")
    void clearTable();


}
