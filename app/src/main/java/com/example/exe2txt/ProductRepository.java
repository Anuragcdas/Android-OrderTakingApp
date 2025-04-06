package com.example.exe2txt;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LiveData;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductRepository {

    private static final String API_URL = "https://dummyjson.com/products";
    public final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ProductDao productDao;
    private final LiveData<List<ProductEntity>> productLiveData;
    private View view;


    public ProductRepository(Context context) {

        AppDatabase db = AppDatabase.getInstance(context);

        this.productDao = db.productDao();
        this.productLiveData = productDao.getAllProducts();


    }

    public View getView() {
        return view;
    }

    public LiveData<List<ProductEntity>> getAllProducts() {
        return productLiveData;
    }

    public void fetchProducts(Context context, View rootView, RepositoryCallback<List<ProductEntity>> callback) {

        if (!NetworkUtils.isNetworkAvailable(context)) {
            NetworkUtils.handleNetworkError(context, rootView, new VolleyError("No Internet Connected !"), callback);
            return;
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL, null,


                response -> {

                    List<ProductEntity> productList = new ArrayList<>();

                    try {
                        JSONArray productArray = response.getJSONArray("products");

                        for (int i = 0; i < productArray.length(); i++) {
                            JSONObject obj = productArray.getJSONObject(i);

                            ProductEntity product = new ProductEntity();

                            product.setId(obj.getInt("id"));
                            product.setTitle(obj.getString("title"));
                            product.setDescription(obj.getString("description"));
                            product.setPrice(obj.getDouble("price"));
                            product.setDiscountPercentage(obj.getDouble("discountPercentage"));
                            product.setThumbnail(obj.getString("thumbnail"));
                            product.setStock(obj.getInt("stock"));
                            productList.add(product);

                        }

                        saveProductsToDB(productList);
                        callback.onSucess(productList);


                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                },

                error ->
                {

                    NetworkUtils.handleNetworkError(context, rootView, error, callback);

                }


        );

        ApiHelper.getInstance(context).addToRequestQueue(jsonObjectRequest);


    }

    private void saveProductsToDB(List<ProductEntity> productList) {

        executorService.execute(() -> {

            productDao.insertAll(productList);
        });


    }

    public void clearAllProducts() {

        executorService.execute(() -> {
            productDao.clearTable();
        });
    }


    public interface ProductCallback {

        void onSuccess(List<ProductEntity> productList);

        void onError(String errorMessage);
    }


}
