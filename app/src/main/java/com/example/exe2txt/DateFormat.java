package com.example.exe2txt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public  class DateFormat {


    public static  String getDateFromTimestamp(long timestamp){

        SimpleDateFormat sdf= new SimpleDateFormat("dd- MM-yyyy" ,Locale.getDefault());

        return sdf.format(new Date(timestamp));
    }


    public static String getTimeFromTimestamp(long timestamp){

        SimpleDateFormat sdf= new SimpleDateFormat("hh-mm-a",Locale.getDefault());
        return  sdf.format(new Date(timestamp));
    }

}
