package com.darwindeveloper.nearby.base_datos;

import android.provider.BaseColumns;

/**
 * Created by DARWIN on 2/1/2017.
 */

public class ChatContract {

    private ChatContract() {
    }

    public static class ChatsEntry implements BaseColumns {
        public static final String TABLE_NAME = "chats";
        public static final String YO = "yo";
        public static final String EL = "el";
        public static final String SMS = "sms";
        public static final String DATE_TIME = "datetime";
        public static final String FROM = "send_from";
    }

}
