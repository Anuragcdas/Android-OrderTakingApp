package com.example.exe2txt;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_table")
public class OrderEntity {

    @PrimaryKey(autoGenerate = true)
    private int orderId;
    private double discount;
    private double finalPrice;
    private int quantity;
    private long timestamp;


    @ColumnInfo(defaultValue = "Completed")
    private String orderStatus;


    private String productName;
    private double price;


    private int productId;

    private String prdImage;
    private boolean isBulkOrder;

    public boolean isBulkOrder() {
        return isBulkOrder;
    }

    public void setBulkOrder(boolean bulkOrder) {
        isBulkOrder = bulkOrder;
    }

    public String getPrdImage() {
        return prdImage;
    }

    public void setPrdImage(String prdImage) {
        this.prdImage = prdImage;
    }


    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        if (orderStatus == null) {
            this.orderStatus = "Completed";
        } else {
            this.orderStatus = orderStatus;
        }

    }
}
