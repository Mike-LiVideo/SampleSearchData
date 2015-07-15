package com.themtgdeckgenius.sampleapplication.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by michael.wheeler on 4/10/2015.
 */
public abstract class BaseHelper
        extends SQLiteOpenHelper
        implements SQLiteStatementsInterface{

    public boolean gIsNew = false;

    public BaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }
}
