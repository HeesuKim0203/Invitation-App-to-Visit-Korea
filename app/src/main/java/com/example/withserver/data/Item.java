package com.example.withserver.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "items")
public class Item implements Serializable {
    public String addr ;
    public String areacode ;
    public String contentid ;
    public String createdtime ;
    public String mainimage ;
    public String modifiedtime ;
    public String readcount ;
    public String sigungucode ;
    public String summary ;
    public String tel ;
    public String telname ;
    @PrimaryKey
    @NonNull
    public String title ;

}
