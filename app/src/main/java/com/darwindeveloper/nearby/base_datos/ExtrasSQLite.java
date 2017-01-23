package com.darwindeveloper.nearby.base_datos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DARWIN on 31/12/2016.
 */

public class ExtrasSQLite {


    public static long insertNewSmsChat(SQLiteDatabase db, String from, String yo, String el, String sms) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ChatContract.ChatsEntry.YO, yo);
        values.put(ChatContract.ChatsEntry.EL, el);
        values.put(ChatContract.ChatsEntry.FROM, from);
        values.put(ChatContract.ChatsEntry.SMS, sms);
        values.put(ChatContract.ChatsEntry.DATE_TIME, getDateTime());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(ChatContract.ChatsEntry.TABLE_NAME, null, values);
        return newRowId;

    }


    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    /**
     * obtinene un cursor con los mensajes del chat para una conversacion
     */
    public static Cursor getChat(SQLiteDatabase db, String from_device_adress, String to_device_adress) {

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ChatContract.ChatsEntry._ID,
                ChatContract.ChatsEntry.YO,
                ChatContract.ChatsEntry.EL,
                ChatContract.ChatsEntry.FROM,
                ChatContract.ChatsEntry.SMS,
                ChatContract.ChatsEntry.DATE_TIME,
        };

        // Filter results WHERE "adress" = 'to_device_adress'
        String selection = ChatContract.ChatsEntry.YO + " = ? AND " + ChatContract.ChatsEntry.EL + " = ?";
        String[] selectionArgs = {from_device_adress, to_device_adress};


        return db.query(
                ChatContract.ChatsEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

    }


    public static boolean delete_sms(SQLiteDatabase db, String ID) {
        // Define 'where' part of query.
        String selection = ChatContract.ChatsEntry._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {ID};
        // Issue SQL statement.
        int result = db.delete(ChatContract.ChatsEntry.TABLE_NAME, selection, selectionArgs);
        if (result == -1) {
            return false;
        }
        return true;
    }

}
