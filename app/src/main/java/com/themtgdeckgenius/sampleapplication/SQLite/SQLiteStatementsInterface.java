package com.themtgdeckgenius.sampleapplication.SQLite;

/**
 * Created by michael.wheeler on 4/13/2015.
 */
public interface SQLiteStatementsInterface{

    String CS_TEXT = " TEXT NOT NULL";
    String CS_COMMA = ",";
    String CS_CREATE_TABLE = "CREATE TABLE ";
    String CS_OPEN_PARENTHESIS = " ( ";
    String CS_CLOSE_PARENTHESIS = " ) ";
    String CS_END_TABLE = " );";
    String CS_DELETE = "DROP TABLE IF EXISTS ";
    String CS_WHERE_END = " = ? ";
    String CS_WHERE_MORE = " = ? AND ";
    String CS_INSERT = "INSERT INTO ";
}
