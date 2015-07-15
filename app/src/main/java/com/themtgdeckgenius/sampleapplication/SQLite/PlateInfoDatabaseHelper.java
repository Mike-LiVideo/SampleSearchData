package com.themtgdeckgenius.sampleapplication.SQLite;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.themtgdeckgenius.sampleapplication.SQLite.tables.PlateInfoTable;
import com.themtgdeckgenius.sampleapplication.util.FileListInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by michael.wheeler on 4/7/2015.
 */
public class PlateInfoDatabaseHelper
        extends BaseHelper{

    private static final String DATABASE_NAME = "plate_info.db";
    private static final int VERSION = 2;

    public PlateInfoDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        PlateInfoTable mPlateInfoTable = new PlateInfoTable();
        gIsNew = true;
        db.execSQL(mPlateInfoTable.getCreateTableStatement());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        PlateInfoTable mPlateInfoTable = new PlateInfoTable();
        gIsNew = true;
        db.execSQL(mPlateInfoTable.DELETE_TABLE);
        db.execSQL(mPlateInfoTable.getCreateTableStatement());
    }

    public void fillDatabase(Context context){
        PlateInfoTable mPlateInfoTable = new PlateInfoTable();
        long start = System.currentTimeMillis();
        AssetManager mAssetManager = context.getAssets();
        SQLiteDatabase mSQLiteDatabase = this.getWritableDatabase();
        String[] mEntry;
        BufferedReader mBufferedReader = null;
        try{
            if(!this.gIsNew){
                mSQLiteDatabase.delete(mPlateInfoTable.TABLE_NAME, null, null);
            }
            String mCurrentLine;
            mBufferedReader = new BufferedReader(new InputStreamReader(mAssetManager.open(FileListInterface.PLATE_INFO)));
            mSQLiteDatabase.beginTransactionNonExclusive();
            SQLiteStatement mSQLiteStatement = mSQLiteDatabase.compileStatement(mPlateInfoTable.getInsertIntoTableStatement());
            while((mCurrentLine = mBufferedReader.readLine()) != null){
                mEntry = mCurrentLine.split("\t", -1);
                mSQLiteStatement.bindString(1, mEntry[0]);
                mSQLiteStatement.bindString(2, mEntry[1]);
                mSQLiteStatement.bindString(3, mEntry[2]);
                mSQLiteStatement.bindString(4, mEntry[3]);
                mSQLiteStatement.bindString(5, mEntry[4]);
                mSQLiteStatement.bindString(6, mEntry[5]);

                mSQLiteStatement.execute();
                mSQLiteStatement.clearBindings();
            }
            mSQLiteDatabase.setTransactionSuccessful();
            mSQLiteDatabase.endTransaction();
            mSQLiteDatabase.close();

        } catch(IOException e){
            e.printStackTrace();
        } finally{
            try{
                if(mBufferedReader != null){
                    mBufferedReader.close();
                }
            } catch(IOException e){
                e.printStackTrace();
            }

        }
        long end = System.currentTimeMillis();

        double time = (end - start) / 1000.00;
    }

    public Cursor getPlateInfoCursor(SQLiteDatabase db, String plate, String state){
        PlateInfoTable mPlateInfoTable = new PlateInfoTable();
        return db.query(mPlateInfoTable.TABLE_NAME, mPlateInfoTable.PROJECTION, mPlateInfoTable.WHERE_CLAUSE, new String[]{plate, state}, null, null, null);
    }

}
