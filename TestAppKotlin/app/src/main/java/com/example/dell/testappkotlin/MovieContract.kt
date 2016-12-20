package com.example.dell.testappkotlin

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

/**
 * Created by dell on 12/20/2016.
 */
object MovieContract {
    val CONTENT_AUTHORITY = "com.example.dell.testappkotlin2"
    val BASE_CONTENT_URI: Uri = Uri.parse("content://${CONTENT_AUTHORITY}")
    val TASK_PATH = MovieEntry.TABLE_NAME

    object MovieEntry {
        val CONTENT_URI: Uri = BASE_CONTENT_URI.buildUpon().appendPath(TASK_PATH).build()
        val CONTENT_TYPE = "${ContentResolver.CURSOR_DIR_BASE_TYPE}/$CONTENT_AUTHORITY/$TASK_PATH"
        val CONTENT_ITEM_TYPE = "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/$CONTENT_AUTHORITY/$TASK_PATH"

        val TABLE_NAME = "movie"

        val _ID = BaseColumns._ID
        val COL_TITLE = "title"
        val COL_OVERVIEW = "overview"
        val COL_LANGUAGE = "language"
        val COL_RATING = "rating"
        val COL_POSTER = "poster_path"
        val COL_POPULARITY = "popularity"
        val COL_VOTE_COUNT = "vote_count"
        val COL_ADULT = "adult"
        val COL_RELEASE_DATE = "release_date"

        fun buildWithId(id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, id)
        }


        fun getIdFromUri(uri: Uri): Long {
            return ContentUris.parseId(uri)
        }
    }
}