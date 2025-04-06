package com.example.exe2txt;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OrderDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long inserOrder(OrderEntity order);

    @Query("SELECT * FROM order_table ORDER BY timestamp DESC")
    LiveData<List<OrderEntity>> getAllOrders();

    @Query("SELECT * FROM order_table WHERE orderId=:orderId  LIMIT 1")
    OrderEntity getOrderBYId(int orderId);


    @Query("UPDATE order_table SET orderStatus=:newStatus WHERE  orderId=:id  ")
    void updateOrderStatus(String newStatus, int id);


    @Query("DELETE FROM order_table WHERE orderId = :orderId")
    void deleteOrderById(int orderId);

    @Query("DELETE FROM order_table")
    void clearTable();


}
