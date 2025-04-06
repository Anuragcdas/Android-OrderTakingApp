package com.example.exe2txt;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ApiHelper {

    private static ApiHelper instance;
    private static Context contx;
    private RequestQueue requestQueue;

    private ApiHelper(Context context) {

        contx = context.getApplicationContext();

        requestQueue = Volley.newRequestQueue(contx);


    }


    public static synchronized ApiHelper getInstance(Context context) {


        if (instance == null) {
            instance = new ApiHelper(context);
        }

        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
