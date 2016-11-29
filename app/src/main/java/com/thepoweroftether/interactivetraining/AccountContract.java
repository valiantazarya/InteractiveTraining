package com.thepoweroftether.interactivetraining;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by ValiantAzarya on 29/11/2016.
 */
public final class AccountContract {
    /**
     * Created by ValiantAzarya on 25/09/2016.
     */
    public static final String SQL_CREATE_ACCOUNTS = String.format(
            "CREATE TABLE %s(%s, %s, %s, %s, %s, %s, %s)",
            AccountEntry.TABLE_NAME,
            String.format("%s INTEGER PRIMARY KEY AUTOINCREMENT", AccountEntry._ID),
            String.format("%s VARCHAR(40)", AccountEntry.COLUMN_NAME_USERNAME),
            String.format("%s VARCHAR(40)", AccountEntry.COLUMN_NAME_PASSWORD),
            String.format("%s VARCHAR(40)", AccountEntry.COLUMN_NAME_FULLNAME),
            String.format("%s TEXT NOT NULL", AccountEntry.COLUMN_NAME_ADDRESS),
            String.format("%s VARCHAR(40)", AccountEntry.COLUMN_NAME_EMAIL),
            String.format("%s TEXT NOT NULL", AccountEntry.COLUMN_NAME_BIRTHDATE)
    );

    public static final String SQL_DELETE_ACCOUNTS = String.format(
            "DROP TABLE IF EXISTS %s",
            AccountEntry.TABLE_NAME
    );

    //Mencegah constructor dipanggil
    private AccountContract() {}

    public static class AccountEntry implements BaseColumns {
        public static final String TABLE_NAME = "account";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_FULLNAME = "fullname";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_BIRTHDATE = "birthdate";
    }

    public static class AccountDbHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "members.db";
        //public AccountDdHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        public AccountDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ACCOUNTS);

            ContentValues values = new ContentValues();
            values.put(AccountEntry.COLUMN_NAME_FULLNAME, "Valiant Azarya");
            values.put(AccountEntry.COLUMN_NAME_USERNAME, "admin");
            values.put(AccountEntry.COLUMN_NAME_PASSWORD, "admin");
            values.put(AccountEntry.COLUMN_NAME_ADDRESS, "-");
            values.put(AccountEntry.COLUMN_NAME_EMAIL, "valiant.azarya@gmail.com");
            values.put(AccountEntry.COLUMN_NAME_BIRTHDATE, "Date");
            db.insert(AccountEntry.TABLE_NAME, null, values);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ACCOUNTS);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //super.onDowngrade(db, oldVersion, newVersion);
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
