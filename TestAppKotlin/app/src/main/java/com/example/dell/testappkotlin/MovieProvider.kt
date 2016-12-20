package com.example.dell.testappkotlin

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri


class MovieProvider : ContentProvider() {
    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = MovieProvider.mOpenHelper?.writableDatabase
        val match = MovieProvider.sUriMatcher.match(uri)
        val deleted: Int

        val customSelection = selection ?: "1"

        when (match) {
            MovieProvider.TASK -> deleted = db!!.delete(MovieContract.MovieEntry.TABLE_NAME, customSelection, selectionArgs)
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        if (deleted > 0) {
            context.contentResolver.notifyChange(uri, null)
        }

        return deleted
    }


    override fun getType(uri: Uri?): String? {
        val match: Int = MovieProvider.sUriMatcher.match(uri)

        when (match) {
            MovieProvider.TASK_WITH_ID -> return MovieContract.MovieEntry.CONTENT_ITEM_TYPE
            MovieProvider.TASK -> return MovieContract.MovieEntry.CONTENT_TYPE
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri? {
        val db = MovieProvider.mOpenHelper?.writableDatabase
        val match: Int = MovieProvider.sUriMatcher.match(uri)
        val insertionUri: Uri?
        val insertedId: Long

        when (match) {
            MovieProvider.TASK -> {
                insertedId = db!!.insert(MovieContract.MovieEntry.TABLE_NAME, null, values)

                insertionUri = if (insertedId > 0) {
                    MovieContract.MovieEntry.buildWithId(insertedId)
                } else {
                    throw SQLException("Failed to insert row into $uri")
                }
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        context.contentResolver.notifyChange(uri, null)
        return insertionUri
    }

    override fun onCreate(): Boolean {
        MovieProvider.mOpenHelper = MovieDbHelper(context)
        return true
    }

    override fun query(uri: Uri?, projection: Array<out String>?,
                       selection: String?, selectionArgs: Array<out String>?,
                       sortOrder: String?): Cursor? {
        val db: SQLiteDatabase = MovieProvider.mOpenHelper?.readableDatabase as SQLiteDatabase
        val match: Int = MovieProvider.sUriMatcher.match(uri)
        val cursor: Cursor?

        when (match) {
            MovieProvider.TASK -> {
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder)
            }
            MovieProvider.TASK_WITH_ID -> {
                val id: Long = MovieContract.MovieEntry.getIdFromUri(uri as Uri)
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, projection,
                        "${MovieContract.MovieEntry._ID} = ?", arrayOf(id.toString()), null, null, sortOrder)
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        cursor?.setNotificationUri(context.contentResolver, uri)
        return cursor
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?,
                        selectionArgs: Array<out String>?): Int {
        val db = MovieProvider.mOpenHelper?.writableDatabase
        val match = MovieProvider.sUriMatcher.match(uri)
        val updated: Int

        when (match) {
            MovieProvider.TASK -> updated = db!!.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs)
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        if (updated > 0) {
            context.contentResolver.notifyChange(uri, null)
        }
        return updated
    }
    companion object {
        val TASK = 102
        val TASK_WITH_ID = 103

        fun createUriMatcher(): UriMatcher {
            val matcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority = MovieContract.CONTENT_AUTHORITY

            matcher.addURI(authority, MovieContract.TASK_PATH, TASK)
            matcher.addURI(authority, "${MovieContract.TASK_PATH}/#", TASK_WITH_ID)

            return matcher
        }

        val sUriMatcher: UriMatcher = createUriMatcher()
        var mOpenHelper: SQLiteOpenHelper? = null
    }
}