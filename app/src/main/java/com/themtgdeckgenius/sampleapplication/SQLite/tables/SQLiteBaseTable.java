package com.themtgdeckgenius.sampleapplication.SQLite.tables;

import android.provider.BaseColumns;

import com.themtgdeckgenius.sampleapplication.SQLite.SQLiteStatementsInterface;

import java.util.ArrayList;

/**
 * Created by michael.wheeler on 4/2/2015.
 */
public abstract class SQLiteBaseTable
        implements BaseColumns, SQLiteStatementsInterface{

    public final String TABLE_NAME = getTableName();
    protected final ArrayList<String> TABLE_COLUMNS = setColumnNames();
    public String[] PROJECTION = TABLE_COLUMNS.toArray(new String[TABLE_COLUMNS.size()]);

    public String DELETE_TABLE = CS_DELETE + TABLE_NAME + "; ";

    abstract String getTableName();

    abstract ArrayList<String> setColumnNames();

    abstract int numberOfUsedColumns();

    public String getValuesEndString(){
        StringBuilder sb = new StringBuilder();
        sb.append("VALUES (");
        //starting at one so i can leave off the comma before the close
        for (int i = 1; i < numberOfUsedColumns(); i++){
            sb.append("?, ");
        }
        sb.append("?)");
        return sb.toString();
    }

    public String getColumnName(int integer){
        if(integer <= TABLE_COLUMNS.size()){
            return TABLE_COLUMNS.get(--integer);
        }
        else{
            return null;
        }
    }

    public String getCreateTableStatement(){
        StringBuilder sb = new StringBuilder();
        sb.append(CS_CREATE_TABLE).append(TABLE_NAME).append(CS_OPEN_PARENTHESIS);

        for (int i = 1; i < TABLE_COLUMNS.size(); i++){
            sb.append(getColumnName(i)).append(CS_TEXT).append(CS_COMMA);
        }
        sb.append(getColumnName(TABLE_COLUMNS.size())).append(CS_TEXT).append(CS_END_TABLE);

        return sb.toString();
    }

    public String getInsertIntoTableStatement(){
        StringBuilder sb = new StringBuilder();
        sb.append(CS_INSERT).append(TABLE_NAME).append(CS_OPEN_PARENTHESIS);

        for (int i = 1; i < TABLE_COLUMNS.size(); i++){
            sb.append(getColumnName(i)).append(CS_COMMA);
        }
        sb.append(getColumnName(TABLE_COLUMNS.size())).append(CS_CLOSE_PARENTHESIS).append(getValuesEndString());

        return sb.toString();
    }
}
