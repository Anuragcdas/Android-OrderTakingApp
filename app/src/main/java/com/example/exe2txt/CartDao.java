package com.example.exe2txt;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCart(CartEntity carts);

    @Query("SELECT * FROM cart_table ORDER BY timestamp DESC")
    LiveData<List<CartEntity>> getAllCart();

    @Query("SELECT * FROM cart_table WHERE cartId=:cartId LIMIT 1")
    CartEntity getCartById(int cartId);

    @Query("DELETE FROM cart_table WHERE cartId= :cartId")
    void deleteOrderBYId(int cartId);

    @Query("SELECT * FROM cart_table WHERE productId=:productId LIMIT 1")
    CartEntity getCartItemByProductId(int productId);


    @Query("SELECT stock FROM cart_table WHERE productId = :productId LIMIT 1")
    LiveData<Integer> getStockLiveData(int productId);

    @Query("DELETE FROM cart_table")
    void clearTable();

    @Query("SELECT * FROM  cart_table")
    List<CartEntity> getycartItemSync();

    @Query("UPDATE cart_table SET quantity=:newQuantity WHERE cartId= :id")
    void updateQuantity(int newQuantity, int id);


}
