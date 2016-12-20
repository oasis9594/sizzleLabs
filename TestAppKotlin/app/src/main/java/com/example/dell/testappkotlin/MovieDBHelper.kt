package com.example.dell.testappkotlin

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MovieDbHelper(context: Context?) : SQLiteOpenHelper(context, MovieDbHelper.DATABASE_NAME, null, MovieDbHelper.DATABASE_VERSION) {

    companion object {
        val DATABASE_NAME = "movie.db"
        val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTaskTable = "CREATE TABLE ${MovieContract.MovieEntry.TABLE_NAME} (" +
                "${MovieContract.MovieEntry._ID} INTEGER PRIMARY KEY, " +
                "${MovieContract.MovieEntry.COL_TITLE} TEXT NOT NULL, " +
                "${MovieContract.MovieEntry.COL_OVERVIEW} TEXT NOT NULL, " +
                "${MovieContract.MovieEntry.COL_POPULARITY} INTEGER, " +
                "${MovieContract.MovieEntry.COL_RATING} REAL, " +
                "${MovieContract.MovieEntry.COL_VOTE_COUNT} INTEGER, " +
                "${MovieContract.MovieEntry.COL_LANGUAGE} TEXT , " +
                "${MovieContract.MovieEntry.COL_POSTER} TEXT, " +
                "${MovieContract.MovieEntry.COL_ADULT} INTEGER, " +
                "${MovieContract.MovieEntry.COL_RELEASE_DATE} TEXT, " +
                " UNIQUE (${MovieContract.MovieEntry.COL_TITLE}) ON CONFLICT REPLACE" +
                ");"

        db?.execSQL(createTaskTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${MovieContract.MovieEntry.TABLE_NAME}")
        onCreate(db)
    }
}