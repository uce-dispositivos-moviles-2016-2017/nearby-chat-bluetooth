package com.darwindeveloper.nearby.base_datos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DARWIN on 2/1/2017.
 */

public class ChatDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BChat.db";


    private static final String COMMA_SEP = ",";




    private static final String SQL_CREATE_CHATS =
            "CREATE TABLE " + ChatContract.ChatsEntry.TABLE_NAME + " (" +
                    ChatContract.ChatsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ChatContract.ChatsEntry.YO + " TEXT NOT NULL," +
                    ChatContract.ChatsEntry.EL + " TEXT NOT NULL," +
                    ChatContract.ChatsEntry.FROM + " TEXT NOT NULL," +
                    ChatContract.ChatsEntry.SMS + " TEXT," +
                    ChatContract.ChatsEntry.DATE_TIME + " TEXT NOT NULL )";





    private static final String SQL_DELETE_CHATS =
            "DROP TABLE IF EXISTS " + ChatContract.ChatsEntry.TABLE_NAME;


    public ChatDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(SQL_CREATE_CHATS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over

        sqLiteDatabase.execSQL(SQL_DELETE_CHATS);
        onCreate(sqLiteDatabase);
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}
