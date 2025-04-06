package com.example.exe2txt;
// for Storing data in Offline mode

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {ProductEntity.class, OrderEntity.class, CartEntity.class, OrderItemEntity.class}, version = 7, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_databse")
                    .fallbackToDestructiveMigration()
                    .build();

        }

        //Automatically delete old data and recreate the database

        return instance;
    }

    public abstract ProductDao productDao();

    public abstract OrderDao orderDao();

    public abstract CartDao cartDao();

    public abstract OrderItemDao orderItemDao();


//    /////////////////
//
//    public static final Migration MIGRATION_2_3 = new Migration(2,3) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//
//            database.execSQL("ALTER TABLE order_table ADD COLUMN prdImage TEXT");
//
//        }
//    };
//


    ///////////////////

    //Synchronised prevent multiple threads creating seperate instance


}









