package com.themtgdeckgenius.sampleapplication.SQLite;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;

import com.themtgdeckgenius.sampleapplication.SQLite.tables.PermitLookupTable;
import com.themtgdeckgenius.sampleapplication.util.FileListInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by michael.wheeler on 4/7/2015.
 */
public class PermitLookupDatabaseHelper
        extends BaseHelper{

    private static final String DATABASE_NAME = "permit_lookup.db";
    private static final int VERSION = 2;

    public PermitLookupDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        PermitLookupTable mPermitLookupTable = new PermitLookupTable();
        gIsNew = true;
        db.execSQL(mPermitLookupTable.getCreateTableStatement());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        PermitLookupTable mPermitLookupTable = new PermitLookupTable();
        gIsNew = true;
        db.execSQL(mPermitLookupTable.DELETE_TABLE);
        db.execSQL(mPermitLookupTable.getCreateTableStatement());
    }

    public void fillDatabase(Context context){
        PermitLookupTable mPermitLookupTable = new PermitLookupTable();
        long start = System.currentTimeMillis();
        SQLiteDatabase mSQLiteDatabase = this.getWritableDatabase();
        AssetManager mAssetManager = context.getAssets();
        String[] mEntry;
        BufferedReader mBufferedReader = null;
        try{
            if(!this.gIsNew){
                mSQLiteDatabase.delete(mPermitLookupTable.TABLE_NAME, null, null);
            }
            String mCurrentLine;
            mBufferedReader = new BufferedReader(new InputStreamReader(mAssetManager.open(FileListInterface.PLATE_PERMITS)));
            mSQLiteDatabase.beginTransactionNonExclusive();
            SQLiteStatement mSQLiteStatement = mSQLiteDatabase.compileStatement(mPermitLookupTable.getInsertIntoTableStatement());
            while((mCurrentLine = mBufferedReader.readLine()) != null){
                mEntry = mCurrentLine.split("\t", -1);
                mSQLiteStatement.bindString(1, mEntry[0]);
                mSQLiteStatement.bindString(2, mEntry[1]);
                mSQLiteStatement.bindString(3, mEntry[2]);
                mSQLiteStatement.bindString(4, mEntry[3]);

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

    public Cursor getPermitCursor(SQLiteDatabase db, String plate, String state){
        PermitLookupTable mPermitLookupTable = new PermitLookupTable();
        return db.query(mPermitLookupTable.TABLE_NAME, mPermitLookupTable.PROJECTION, mPermitLookupTable.WHERE_CLAUSE, new String[]{plate, state}, null, null, null);
    }
}
