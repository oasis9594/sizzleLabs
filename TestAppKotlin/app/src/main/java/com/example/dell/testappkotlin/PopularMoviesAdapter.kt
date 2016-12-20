package com.example.dell.testappkotlin


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.custom_movie_row.view.*

import java.util.ArrayList

class PopularMoviesAdapter(internal var myMovies: ArrayList<MovieObject>) : RecyclerView.Adapter<PopularMoviesAdapter.ViewHolder>() {
    var mContext: Context?=null
    val TAG="Movie.TAG"

    class ViewHolder(itemView: View, internal var mlistener: ViewHolder.MyClickHandler) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mlistener.showMovieDetails(v, adapterPosition)
        }

        interface MyClickHandler {
            fun showMovieDetails(v: View, pos: Int)
        }
        fun setUI(movieObject: MovieObject)
        {
            with(itemView)
            {
                Picasso.with(movie_poster_image.context).load(movieObject.posterPath).error(R.drawable.noposter).into(movie_poster_image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.v(TAG, "onCreateViewHolder1");
        val customView = LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_movie_row, parent, false)
        val vh = ViewHolder(customView, object : ViewHolder.MyClickHandler {

            override fun showMovieDetails(v: View, pos: Int) {
                //Show detailed activity of movie
                if( mContext!=null)
                {
                    val movie = myMovies.get(pos)
                    mContext!!.startActivity(Intent(mContext, MovieDetails::class.java).putExtra("id", movie.id))
                }
            }
        })
        Log.v(TAG, "onCreateViewHolder2");
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.v(TAG, "onBindViewHolder1")
        val movieObject : MovieObject = myMovies.get(position)
        holder.setUI(movieObject)
        Log.v(TAG, "onBindViewHolder2")
    }

    override fun getItemCount(): Int {
        return myMovies.size
    }

}
