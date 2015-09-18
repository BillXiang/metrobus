package com.bill.metrobus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bill.metrobus.UserFragment.MainTable;

/**
 * This class helps open, create, and upgrade the database file.
 */
class DatabaseHelper extends SQLiteOpenHelper {

   private static final String DATABASE_NAME = "loader_throttle.db";
   private static final int DATABASE_VERSION = 2;

   DatabaseHelper(Context context) {

       // calls the super constructor, requesting the default cursor factory.
       super(context, DATABASE_NAME, null, DATABASE_VERSION);
   }

   /**
    *
    * Creates the underlying database with table name and column names taken from the
    * NotePad class.
    */
   @Override
   public void onCreate(SQLiteDatabase db) {
       db.execSQL("CREATE TABLE " + MainTable.TABLE_NAME + " ("
               + MainTable._ID + " INTEGER PRIMARY KEY,"
               + MainTable.COLUMN_NAME_DATA + " TEXT"
               + ");");
   }

   /**
    *
    * Demonstrates that the provider must consider what happens when the
    * underlying datastore is changed. In this sample, the database is upgraded the database
    * by destroying the existing data.
    * A real application should upgrade the database in place.
    */
   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

       // Logs that the database is being upgraded
       Log.w("MetroBus", "Upgrading database from version " + oldVersion + " to "
               + newVersion + ", which will destroy all old data");

       // Kills the table and existing data
       db.execSQL("DROP TABLE IF EXISTS notes");

       // Recreates the database with a new version
       onCreate(db);
   }
}
