package com.themtgdeckgenius.sampleapplication.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.themtgdeckgenius.sampleapplication.R;
import com.themtgdeckgenius.sampleapplication.SQLite.PermitLookupDatabaseHelper;
import com.themtgdeckgenius.sampleapplication.SQLite.PlateInfoDatabaseHelper;
import com.themtgdeckgenius.sampleapplication.SQLite.RepeatOffendersDatabaseHelper;
import com.themtgdeckgenius.sampleapplication.SQLite.tables.PermitLookupTable;
import com.themtgdeckgenius.sampleapplication.SQLite.tables.PlateInfoTable;
import com.themtgdeckgenius.sampleapplication.SQLite.tables.RepeatOffenderTable;

/**
 * Created by michael.wheeler on 7/20/2015.
 */
public class ButtonActions{

    public static void checkForPlateInfo(final String aPlate, final String aState, final Context context){
        PlateInfoDatabaseHelper mPlateInfoDatabaseHelper = new PlateInfoDatabaseHelper(context);
        SQLiteDatabase mPlateSQLiteDatabase = mPlateInfoDatabaseHelper.getReadableDatabase();
        Cursor mPlateInfoCursor = mPlateInfoDatabaseHelper.getPlateInfoCursor(mPlateSQLiteDatabase, aPlate, aState);
        PlateInfoTable mPlateInfoTable = new PlateInfoTable();
        if(mPlateInfoCursor.moveToFirst()){
            String vin = mPlateInfoCursor.getString(mPlateInfoCursor.getColumnIndex(mPlateInfoTable.getColumnName(3)));
            String make = mPlateInfoCursor.getString(mPlateInfoCursor.getColumnIndex(mPlateInfoTable.getColumnName(4)));
            String type = mPlateInfoCursor.getString(mPlateInfoCursor.getColumnIndex(mPlateInfoTable.getColumnName(5)));
            String color = mPlateInfoCursor.getString(mPlateInfoCursor.getColumnIndex(mPlateInfoTable.getColumnName(6)));
            final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();
            mAlertDialog.setTitle(R.string.plate_info_found);
            StringBuilder mStringBuilder = new StringBuilder();
            mStringBuilder.append("Vin: ").append(vin).append("\n");
            mStringBuilder.append("Make: ").append(make).append("\n");
            mStringBuilder.append("Model: ").append(type).append("\n");
            mStringBuilder.append("Color: ").append(color).append("\n");
            mAlertDialog.setMessage(mStringBuilder);
            mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    mAlertDialog.dismiss();
                }
            });
            mAlertDialog.show();
        }
        else{
            final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();
            mAlertDialog.setTitle(R.string.no_plate_info_found);
            mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    mAlertDialog.dismiss();
                }
            });
            mAlertDialog.show();
        }
    }

    public static void checkPermits(final String aPlate, final String aState, final Context context){

        PermitLookupDatabaseHelper mPermitLookupDatabaseHelper = new PermitLookupDatabaseHelper(context);
        SQLiteDatabase mSQLitePermitDatabase = mPermitLookupDatabaseHelper.getReadableDatabase();
        PermitLookupTable mPermitLookupTable = new PermitLookupTable();
        StringBuilder mPermitStringBuilder = new StringBuilder();
        mPermitStringBuilder.append(aPlate);
        mPermitStringBuilder.append("-");
        mPermitStringBuilder.append(aState);
        mPermitStringBuilder.append(", ");
        Cursor mPermitCursor = mPermitLookupDatabaseHelper.getPermitCursor(mSQLitePermitDatabase, aPlate, aState);
        if(mPermitCursor.moveToFirst()){
            mPermitStringBuilder.append(mPermitCursor.getString(mPermitCursor.getColumnIndex(mPermitLookupTable.getColumnName(3))));
            mPermitStringBuilder.append(" - ");
            mPermitStringBuilder.append(mPermitCursor.getString(mPermitCursor.getColumnIndex(mPermitLookupTable.getColumnName(4))));
            final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();
            mAlertDialog.setTitle(R.string.permit_tital);
            mAlertDialog.setMessage(mPermitStringBuilder);
            mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    mAlertDialog.dismiss();
                }
            });
            mAlertDialog.show();
        }
        else{
            final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();
            mAlertDialog.setTitle(R.string.no_permit_found);
            mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    mAlertDialog.dismiss();
                }
            });
            mAlertDialog.show();
        }
    }

    public static void checkRepeatOffenders(final String aPlate, final String aState, final Context context){
        RepeatOffendersDatabaseHelper mRepeatOffendersDatabaseHelper = new RepeatOffendersDatabaseHelper(context);
        SQLiteDatabase mSQLiteDatabase = mRepeatOffendersDatabaseHelper.getReadableDatabase();
        RepeatOffenderTable mRepeatOffenderTable = new RepeatOffenderTable();
        int numberOfViolations = mRepeatOffendersDatabaseHelper.getViolationsCount(mSQLiteDatabase, aState, aPlate);
        if(numberOfViolations > 0){
            Cursor allViolations = mRepeatOffendersDatabaseHelper.getViolationsCursor(mSQLiteDatabase, aState, aPlate);
            StringBuilder mStringBuilder = new StringBuilder();
            mStringBuilder.append(aPlate);
            mStringBuilder.append("-");
            mStringBuilder.append(aState);
            mStringBuilder.append(" has \n");
            for(int loop = 0; loop < numberOfViolations; loop++){
                allViolations.moveToPosition(loop);
                final String violationCode = allViolations.getString(allViolations.getColumnIndex(mRepeatOffenderTable.getColumnName(3)));
                final int warningsCount = mRepeatOffendersDatabaseHelper.getWarningsCount(mSQLiteDatabase, aState, aPlate, violationCode);
                final int violationsCount = mRepeatOffendersDatabaseHelper.getCitationsCount(mSQLiteDatabase, aState, aPlate, violationCode);
                if(warningsCount != 0){
                    mStringBuilder.append(warningsCount);
                    if(warningsCount == 1){
                        mStringBuilder.append(" warn");
                    }
                    else{
                        mStringBuilder.append(" warns");
                    }
                }
                if(violationsCount != 0){
                    if(warningsCount > 0){
                        mStringBuilder.append(", ");
                    }
                    mStringBuilder.append(violationsCount);
                    if(violationsCount == 1){
                        mStringBuilder.append(" cite ");
                    }
                    else{
                        mStringBuilder.append(" cites ");
                    }
                }
                mStringBuilder.append(" for ");
                mStringBuilder.append(violationCode);
                mStringBuilder.append("\n");
            }

            final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();
            mAlertDialog.setTitle("Violations");
            mAlertDialog.setMessage(mStringBuilder.toString());
            mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    mAlertDialog.dismiss();
                }
            });
            mAlertDialog.show();
        }
        else{
            final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();
            mAlertDialog.setTitle("Violations");
            mAlertDialog.setMessage(context.getString(R.string.nothingFoundVios));
            mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    mAlertDialog.dismiss();
                }
            });
            mAlertDialog.show();
        }
    }
}
