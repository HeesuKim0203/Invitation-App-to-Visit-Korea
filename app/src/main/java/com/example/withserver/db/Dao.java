package com.example.withserver.db;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverter;

import com.example.withserver.data.Item;

import java.util.List;

@androidx.room.Dao
public interface Dao {
    @Insert
    long insertLocal(Item item) ;

    @Query("SELECT * FROM items")
    @TypeConverter
    List<Item> getData() ;

    @Query("SELECT * FROM items WHERE title=:title")
    Item get(String title) ;

    @Delete
    int delete(Item item) ;


}
