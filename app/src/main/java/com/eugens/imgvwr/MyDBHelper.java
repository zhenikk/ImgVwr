package com.eugens.imgvwr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Eugen on 04.03.2015.
 */
public class MyDBHelper extends SQLiteOpenHelper{


    private final static String IMAGES_TABLE_NAME = "images";
    private final static String ID_COL = "_id";
    private final static String IMAGE_COL = "image";



    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + IMAGES_TABLE_NAME+" ( "+ID_COL+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                IMAGE_COL + " TEXT);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
