package com.example.dell.testappkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_movie_details.*

class MovieDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        val id : Long= intent.extras.getLong("id")
        //Find uri associated with this id
        val uri = MovieContract.MovieEntry.buildWithId(id)

        val mProjection = arrayOf(
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COL_TITLE,
                MovieContract.MovieEntry.COL_OVERVIEW,
                MovieContract.MovieEntry.COL_POSTER,
                MovieContract.MovieEntry.COL_RATING,
                MovieContract.MovieEntry.COL_RELEASE_DATE,
                MovieContract.MovieEntry.COL_VOTE_COUNT
        )

        val cursor = contentResolver.query(uri, mProjection, null, null, null)

        if (cursor.moveToFirst())
        {
            detail_date.text = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COL_RELEASE_DATE))
            detail_rating.text = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COL_RATING)).toString()
            detail_overview.text = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COL_OVERVIEW))
            detail_votes.text = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COL_VOTE_COUNT)).toString()

            supportActionBar?.title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COL_TITLE))

            val path = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COL_POSTER))
            Picasso.with(detail_poster_image.context).load(path).error(R.drawable.noposter).into(detail_poster_image)
        }
    }
}
