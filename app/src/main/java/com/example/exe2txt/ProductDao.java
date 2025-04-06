package com.example.exe2txt;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM product_table")
    LiveData<List<ProductEntity>> getAllProducts();

    @Query("SELECT * FROM product_table WHERE id= :productId")
    LiveData<ProductEntity> getProductById(int productId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ProductEntity> products);

    @Query("DELETE FROM product_table")
    void clearTable();

    @Update
    void updateProduct(ProductEntity product);

    @Query("UPDATE product_table SET stock = :newStock WHERE id= :productId")
    void updateStock(int productId, int newStock);

    @Query("SELECT * FROM product_table WHERE id = :productId")
    ProductEntity getProductByIdSync(int productId);


    @Query("SELECT stock FROM product_table WHERE id = :productId")
    int getStockByProductId(int productId);

    @Query("SELECT title FROM product_table WHERE id=:productId")
    String getProductName(int productId);


    @Query("SELECT thumbnail FROM  product_table WHERE id=:productId")
    String getProductThumbNail(int productId);

}
