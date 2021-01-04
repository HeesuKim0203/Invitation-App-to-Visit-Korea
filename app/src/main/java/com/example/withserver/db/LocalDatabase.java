package com.example.withserver.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.withserver.data.Item;


@Database(entities = {Item.class}, version = 1)
public abstract class LocalDatabase extends RoomDatabase {
    public abstract Dao dao() ;

    private static LocalDatabase instance ;
    public static LocalDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(
                    context,
                    LocalDatabase.class,
                    "sqlLite")
                    .build() ;
        }

        return instance ;
    }
}
