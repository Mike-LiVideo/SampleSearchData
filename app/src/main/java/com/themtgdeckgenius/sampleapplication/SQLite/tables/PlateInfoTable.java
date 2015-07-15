package com.themtgdeckgenius.sampleapplication.SQLite.tables;

import java.util.ArrayList;

/**
 * Created by michael.wheeler on 4/1/2015.
 */
public class PlateInfoTable
        extends SQLiteBaseTable{

    public final String WHERE_CLAUSE = this.getColumnName(1) + CS_WHERE_MORE + this.getColumnName(2) + CS_WHERE_END;

    @Override
    String getTableName(){
        return "plate_info_table";
    }

    @Override
    ArrayList<String> setColumnNames(){
        ArrayList<String> columns = new ArrayList<String>();
        columns.add("plate");
        columns.add("state");
        columns.add("vin");
        columns.add("make");
        columns.add("type");
        columns.add("primary_color");
        return columns;
    }

    @Override
    int numberOfUsedColumns(){
        return this.TABLE_COLUMNS.size();
    }
}
