package com.example.dell.testappkotlin

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.util.Log
import java.util.*

class BeaconProvider : ContentProvider() {
    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = mOpenHelper?.writableDatabase
        val match = sUriMatcher.match(uri)
        val deleted: Int

        val customSelection = selection ?: "1"

        when (match) {
            TASK -> deleted = db!!.delete(BeaconContract.BeaconEntry.TABLE_NAME, customSelection, selectionArgs)
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        if (deleted > 0) {
            context.contentResolver.notifyChange(uri, null)
        }

        return deleted
    }
    override fun getType(uri: Uri?): String? {
        val match: Int = sUriMatcher.match(uri)

        when (match) {
            TASK_WITH_ID -> return BeaconContract.BeaconEntry.CONTENT_ITEM_TYPE
            TASK -> return BeaconContract.BeaconEntry.CONTENT_TYPE
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri? {
        val db = mOpenHelper?.writableDatabase
        val match: Int = sUriMatcher.match(uri)
        val insertionUri: Uri?
        val insertedId: Long

        when (match) {
            TASK -> {
                insertedId = db!!.insert(BeaconContract.BeaconEntry.TABLE_NAME, null, values)

                insertionUri = if (insertedId > 0) {
                    BeaconContract.BeaconEntry.buildWithId(insertedId)
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
        mOpenHelper = BeaconDbHelper(context)
        return true
    }

    override fun query(uri: Uri?, projection: Array<out String>?,
                       selection: String?, selectionArgs: Array<out String>?,
                       sortOrder: String?): Cursor? {
        val db: SQLiteDatabase = mOpenHelper?.readableDatabase as SQLiteDatabase
        val match: Int = sUriMatcher.match(uri)
        val cursor: Cursor?

        when (match) {
            TASK -> {
                cursor = db.query(BeaconContract.BeaconEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder)
            }
            TASK_WITH_ID -> {
                val id: Long = BeaconContract.BeaconEntry.getIdFromUri(uri as Uri)
                cursor = db.query(BeaconContract.BeaconEntry.TABLE_NAME, projection,
                        "${BeaconContract.BeaconEntry._ID} = ?", arrayOf(id.toString()), null, null, sortOrder)
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        cursor?.setNotificationUri(context.contentResolver, uri)
        return cursor
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?,
                        selectionArgs: Array<out String>?): Int {
        val db = mOpenHelper?.writableDatabase
        val match = sUriMatcher.match(uri)
        val updated: Int

        when (match) {
            TASK -> updated = db!!.update(BeaconContract.BeaconEntry.TABLE_NAME, values, selection, selectionArgs)
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        if (updated > 0) {
            context.contentResolver.notifyChange(uri, null)
        }
        return updated
    }

    companion object {
        val TASK = 100
        val TASK_WITH_ID = 101

        fun createUriMatcher(): UriMatcher {
            var matcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority = BeaconContract.CONTENT_AUTHORITY

            matcher.addURI(authority, BeaconContract.TASK_PATH, TASK)
            matcher.addURI(authority, "${BeaconContract.TASK_PATH}/#", TASK_WITH_ID)

            return matcher
        }

        val sUriMatcher: UriMatcher = createUriMatcher()
        var mOpenHelper: SQLiteOpenHelper? = null
    }


    fun getAllBeacons() : ArrayList<MyBeacon>
    {
        val beacons = ArrayList<MyBeacon>()

        val db = mOpenHelper?.writableDatabase
        val BEACON_SELECT_QUERY = String.format("SELECT * FROM %s",
                BeaconContract.BeaconEntry.TABLE_NAME)

        val cursor = db?.rawQuery(BEACON_SELECT_QUERY, null) ?: return beacons
        try {
            if (cursor.moveToFirst()) {
                do {
                    val title=cursor.getString(cursor.getColumnIndex(BeaconContract.BeaconEntry.COL_TITLE))
                    val url = cursor.getString(cursor.getColumnIndex(BeaconContract.BeaconEntry.COL_DESCRIPTION))
                    beacons.add(MyBeacon(title, url, 0.0))
                }while (cursor.moveToFirst())
            }
        } catch (e: Exception) {
            Log.d("Movie.TAG", "Error while trying to get alarms from database");
        } finally {
            if (!cursor.isClosed) {
                cursor.close()
            }
        }
        return beacons
    }
}