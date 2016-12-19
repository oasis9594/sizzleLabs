package com.example.dell.testappkotlin

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BeaconDbHelper(context: Context?) : SQLiteOpenHelper(context, BeaconDbHelper.DATABASE_NAME, null, BeaconDbHelper.DATABASE_VERSION) {

    companion object {
        val DATABASE_NAME = "beacon.db"
        val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTaskTable = "CREATE TABLE ${BeaconContract.BeaconEntry.TABLE_NAME} (" +
                "${BeaconContract.BeaconEntry._ID} INTEGER PRIMARY KEY, " +
                "${BeaconContract.BeaconEntry.COL_TITLE} TEXT NOT NULL, " +
                "${BeaconContract.BeaconEntry.COL_DESCRIPTION} TEXT NOT NULL, " +
                " UNIQUE (${BeaconContract.BeaconEntry.COL_TITLE}) ON CONFLICT REPLACE" +
                ");"

        db?.execSQL(createTaskTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${BeaconContract.BeaconEntry.TABLE_NAME}")
        onCreate(db)
    }
}