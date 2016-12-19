package com.example.dell.testappkotlin


import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.*
import android.widget.Toast
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_popular_movies.*

import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import android.view.MenuInflater




class PopularMovies : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");
        setHasOptionsMenu(true)
        initToolbar()
        return inflater!!.inflate(R.layout.fragment_popular_movies, container, false)
    }

    val TAG="Movie.TAG"


    var movieObjects = ArrayList<MovieObject>()
    var movieAdapter: PopularMoviesAdapter = PopularMoviesAdapter(movieObjects)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        //initialize toolbar
        initToolbar()
        Log.d(TAG, "Toolbar initialized")
        //set layout manager
        val layoutManager = GridLayoutManager(context, 2)
        popularMovieList.layoutManager=layoutManager
        Log.d(TAG, "Layout Manager set")
        //getData from async task
        getData(getType())
        Log.d(TAG, "getData() called")
        //set adapter
        movieAdapter.mContext=context
        popularMovieList.adapter=movieAdapter
        Log.d(TAG, "Adapter set")

    }

    override fun onAttachFragment(childFragment: Fragment?) {
        super.onAttachFragment(childFragment)
    }

    fun getData(s :String) {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            val movieTask = FetchMovieTask()
            activity.toolbar.title= getTitleName()
            movieTask.execute(s)
        } else {
            Toast.makeText(activity.applicationContext, "No Internet Connection!!!!", Toast.LENGTH_SHORT).show()
        }
    }
    fun initToolbar()
    {
        activity.toolbar.showOverflowMenu()
        activity.toolbar.setTitleTextColor(Color.WHITE);
        activity.toolbar.title = getTitleName()
        Log.d(TAG, getTitleName())
    }

    fun getTitleName() : String
    {
        val type = getType()
        Log.d(TAG, type)
        return when(type){
            "popular"->"Popular Movies"
            "upcoming"->"Upcoming Movies"
            "top_rated"->"Top Rated"
            else -> {"Popular Movies"
            }
        }
    }
    fun getType() : String
    {
        val shared = PreferenceManager.getDefaultSharedPreferences(context)
        return shared.getString(resources.getString(R.string.movie_type),"popular")
    }

    inner class FetchMovieTask : AsyncTask<String, Void, ArrayList<MovieObject>>() {

        override fun onPostExecute(myMovieList: ArrayList<MovieObject>?) {
            if (isAdded) {

                if (myMovieList != null) {
                    movieObjects.clear()
                    for (o in myMovieList) {
                        movieObjects.add(o)
                        Log.v(TAG, o.original_title)
                    }
                    Log.v(TAG, "" + movieObjects.size)
                    movieAdapter.notifyDataSetChanged()
                    if (activity != null && activity.toolbar != null)
                        activity.toolbar.title = getTitleName()
                }
            }
        }

        override fun doInBackground(vararg params: String): ArrayList<MovieObject>? {
            if(!isAdded)
                return null
            if (params.isEmpty())
                return null
            var urlConnection: HttpURLConnection? = null
            var reader: BufferedReader? = null
            val movieJsonString: String?
            val MOVIE_API_KEY = resources.getString(R.string.movie_key)
            try {
                val BASE_API_POPULAR = "http://api.themoviedb.org/3/movie"
                val QUERY_API_KEY = "api_key"
                val builtUri = Uri.parse(BASE_API_POPULAR).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(QUERY_API_KEY, MOVIE_API_KEY).build()
                val url = URL(builtUri.toString())

                Log.v(TAG, "Built URI " + builtUri.toString())

                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.connect()
                val inputStream = urlConnection.inputStream
                val buffer = StringBuffer()
                if (inputStream == null) {
                    // Nothing to do.
                    Log.v(TAG, "inputstream null")
                    return null
                }
                reader = BufferedReader(InputStreamReader(inputStream))

                var line: String?
                line = reader.readLine()
                while (line != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n")
                    line = reader.readLine()
                }

                if (buffer.isEmpty()) {
                    // Stream was empty.  No point in parsing.
                    Log.v(TAG, "Stream empty while fetching data")
                    return null
                }
                movieJsonString = buffer.toString()
                Log.v(TAG, movieJsonString)
            } catch (e: IOException) {
                Log.e(TAG, "Error getting data from api: " + e.message)
                return null
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        Log.e(TAG, "Error closing stream", e)
                    }

                }
            }
            try {
                return getMovieDataFromJson(movieJsonString)
            } catch (e: JSONException) {
                Log.e(TAG, "Error parsing JSON: " + e.message)
            }

            return null
        }
    }

    @Throws(JSONException::class)
    internal fun getMovieDataFromJson(movieJsonStr: String?): ArrayList<MovieObject> {
        val myMovies = ArrayList<MovieObject>()

        val POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342"
        val MOVIE_RESULTS = "results"
        val MOVIE_POSTER = "poster_path"
        val MOVIE_ORIGINAL_TITLE = "original_title"
        val MOVIE_OVERVIEW = "overview"
        val MOVIE_LANGUAGE = "original_language"
        val MOVIE_RATING = "vote_average"
        val MOVIE_POPULARITY = "popularity"
        val MOVIE_VOTE_COUNT = "vote_count"
        val MOVIE_ADULT = "adult"
        val MOVIE_DATE = "release_date"

        val movieJson = JSONObject(movieJsonStr)
        val movieArray = movieJson.getJSONArray(MOVIE_RESULTS)
        var i: Int
        i = 0
        while (i < movieArray.length()) {
            val obj = MovieObject()
            val posterPath: String
            val movie = movieArray.getJSONObject(i)

            posterPath = POSTER_BASE_URL + movie.getString(MOVIE_POSTER)

            val title = movie.getString(MOVIE_ORIGINAL_TITLE)
            val overview = movie.getString(MOVIE_OVERVIEW)
            val language = movie.getString(MOVIE_LANGUAGE)
            val rating = movie.getDouble(MOVIE_RATING)
            val popularity = movie.getDouble(MOVIE_POPULARITY)
            val votes = movie.getInt(MOVIE_VOTE_COUNT)
            val b = movie.getBoolean(MOVIE_ADULT)
            val d = movie.getString(MOVIE_DATE)

            obj.posterPath=posterPath
            Log.v(TAG, posterPath)
            obj.original_title=title
            obj.isAdult=b
            obj.overview=overview
            obj.popularity=popularity
            obj.rating=rating
            obj.language=language
            obj.vote_count=votes
            obj.release_date=d
            myMovies.add(obj)
            i++
        }
        return myMovies
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.movie_menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_popular) {
            val shared = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = shared.edit()
            editor.putString(resources.getString(R.string.movie_type), "popular")
            editor.apply()
            editor.commit()
            getData("popular")
        } else if (id == R.id.menu_top_rated) {
            val shared = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = shared.edit()
            editor.putString(resources.getString(R.string.movie_type), "top_rated")
            editor.apply()
            editor.commit()
            getData("top_rated")
        } else if (id == R.id.menu_upcoming) {
            val shared = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = shared.edit()
            editor.putString(resources.getString(R.string.movie_type), "upcoming")
            editor.apply()
            editor.commit()
            getData("upcoming")
        }
        return super.onOptionsItemSelected(item)
    }
}// Required empty public constructor
