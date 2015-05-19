package com.gui.royal.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jeremy on 2015/5/17.
 * 创建数据库类
 * 继承SQLiteOpenHelper类，实现onCreate（）和onUpgrade（）方法
 */
public class CoolWeatherOpenHelp extends SQLiteOpenHelper {
    /**
     * Province的建表语句
     */
    public static final String CREATE_PROVINCE = "create table Province ("
            + "id integer primary key autoincrement, "
            + "province_name text, "
            + "province_code text) ";

    /**
     * City 的建表语句
     */
    public static final String CREATE_CITY = "create table City ("
            + "id integer primary key autoincrement, "
            + "city_name text, "
            + "city_code text, "
            + "province_id integer) ";

    public static final String CREATE_COUNTY = "create table County ("
            + "id integer primary key autoincrement, "
            + "county_name text, "
            + "county_code text, "
            + "city_id integer) ";



    public CoolWeatherOpenHelp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * 创建数据库表：Province  City  County
     * @param db SQLiteDatabase,可对数据库进行读写操作的对象
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE); //创建Province表
        db.execSQL(CREATE_CITY); //创建City表
        db.execSQL(CREATE_COUNTY); //创建County表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
