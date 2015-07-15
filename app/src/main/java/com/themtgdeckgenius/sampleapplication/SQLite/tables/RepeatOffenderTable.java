package com.themtgdeckgenius.sampleapplication.SQLite.tables;

import java.util.ArrayList;

/**
 * Created by michael.wheeler on 4/1/2015.
 */
public class RepeatOffenderTable
        extends SQLiteBaseTable{

    public final String SPECIFIC_VIOLATION_WHERE_CLAUSE = this.getColumnName(1) + CS_WHERE_MORE + this.getColumnName(2) + CS_WHERE_MORE + this.getColumnName(3) + CS_WHERE_MORE + this.getColumnName(5) + CS_WHERE_END;
    public final String ALL_VIOLATIONS_WHERE_CLAUSE = this.getColumnName(1) + CS_WHERE_MORE + this.getColumnName(2) + CS_WHERE_END;

    @Override
    String getTableName(){
        return "repeat_offender";
    }

    @Override
    ArrayList<String> setColumnNames(){
        ArrayList<String> columns = new ArrayList<String>();
        columns.add("state");
        columns.add("plate");
        columns.add("violation");
        columns.add("amount");
        columns.add("is_warning");
        return columns;
    }

    @Override
    int numberOfUsedColumns(){
        return this.TABLE_COLUMNS.size();
    }
}
